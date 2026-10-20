package com.codex.listenandwrite.ui.wordbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codex.listenandwrite.data.entity.TextbookEntity
import com.codex.listenandwrite.data.entity.WordBookEntity
import com.codex.listenandwrite.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WordBookViewModel(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0) // 0: 教材词库, 1: 我的词包, 2: 导入词包
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchVisible = MutableStateFlow(false)
    val isSearchVisible: StateFlow<Boolean> = _isSearchVisible.asStateFlow()

    private val _importStatusMessage = MutableStateFlow<String?>(null)
    val importStatusMessage: StateFlow<String?> = _importStatusMessage.asStateFlow()

    private val _selectedStage = MutableStateFlow("all") // "all", "primary", "junior", "senior"
    val selectedStage: StateFlow<String> = _selectedStage.asStateFlow()

    val textbooks: StateFlow<List<TextbookEntity>> = combine(
        wordRepository.getAllTextbooks(),
        _searchQuery,
        _selectedStage
    ) { list, query, stage ->
        list.filter { book ->
            val matchesQuery = query.isBlank() ||
                book.title.contains(query, ignoreCase = true) ||
                book.publisher.contains(query, ignoreCase = true)

            val matchesStage = when (stage) {
                "primary" -> book.grade in 3..6
                "junior" -> book.grade in 7..9
                "senior" -> book.grade >= 10
                else -> true
            }

            matchesQuery && matchesStage
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectStage(stage: String) {
        _selectedStage.value = stage
    }

    val customWordBooks: StateFlow<List<WordBookEntity>> = wordRepository.getAllWordBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun toggleSearch() {
        _isSearchVisible.value = !_isSearchVisible.value
        if (!_isSearchVisible.value) {
            _searchQuery.value = ""
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectTextbook(id: String) {
        viewModelScope.launch {
            wordRepository.setSelectedTextbook(id)
        }
    }

    fun importJson(jsonContent: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = wordRepository.importWordBookFromJson(jsonContent)
            if (result.isSuccess) {
                _importStatusMessage.value = "导入成功！"
                _selectedTab.value = 1 // Switch to custom word packages
                onSuccess()
            } else {
                _importStatusMessage.value = "导入失败: ${result.exceptionOrNull()?.localizedMessage ?: "格式不正确"}"
            }
        }
    }

    fun clearImportMessage() {
        _importStatusMessage.value = null
    }

    class Factory(private val wordRepository: WordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WordBookViewModel(wordRepository) as T
        }
    }
}
