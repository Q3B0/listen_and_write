package com.codex.listenandwrite.tts

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.codex.listenandwrite.domain.model.SpeechAccent
import com.codex.listenandwrite.domain.model.VoiceSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class TtsEngineStatus {
    UNINITIALIZED,
    INITIALIZING,
    READY,
    SPEAKING,
    ERROR
}

data class EngineInfo(
    val name: String,
    val label: String
)

class TtsManager private constructor() : TextToSpeech.OnInitListener {

    companion object {
        @Volatile
        private var instance: TtsManager? = null

        fun getInstance(): TtsManager {
            return instance ?: synchronized(this) {
                instance ?: TtsManager().also { instance = it }
            }
        }
    }

    private var appContext: Context? = null
    private var textToSpeech: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    private val _status = MutableStateFlow(TtsEngineStatus.UNINITIALIZED)
    val status: StateFlow<TtsEngineStatus> = _status.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _installedEngines = MutableStateFlow<List<EngineInfo>>(emptyList())
    val installedEngines: StateFlow<List<EngineInfo>> = _installedEngines.asStateFlow()

    private val _currentEngineName = MutableStateFlow<String>("系统默认")
    val currentEngineName: StateFlow<String> = _currentEngineName.asStateFlow()

    var voiceSource: VoiceSource = VoiceSource.REAL_HUMAN
    private var currentAccent: SpeechAccent = SpeechAccent.US
    private var currentRate: Float = 1.0f
    private var currentPitch: Float = 1.05f

    private val callbackMap = ConcurrentHashMap<String, UtteranceCallback>()
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    data class UtteranceCallback(
        val onStart: (() -> Unit)? = null,
        val onDone: (() -> Unit)? = null,
        val onError: ((String) -> Unit)? = null
    )

    fun initialize(context: Context, enginePackage: String? = null) {
        appContext = context.applicationContext
        if (textToSpeech != null && _status.value == TtsEngineStatus.READY && enginePackage == null) return
        _status.value = TtsEngineStatus.INITIALIZING
        _errorMessage.value = null

        try {
            textToSpeech?.shutdown()
            textToSpeech = if (enginePackage != null) {
                TextToSpeech(context.applicationContext, this, enginePackage)
            } else {
                TextToSpeech(context.applicationContext, this)
            }
        } catch (e: Exception) {
            _status.value = TtsEngineStatus.ERROR
            _errorMessage.value = "初始化失败: ${e.message}"
        }
    }

    override fun onInit(initStatus: Int) {
        if (initStatus == TextToSpeech.SUCCESS) {
            setupTtsEngine()
        } else {
            _status.value = TtsEngineStatus.ERROR
            _errorMessage.value = "TTS 引擎初始化失败，代码：$initStatus"
        }
    }

    private fun setupTtsEngine() {
        val tts = textToSpeech ?: return

        // Fetch installed engines
        try {
            val engines = tts.engines.map { EngineInfo(it.name, it.label) }
            _installedEngines.value = engines
            val currentEng = tts.defaultEngine
            _currentEngineName.value = engines.find { it.name == currentEng }?.label ?: currentEng ?: "系统默认"
        } catch (_: Exception) {}

        // Configure audio attributes for clear speech
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .build()
            tts.setAudioAttributes(audioAttributes)
        } catch (_: Exception) {}

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _status.value = TtsEngineStatus.SPEAKING
                utteranceId?.let { id ->
                    callbackMap[id]?.onStart?.let { cb ->
                        mainScope.launch { cb() }
                    }
                }
            }

            override fun onDone(utteranceId: String?) {
                _status.value = TtsEngineStatus.READY
                utteranceId?.let { id ->
                    callbackMap.remove(id)?.onDone?.let { cb ->
                        mainScope.launch { cb() }
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                handleSpeechError(utteranceId, "朗读异常")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                handleSpeechError(utteranceId, "朗读失败 (代码 $errorCode)")
            }
        })

        applyLanguageAndRate()
        _status.value = TtsEngineStatus.READY
    }

    private fun handleSpeechError(utteranceId: String?, message: String) {
        _status.value = TtsEngineStatus.ERROR
        _errorMessage.value = message
        utteranceId?.let { id ->
            callbackMap.remove(id)?.onError?.let { cb ->
                mainScope.launch { cb(message) }
            }
        }
    }

    fun setAccent(accent: SpeechAccent) {
        currentAccent = accent
        applyLanguageAndRate()
    }

    fun setSpeechRate(rate: Float) {
        currentRate = rate
        applyLanguageAndRate()
    }

    fun setPitch(pitch: Float) {
        currentPitch = pitch
        applyLanguageAndRate()
    }

    private fun applyLanguageAndRate() {
        val tts = textToSpeech ?: return

        val targetLocale = when (currentAccent) {
            SpeechAccent.UK -> Locale.UK
            SpeechAccent.US -> Locale.US
        }

        // Try to pick the best high-quality Voice from available voices
        try {
            val voices = tts.voices
            if (!voices.isNullOrEmpty()) {
                val matchingVoices = voices.filter { v ->
                    v.locale.language.equals(targetLocale.language, ignoreCase = true) &&
                    (v.locale.country.isEmpty() || v.locale.country.equals(targetLocale.country, ignoreCase = true)) &&
                    !v.features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
                }
                val bestVoice = matchingVoices.maxByOrNull { v ->
                    var score = v.quality
                    val name = v.name.lowercase()
                    if (name.contains("neural") || name.contains("natural") || name.contains("wavenet")) score += 1000
                    if (name.contains("local") || !v.isNetworkConnectionRequired) score += 200
                    if (name.contains("female") || name.contains("en-us-x") || name.contains("en-gb-x")) score += 100
                    score
                }
                if (bestVoice != null) {
                    tts.voice = bestVoice
                } else {
                    tts.language = targetLocale
                }
            } else {
                tts.language = targetLocale
            }
        } catch (_: Exception) {
            tts.language = targetLocale
        }

        tts.setSpeechRate(currentRate)
        tts.setPitch(currentPitch)
    }

    /**
     * Main entry point to speak a word or phrase.
     * If voiceSource == REAL_HUMAN, plays real human pronunciation MP3 (with offline cache).
     * If offline and uncached, or if voiceSource == SYSTEM_TTS, speaks via local TTS.
     */
    fun speak(
        text: String,
        utteranceId: String = "utt_${System.currentTimeMillis()}",
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (voiceSource == VoiceSource.REAL_HUMAN) {
            speakRealHuman(
                text = text,
                utteranceId = utteranceId,
                onStart = onStart,
                onDone = onDone,
                onError = {
                    // Transparent fallback to local TTS if real human voice is unavailable
                    speakViaTts(text, utteranceId, onStart, onDone, onError)
                }
            )
        } else {
            speakViaTts(text, utteranceId, onStart, onDone, onError)
        }
    }

    /**
     * Play native human speaker audio (with persistent offline cache).
     */
    private fun speakRealHuman(
        text: String,
        utteranceId: String,
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val cleanWord = text.trim().lowercase().replace(Regex("[^a-zA-Z0-9' -]"), "")
        if (cleanWord.isBlank()) {
            onError?.invoke("单词为空")
            return
        }

        ioScope.launch {
            val audioFile = getOrDownloadAudioFile(cleanWord, currentAccent)
            if (audioFile != null && audioFile.exists() && audioFile.length() > 500) {
                withContext(Dispatchers.Main) {
                    playAudioFile(audioFile, onStart, onDone, onError)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onError?.invoke("真人音频下载失败")
                }
            }
        }
    }

    private fun playAudioFile(
        file: File,
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        stopMediaPlayer()
        try {
            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener { mp ->
                    _status.value = TtsEngineStatus.SPEAKING
                    onStart?.invoke()
                    mp.start()
                }
                setOnCompletionListener { mp ->
                    _status.value = TtsEngineStatus.READY
                    onDone?.invoke()
                    stopMediaPlayer()
                }
                setOnErrorListener { _, what, extra ->
                    _status.value = TtsEngineStatus.ERROR
                    onError?.invoke("音频播放失败 ($what, $extra)")
                    stopMediaPlayer()
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            onError?.invoke("初始化播放失败: ${e.message}")
        }
    }

    private fun stopMediaPlayer() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        } catch (_: Exception) {}
    }

    /**
     * Download or retrieve audio file from cache.
     */
    private fun getOrDownloadAudioFile(word: String, accent: SpeechAccent): File? {
        val context = appContext ?: return null
        val cacheSubDir = File(context.filesDir, "audio_cache/${accent.name.lowercase()}").apply { mkdirs() }
        val targetFile = File(cacheSubDir, "$word.mp3")

        if (targetFile.exists() && targetFile.length() > 500) {
            return targetFile
        }

        // 1. Check if pre-bundled in assets (built-in standard native speaker voice)
        val normalized = word.trim().lowercase()
        val candidatePaths = listOf(
            "audio/${accent.name.lowercase()}/$normalized.mp3",
            "audio/${accent.name.lowercase()}/${normalized.replace(' ', '_')}.mp3"
        )
        for (assetPath in candidatePaths) {
            try {
                context.assets.open(assetPath).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (targetFile.exists() && targetFile.length() > 500) {
                    return targetFile
                }
            } catch (_: Exception) {}
        }

        // 2. Download from dictionary audio CDN
        val type = if (accent == SpeechAccent.UK) 1 else 2
        val encodedWord = try { URLEncoder.encode(word, "UTF-8") } catch (_: Exception) { word }
        val urlString = "https://dict.youdao.com/dictvoice?type=$type&audio=$encodedWord"

        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val tempFile = File(cacheSubDir, "$word.mp3.tmp")
                connection.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (tempFile.length() > 500) {
                    tempFile.renameTo(targetFile)
                    return targetFile
                } else {
                    tempFile.delete()
                }
            }
        } catch (_: Exception) {
            // Offline or network error
        } finally {
            connection?.disconnect()
        }

        // 3. Fallback to https://dictionaryapi.dev (Wiktionary audio API)
        val dictApiAudio = tryDownloadFromDictionaryApi(word, accent, cacheSubDir, targetFile)
        if (dictApiAudio != null) {
            return dictApiAudio
        }

        return null
    }

    /**
     * Fallback audio retriever from https://dictionaryapi.dev/
     */
    private fun tryDownloadFromDictionaryApi(
        word: String,
        accent: SpeechAccent,
        cacheSubDir: File,
        targetFile: File
    ): File? {
        var conn: HttpURLConnection? = null
        try {
            val encodedWord = try { URLEncoder.encode(word, "UTF-8") } catch (_: Exception) { word }
            val apiUrl = URL("https://api.dictionaryapi.dev/api/v2/entries/en/$encodedWord")
            conn = (apiUrl.openConnection() as HttpURLConnection).apply {
                connectTimeout = 3000
                readTimeout = 3000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return null

            val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
            val entries: List<Map<String, Any>> = gson.fromJson(jsonString, listType) ?: return null

            // Find best matching audio URL
            var matchedAudioUrl: String? = null
            val accentTag = if (accent == SpeechAccent.UK) "-uk" else "-us"

            for (entry in entries) {
                @Suppress("UNCHECKED_CAST")
                val phonetics = entry["phonetics"] as? List<Map<String, Any>> ?: continue
                for (p in phonetics) {
                    val audio = p["audio"] as? String ?: continue
                    if (audio.isNotBlank()) {
                        if (audio.contains(accentTag, ignoreCase = true)) {
                            matchedAudioUrl = audio
                            break
                        } else if (matchedAudioUrl == null) {
                            matchedAudioUrl = audio
                        }
                    }
                }
                if (matchedAudioUrl?.contains(accentTag, ignoreCase = true) == true) break
            }

            if (matchedAudioUrl.isNullOrBlank()) return null

            // Download audio from matchedAudioUrl
            var audioConn: HttpURLConnection? = null
            try {
                val audioUrl = URL(matchedAudioUrl)
                audioConn = (audioUrl.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 3000
                    readTimeout = 3000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "Mozilla/5.0")
                }
                if (audioConn.responseCode == HttpURLConnection.HTTP_OK) {
                    val tempFile = File(cacheSubDir, "$word.mp3.tmp")
                    audioConn.inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    if (tempFile.length() > 500) {
                        tempFile.renameTo(targetFile)
                        return targetFile
                    } else {
                        tempFile.delete()
                    }
                }
            } finally {
                audioConn?.disconnect()
            }
        } catch (_: Exception) {
            // Network timeout or parsing error
        } finally {
            conn?.disconnect()
        }
        return null
    }

    /**
     * Speak via local TextToSpeech engine.
     */
    fun speakViaTts(
        text: String,
        utteranceId: String = "utt_${System.currentTimeMillis()}",
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val tts = textToSpeech
        if (tts == null || _status.value == TtsEngineStatus.UNINITIALIZED || _status.value == TtsEngineStatus.ERROR) {
            onError?.invoke("TTS 未就绪")
            return
        }

        stopMediaPlayer()
        callbackMap[utteranceId] = UtteranceCallback(onStart, onDone, onError)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            callbackMap.remove(utteranceId)
            onError?.invoke("提交发音失败")
        }
    }

    /**
     * Speak Chinese translation.
     */
    fun speakChinese(
        text: String,
        utteranceId: String = "zh_${System.currentTimeMillis()}",
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val tts = textToSpeech ?: run {
            onError?.invoke("TTS 未就绪")
            return
        }
        stopMediaPlayer()

        tts.language = Locale.CHINESE
        tts.setPitch(1.0f)
        tts.setSpeechRate(1.0f)

        callbackMap[utteranceId] = UtteranceCallback(
            onStart = onStart,
            onDone = {
                applyLanguageAndRate() // Restore English accent/rate
                onDone?.invoke()
            },
            onError = { err ->
                applyLanguageAndRate()
                onError?.invoke(err)
            }
        )

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    /**
     * Pre-cache a list of words for offline dictation.
     */
    suspend fun preCacheWords(
        words: List<String>,
        accent: SpeechAccent,
        onProgress: (done: Int, total: Int) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        var count = 0
        val total = words.size
        words.forEachIndexed { index, word ->
            val cleanWord = word.trim().lowercase().replace(Regex("[^a-zA-Z0-9' -]"), "")
            if (cleanWord.isNotBlank()) {
                val file = getOrDownloadAudioFile(cleanWord, accent)
                if (file != null && file.exists()) {
                    count++
                }
            }
            withContext(Dispatchers.Main) {
                onProgress(index + 1, total)
            }
        }
        count
    }

    /**
     * Get offline audio cache statistics.
     */
    fun getCachedWordsCount(): Int {
        val context = appContext ?: return 0
        val baseDir = File(context.filesDir, "audio_cache")
        if (!baseDir.exists()) return 0
        return baseDir.walkTopDown().filter { it.isFile && it.extension == "mp3" }.count()
    }

    fun getCacheSizeMB(): Float {
        val context = appContext ?: return 0f
        val baseDir = File(context.filesDir, "audio_cache")
        if (!baseDir.exists()) return 0f
        val bytes = baseDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        return bytes / (1024f * 1024f)
    }

    fun clearCache(): Boolean {
        val context = appContext ?: return false
        val baseDir = File(context.filesDir, "audio_cache")
        return baseDir.deleteRecursively()
    }

    fun openSystemTtsSettings(context: Context) {
        try {
            val intent = Intent("com.android.settings.TTS_SETTINGS").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun stop() {
        try {
            stopMediaPlayer()
            textToSpeech?.stop()
            callbackMap.clear()
            if (_status.value == TtsEngineStatus.SPEAKING) {
                _status.value = TtsEngineStatus.READY
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            stop()
            textToSpeech?.shutdown()
            textToSpeech = null
            _status.value = TtsEngineStatus.UNINITIALIZED
        } catch (_: Exception) {}
    }
}
