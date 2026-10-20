package com.codex.listenandwrite.ui.dictation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codex.listenandwrite.data.entity.DictationItemEntity
import com.codex.listenandwrite.data.entity.DictationSessionEntity
import com.codex.listenandwrite.data.repository.DictationRepository
import com.codex.listenandwrite.data.repository.MistakeRepository
import com.codex.listenandwrite.data.repository.WordRepository
import com.codex.listenandwrite.domain.model.DictationConfig
import com.codex.listenandwrite.domain.model.DictationMode
import com.codex.listenandwrite.domain.model.DictationState
import com.codex.listenandwrite.domain.model.DictationWordItem
import com.codex.listenandwrite.tts.TtsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DictationViewModel(
    private val wordRepository: WordRepository,
    private val dictationRepository: DictationRepository,
    private val mistakeRepository: MistakeRepository,
    private val ttsManager: TtsManager,
    private val config: DictationConfig
) : ViewModel() {

    private val _words = MutableStateFlow<List<DictationWordItem>>(emptyList())
    val words: StateFlow<List<DictationWordItem>> = _words.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _dictationState = MutableStateFlow(DictationState.IDLE)
    val dictationState: StateFlow<DictationState> = _dictationState.asStateFlow()

    private val _currentPlayIteration = MutableStateFlow(1)
    val currentPlayIteration: StateFlow<Int> = _currentPlayIteration.asStateFlow()

    private val _inputAnswer = MutableStateFlow("")
    val inputAnswer: StateFlow<String> = _inputAnswer.asStateFlow()

    private val _hintText = MutableStateFlow<String?>(null)
    val hintText: StateFlow<String?> = _hintText.asStateFlow()

    private val _isSlowSpeed = MutableStateFlow(false)
    val isSlowSpeed: StateFlow<Boolean> = _isSlowSpeed.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog: StateFlow<Boolean> = _showExitDialog.asStateFlow()

    private var timerJob: Job? = null
    private var playbackJob: Job? = null
    private var isSessionCompleted = false

    init {
        initializeSession()
    }

    private fun initializeSession() {
        _dictationState.value = DictationState.PREPARING
        viewModelScope.launch {
            val entities = wordRepository.getWordsForUnits(config.selectedUnitIds)
            val pool = if (config.isRandom) entities.shuffled() else entities
            val selected = pool.take(config.wordCount)

            val wordItems = selected.mapIndexed { idx, entity ->
                DictationWordItem(
                    id = entity.id,
                    word = entity.word,
                    phonetic = entity.phonetic,
                    translation = entity.translation,
                    unitTitle = ""
                )
            }

            _words.value = wordItems
            _currentIndex.value = 0
            _elapsedSeconds.value = 0

            startTimer()
            playCurrentWord()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_dictationState.value != DictationState.PAUSED && !isSessionCompleted) {
                    _elapsedSeconds.value += 1
                }
            }
        }
    }

    fun playCurrentWord(forceSlow: Boolean = false) {
        val currentList = _words.value
        val index = _currentIndex.value
        if (index !in currentList.indices) return

        val item = currentList[index]
        playbackJob?.cancel()

        playbackJob = viewModelScope.launch {
            val rate = if (forceSlow || _isSlowSpeed.value) 0.7f else config.speechRate
            ttsManager.setSpeechRate(rate)
            ttsManager.setAccent(config.speechAccent)
            ttsManager.voiceSource = config.voiceSource

            for (iter in 1..config.repeatCount) {
                if (_dictationState.value == DictationState.PAUSED) break

                _currentPlayIteration.value = iter
                _dictationState.value = DictationState.SPEAKING

                // Wait for speech to complete
                val speechDone = kotlinx.coroutines.CompletableDeferred<Boolean>()
                ttsManager.speak(
                    text = item.word,
                    utteranceId = "word_${item.id}_$iter",
                    onDone = { speechDone.complete(true) },
                    onError = { speechDone.complete(false) }
                )
                speechDone.await()

                if (config.readChinese && item.translation.isNotBlank()) {
                    delay(300)
                    val chineseDone = kotlinx.coroutines.CompletableDeferred<Boolean>()
                    ttsManager.speakChinese(
                        text = item.translation,
                        utteranceId = "zh_${item.id}_$iter",
                        onDone = { chineseDone.complete(true) },
                        onError = { chineseDone.complete(false) }
                    )
                    chineseDone.await()
                }

                if (iter < config.repeatCount) {
                    // Important per requirements: interval measured AFTER speech completes
                    _dictationState.value = DictationState.WAITING
                    delay((config.intervalSeconds * 1000).toLong())
                }
            }

            if (_dictationState.value != DictationState.PAUSED && _dictationState.value != DictationState.FEEDBACK) {
                if (config.mode == DictationMode.INPUT) {
                    _dictationState.value = DictationState.ANSWERING
                } else {
                    _dictationState.value = DictationState.WAITING
                }
            }
        }
    }

    // Input Mode: Input changed
    fun onInputAnswerChanged(text: String) {
        _inputAnswer.value = text
    }

    // Input Mode: Request Hint
    fun requestHint() {
        val currentList = _words.value
        val index = _currentIndex.value
        if (index in currentList.indices) {
            val word = currentList[index].word
            val currentHint = _hintText.value
            _hintText.value = if (currentHint == null) {
                // Show length hint: e.g. "长度: 6 个字母，首字母为 ${word.first()}"
                "首字母: ${word.first().uppercaseChar()} (共 ${word.length} 字母)"
            } else {
                // Show more: e.g. "f a _ _ _ _"
                word.mapIndexed { idx, c -> if (idx < 2) c else '_' }.joinToString(" ")
            }
        }
    }

    // Input Mode: Submit Answer
    fun submitAnswer() {
        val currentList = _words.value
        val index = _currentIndex.value
        if (index !in currentList.indices) return

        val item = currentList[index]
        val input = _inputAnswer.value.trim()
        val isCorrect = item.checkAnswer(input)

        item.userInput = input
        item.isCorrect = isCorrect
        item.isRevealed = true

        if (!isCorrect) {
            viewModelScope.launch {
                mistakeRepository.recordMistake(
                    word = item.word,
                    phonetic = item.phonetic,
                    translation = item.translation,
                    unitTitle = item.unitTitle
                )
            }
        }

        playbackJob?.cancel()
        ttsManager.stop()
        _dictationState.value = DictationState.FEEDBACK
    }

    // Feedback Mode or Paper Mode: Move to Next
    fun nextWord() {
        val currentList = _words.value
        val nextIdx = _currentIndex.value + 1

        if (nextIdx < currentList.size) {
            _currentIndex.value = nextIdx
            _inputAnswer.value = ""
            _hintText.value = null
            _dictationState.value = DictationState.PREPARING
            playCurrentWord()
        } else {
            finishSession()
        }
    }

    fun previousWord() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
            _inputAnswer.value = ""
            _hintText.value = null
            playCurrentWord()
        }
    }

    fun togglePause() {
        if (_dictationState.value == DictationState.PAUSED) {
            _dictationState.value = DictationState.WAITING
            playCurrentWord()
        } else {
            playbackJob?.cancel()
            ttsManager.stop()
            _dictationState.value = DictationState.PAUSED
        }
    }

    fun toggleSlowSpeed() {
        _isSlowSpeed.value = !_isSlowSpeed.value
        playCurrentWord(forceSlow = _isSlowSpeed.value)
    }

    fun replayCurrentWord() {
        playCurrentWord()
    }

    fun setPaperWordEvaluation(index: Int, isCorrect: Boolean) {
        val list = _words.value
        if (index in list.indices) {
            list[index].isCorrect = isCorrect
            list[index].isRevealed = true
            _words.value = ArrayList(list)

            if (!isCorrect) {
                viewModelScope.launch {
                    val item = list[index]
                    mistakeRepository.recordMistake(
                        word = item.word,
                        phonetic = item.phonetic,
                        translation = item.translation,
                        unitTitle = item.unitTitle
                    )
                }
            }
        }
    }

    private fun finishSession() {
        isSessionCompleted = true
        timerJob?.cancel()
        playbackJob?.cancel()
        ttsManager.stop()

        _dictationState.value = DictationState.COMPLETED

        // Save session to Room
        viewModelScope.launch {
            val list = _words.value
            val correctCount = list.count { it.isCorrect == true }
            val wrongCount = list.count { it.isCorrect == false }
            val total = list.size
            val accuracy = if (total > 0) correctCount.toFloat() / total.toFloat() else 0f

            val session = DictationSessionEntity(
                textbookTitle = config.textbookTitle,
                unitRange = config.unitRangeLabel,
                mode = config.mode.name,
                totalCount = total,
                correctCount = correctCount,
                wrongCount = wrongCount,
                accuracy = accuracy,
                durationSeconds = _elapsedSeconds.value
            )

            val items = list.mapIndexed { idx, item ->
                DictationItemEntity(
                    sessionId = 0,
                    word = item.word,
                    phonetic = item.phonetic,
                    translation = item.translation,
                    userInput = item.userInput,
                    isCorrect = item.isCorrect ?: false,
                    orderIndex = idx
                )
            }

            dictationRepository.saveSession(session, items)
        }
    }

    fun requestExit() {
        _showExitDialog.value = true
    }

    fun dismissExitDialog() {
        _showExitDialog.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        playbackJob?.cancel()
        ttsManager.stop()
    }

    class Factory(
        private val wordRepository: WordRepository,
        private val dictationRepository: DictationRepository,
        private val mistakeRepository: MistakeRepository,
        private val ttsManager: TtsManager,
        private val config: DictationConfig
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DictationViewModel(
                wordRepository,
                dictationRepository,
                mistakeRepository,
                ttsManager,
                config
            ) as T
        }
    }
}
