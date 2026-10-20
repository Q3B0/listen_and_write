package com.codex.listenandwrite.domain.model

enum class DictationMode(val title: String, val subtitle: String) {
    PAPER("纸笔听写", "只播放声音 在纸上写"),
    INPUT("屏幕拼写", "输入单词 自动判分")
}

enum class DictationState {
    IDLE,
    PREPARING,
    SPEAKING,
    WAITING,
    PAUSED,
    ANSWERING,
    FEEDBACK,
    COMPLETED,
    ERROR
}

enum class SpeechAccent(val code: String, val label: String) {
    US("US", "美式英语"),
    UK("UK", "英式英语")
}

enum class VoiceSource(val label: String, val subtitle: String) {
    REAL_HUMAN("真人原声", "纯正母语发音 · 离线缓存"),
    SYSTEM_TTS("系统合成", "本地 TTS 语音引擎")
}

data class DictationConfig(
    val textbookId: String = "flyabc_3a",
    val textbookTitle: String = "人教版 PEP 小学英语 · 三年级上册 (2024新版)",
    val selectedUnitIds: List<String> = emptyList(),
    val unitRangeLabel: String = "3 个单元",
    val totalAvailableWords: Int = 98,
    val wordCount: Int = 20,
    val isRandom: Boolean = true,
    val repeatCount: Int = 2,
    val intervalSeconds: Int = 5,
    val speechRate: Float = 1.0f,
    val speechAccent: SpeechAccent = SpeechAccent.US,
    val readChinese: Boolean = false,
    val voiceSource: VoiceSource = VoiceSource.REAL_HUMAN,
    val mode: DictationMode = DictationMode.PAPER
)

data class DictationWordItem(
    val id: String,
    val word: String,
    val phonetic: String,
    val translation: String,
    val unitTitle: String = "",
    var userInput: String = "",
    var isCorrect: Boolean? = null,
    var isRevealed: Boolean = false
) {
    /**
     * Normalize spelling for comparison:
     * - trim whitespace
     * - case-insensitive
     * - normalize punctuation if needed (e.g. apostrophes)
     */
    fun checkAnswer(input: String): Boolean {
        val cleanInput = input.trim().lowercase().replace("’", "'")
        val cleanWord = word.trim().lowercase().replace("’", "'")
        return cleanInput == cleanWord
    }
}
