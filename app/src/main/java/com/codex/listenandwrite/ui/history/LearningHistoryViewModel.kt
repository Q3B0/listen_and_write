package com.codex.listenandwrite.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codex.listenandwrite.data.entity.DictationSessionEntity
import com.codex.listenandwrite.data.repository.DictationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChartPoint(
    val dateLabel: String,
    val accuracyPercent: Int,
    val isHighlighted: Boolean = false
)

class LearningHistoryViewModel(
    private val dictationRepository: DictationRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0) // 0: 周统计, 1: 月统计, 2: 全部
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val allSessions = dictationRepository.getAllSessions()

    val sessions: StateFlow<List<DictationSessionEntity>> = combine(
        allSessions,
        _selectedTab
    ) { list, tab ->
        val now = System.currentTimeMillis()
        val oneDay = 24L * 3600 * 1000
        when (tab) {
            0 -> list.filter { it.timestamp >= now - 7 * oneDay }
            1 -> list.filter { it.timestamp >= now - 30 * oneDay }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chartPoints: StateFlow<List<ChartPoint>> = sessions.combine(_selectedTab) { list, _ ->
        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
        // Default points if list is small or sample
        if (list.isEmpty()) {
            listOf(
                ChartPoint("10/13", 65),
                ChartPoint("10/14", 58),
                ChartPoint("10/15", 72),
                ChartPoint("10/16", 70),
                ChartPoint("10/17", 85, isHighlighted = true),
                ChartPoint("10/18", 78),
                ChartPoint("10/19", 90)
            )
        } else {
            val sorted = list.sortedBy { it.timestamp }.takeLast(7)
            sorted.mapIndexed { idx, session ->
                ChartPoint(
                    dateLabel = sdf.format(Date(session.timestamp)),
                    accuracyPercent = (session.accuracy * 100).toInt(),
                    isHighlighted = idx == sorted.size - 2 || idx == sorted.size - 1
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    class Factory(private val dictationRepository: DictationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LearningHistoryViewModel(dictationRepository) as T
        }
    }
}
