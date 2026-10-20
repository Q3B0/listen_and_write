package com.codex.listenandwrite.ui.mistakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codex.listenandwrite.data.entity.MistakeRecordEntity
import com.codex.listenandwrite.data.repository.MistakeRepository
import com.codex.listenandwrite.tts.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MistakeBookViewModel(
    private val mistakeRepository: MistakeRepository,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0) // 0: 全部错词, 1: 按单元, 2: 按时间
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val mistakes: StateFlow<List<MistakeRecordEntity>> = combine(
        mistakeRepository.getAllMistakes(),
        _selectedTab,
        _searchQuery
    ) { list, tab, query ->
        var filtered = if (query.isBlank()) list
        else list.filter { it.word.contains(query, ignoreCase = true) || it.translation.contains(query, ignoreCase = true) }

        when (tab) {
            0 -> filtered.sortedByDescending { it.lastMistakeTime }
            1 -> filtered.sortedBy { it.unitTitle }
            2 -> filtered.sortedByDescending { it.lastMistakeTime }
            else -> filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun playWord(word: String) {
        ttsManager.speak(text = word, utteranceId = "mistake_$word")
    }

    fun toggleFavorite(record: MistakeRecordEntity) {
        viewModelScope.launch {
            mistakeRepository.setFavoriteStatus(record.id, !record.isFavorite)
        }
    }

    fun toggleMastered(record: MistakeRecordEntity) {
        viewModelScope.launch {
            mistakeRepository.setMasteredStatus(record.id, !record.isMastered)
        }
    }

    class Factory(
        private val mistakeRepository: MistakeRepository,
        private val ttsManager: TtsManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MistakeBookViewModel(mistakeRepository, ttsManager) as T
        }
    }
}
