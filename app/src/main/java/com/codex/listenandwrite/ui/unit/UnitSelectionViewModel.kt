package com.codex.listenandwrite.ui.unit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codex.listenandwrite.data.entity.TextbookEntity
import com.codex.listenandwrite.data.entity.UnitEntity
import com.codex.listenandwrite.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UnitSelectionViewModel(
    private val wordRepository: WordRepository,
    private val initialTextbookId: String? = null
) : ViewModel() {

    private val _currentTextbook = MutableStateFlow<TextbookEntity?>(null)
    val currentTextbook: StateFlow<TextbookEntity?> = _currentTextbook.asStateFlow()

    private val _units = MutableStateFlow<List<UnitEntity>>(emptyList())
    val units: StateFlow<List<UnitEntity>> = _units.asStateFlow()

    private val _selectedUnitIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedUnitIds: StateFlow<Set<String>> = _selectedUnitIds.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val textbook = if (initialTextbookId != null) {
                wordRepository.getTextbookById(initialTextbookId)
            } else {
                null
            } ?: wordRepository.getTextbookById(com.codex.listenandwrite.data.local.TextbookImporter.DEFAULT_TEXTBOOK_ID)

            _currentTextbook.value = textbook

            textbook?.let { tb ->
                val list = wordRepository.getUnitsListForTextbook(tb.id)
                _units.value = list

                // Default selection matching reference UI: Unit 1, Unit 2, Unit 5
                val defaultSelection = list.filter {
                    it.unitNumber in listOf(1, 2, 5)
                }.map { it.id }.toSet()

                _selectedUnitIds.value = if (defaultSelection.isNotEmpty()) defaultSelection else list.map { it.id }.toSet()
            }
        }
    }

    fun toggleUnit(unitId: String) {
        val current = _selectedUnitIds.value.toMutableSet()
        if (current.contains(unitId)) {
            current.remove(unitId)
        } else {
            current.add(unitId)
        }
        _selectedUnitIds.value = current
    }

    fun toggleSelectAll() {
        val allIds = _units.value.map { it.id }.toSet()
        if (_selectedUnitIds.value.size == allIds.size) {
            _selectedUnitIds.value = emptySet()
        } else {
            _selectedUnitIds.value = allIds
        }
    }

    val selectedWordCount: Int
        get() {
            val selected = _selectedUnitIds.value
            return _units.value.filter { selected.contains(it.id) }.sumOf { it.wordCount }
        }

    class Factory(
        private val wordRepository: WordRepository,
        private val textbookId: String?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UnitSelectionViewModel(wordRepository, textbookId) as T
        }
    }
}
