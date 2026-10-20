package com.codex.listenandwrite.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codex.listenandwrite.data.entity.TextbookEntity
import com.codex.listenandwrite.data.entity.UnitEntity
import com.codex.listenandwrite.data.local.PreferencesManager
import com.codex.listenandwrite.data.repository.WordRepository
import com.codex.listenandwrite.domain.model.DictationConfig
import com.codex.listenandwrite.domain.model.DictationMode
import com.codex.listenandwrite.domain.model.SpeechAccent
import com.codex.listenandwrite.domain.model.VoiceSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DictationSettingsViewModel(
    private val wordRepository: WordRepository,
    private val preferencesManager: PreferencesManager,
    private val textbookId: String,
    private val unitIds: List<String>
) : ViewModel() {

    private val _config = MutableStateFlow(DictationConfig())
    val config: StateFlow<DictationConfig> = _config.asStateFlow()

    private val _previewWords = MutableStateFlow<List<String>>(emptyList())
    val previewWords: StateFlow<List<String>> = _previewWords.asStateFlow()

    private val _showPreviewDialog = MutableStateFlow(false)
    val showPreviewDialog: StateFlow<Boolean> = _showPreviewDialog.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val textbook = wordRepository.getTextbookById(textbookId)
            val units = wordRepository.getUnitsListForTextbook(textbookId).filter { unitIds.contains(it.id) }
            val words = wordRepository.getWordsForUnits(unitIds)

            val totalWords = words.size
            _previewWords.value = words.map { "${it.word} (${it.translation})" }

            val voiceSrcStr = try { preferencesManager.voiceSource.first() } catch (_: Exception) { "REAL_HUMAN" }
            val voiceSrc = if (voiceSrcStr == "SYSTEM_TTS") VoiceSource.SYSTEM_TTS else VoiceSource.REAL_HUMAN

            val accentStr = try { preferencesManager.speechAccent.first() } catch (_: Exception) { "US" }
            val accent = if (accentStr == "UK") SpeechAccent.UK else SpeechAccent.US

            val rate = try { preferencesManager.speechRate.first() } catch (_: Exception) { 1.0f }
            val rpt = try { preferencesManager.repeatCount.first() } catch (_: Exception) { 2 }
            val interval = try { preferencesManager.intervalSeconds.first() } catch (_: Exception) { 5 }
            val readZh = try { preferencesManager.readChinese.first() } catch (_: Exception) { false }

            _config.value = _config.value.copy(
                textbookId = textbookId,
                textbookTitle = textbook?.title ?: "三年级 下册",
                selectedUnitIds = unitIds,
                unitRangeLabel = "${units.size} 个单元",
                totalAvailableWords = totalWords,
                wordCount = minOf(20, totalWords),
                voiceSource = voiceSrc,
                speechAccent = accent,
                speechRate = rate,
                repeatCount = rpt,
                intervalSeconds = interval,
                readChinese = readZh
            )
        }
    }

    fun setWordCount(count: Int) {
        val limited = minOf(count, _config.value.totalAvailableWords)
        _config.value = _config.value.copy(wordCount = limited)
    }

    fun setRepeatCount(count: Int) {
        if (count in 1..5) {
            _config.value = _config.value.copy(repeatCount = count)
        }
    }

    fun setIntervalSeconds(seconds: Int) {
        if (seconds in 2..15) {
            _config.value = _config.value.copy(intervalSeconds = seconds)
        }
    }

    fun setSpeechRate(rate: Float) {
        _config.value = _config.value.copy(speechRate = rate)
    }

    fun setSpeechAccent(accent: SpeechAccent) {
        _config.value = _config.value.copy(speechAccent = accent)
    }

    fun setVoiceSource(source: VoiceSource) {
        _config.value = _config.value.copy(voiceSource = source)
        viewModelScope.launch {
            preferencesManager.setVoiceSource(source.name)
        }
    }

    fun setReadChinese(read: Boolean) {
        _config.value = _config.value.copy(readChinese = read)
        viewModelScope.launch {
            preferencesManager.setReadChinese(read)
        }
    }

    fun setDictationMode(mode: DictationMode) {
        _config.value = _config.value.copy(mode = mode)
    }

    fun togglePreviewDialog(show: Boolean) {
        _showPreviewDialog.value = show
    }

    class Factory(
        private val wordRepository: WordRepository,
        private val preferencesManager: PreferencesManager,
        private val textbookId: String,
        private val unitIds: List<String>
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DictationSettingsViewModel(
                wordRepository,
                preferencesManager,
                textbookId,
                unitIds
            ) as T
        }
    }
}
