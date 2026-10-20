package com.codex.listenandwrite.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object WordBook : Screen("word_book")
    object UnitSelection : Screen("unit_selection/{textbookId}") {
        fun createRoute(textbookId: String) = "unit_selection/$textbookId"
    }
    object DictationSettings : Screen("dictation_settings/{textbookId}/{unitIds}") {
        fun createRoute(textbookId: String, unitIds: List<String>) =
            "dictation_settings/$textbookId/${unitIds.joinToString(",")}"
    }
    object DictationSession : Screen("dictation_session")
    object MistakeBook : Screen("mistake_book")
    object LearningHistory : Screen("learning_history")
    object Mine : Screen("mine")
}
