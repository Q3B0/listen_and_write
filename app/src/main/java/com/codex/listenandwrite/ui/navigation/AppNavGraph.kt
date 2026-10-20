package com.codex.listenandwrite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codex.listenandwrite.di.AppContainer
import com.codex.listenandwrite.domain.model.DictationConfig
import com.codex.listenandwrite.domain.model.DictationMode
import com.codex.listenandwrite.domain.model.DictationState
import com.codex.listenandwrite.ui.components.BottomNavTab
import com.codex.listenandwrite.ui.dictation.CorrectAnswerScreen
import com.codex.listenandwrite.ui.dictation.DictationInputScreen
import com.codex.listenandwrite.ui.dictation.DictationPaperScreen
import com.codex.listenandwrite.ui.dictation.DictationViewModel
import com.codex.listenandwrite.ui.dictation.WrongAnswerScreen
import com.codex.listenandwrite.ui.history.LearningHistoryScreen
import com.codex.listenandwrite.ui.history.LearningHistoryViewModel
import com.codex.listenandwrite.ui.home.HomeScreen
import com.codex.listenandwrite.ui.mine.MineScreen
import com.codex.listenandwrite.ui.mistakes.MistakeBookScreen
import com.codex.listenandwrite.ui.mistakes.MistakeBookViewModel
import com.codex.listenandwrite.ui.result.DictationResultScreen
import com.codex.listenandwrite.ui.settings.DictationSettingsScreen
import com.codex.listenandwrite.ui.settings.DictationSettingsViewModel
import com.codex.listenandwrite.ui.unit.UnitSelectionScreen
import com.codex.listenandwrite.ui.unit.UnitSelectionViewModel
import com.codex.listenandwrite.ui.wordbook.WordBookScreen
import com.codex.listenandwrite.ui.wordbook.WordBookViewModel

@Composable
fun AppNavGraph(
    container: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    var activeDictationConfig by remember { mutableStateOf<DictationConfig?>(null) }
    var activeDictationViewModel by remember { mutableStateOf<DictationViewModel?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // 1. Home Screen
        composable(Screen.Home.route) {
            val selectedTb by container.preferencesManager.selectedTextbookId.collectAsState(
                initial = com.codex.listenandwrite.data.local.TextbookImporter.DEFAULT_TEXTBOOK_ID
            )
            HomeScreen(
                onStartDictationClick = {
                    navController.navigate(Screen.UnitSelection.createRoute(selectedTb))
                },
                onWordBookClick = {
                    navController.navigate(Screen.WordBook.route)
                },
                onMistakeBookClick = {
                    navController.navigate(Screen.MistakeBook.route)
                },
                onLearningHistoryClick = {
                    navController.navigate(Screen.LearningHistory.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Mine.route)
                },
                currentTab = BottomNavTab.HOME,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.HOME -> {}
                        BottomNavTab.WORD_BOOK -> navController.navigate(Screen.WordBook.route)
                        BottomNavTab.MISTAKES -> navController.navigate(Screen.MistakeBook.route)
                        BottomNavTab.MINE -> navController.navigate(Screen.Mine.route)
                    }
                }
            )
        }

        // 2. WordBook Screen (教材选择)
        composable(Screen.WordBook.route) {
            val vm: WordBookViewModel = viewModel(factory = WordBookViewModel.Factory(container.wordRepository))
            WordBookScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStack() },
                onTextbookSelected = { textbookId ->
                    navController.navigate(Screen.UnitSelection.createRoute(textbookId))
                }
            )
        }

        // 3. Unit Selection Screen (单元选择)
        composable(
            route = Screen.UnitSelection.route,
            arguments = listOf(navArgument("textbookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tbId = backStackEntry.arguments?.getString("textbookId") ?: "pep_3_2"
            val vm: UnitSelectionViewModel = viewModel(
                factory = UnitSelectionViewModel.Factory(container.wordRepository, tbId)
            )
            UnitSelectionScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStack() },
                onNextClick = { textbookId, selectedUnitIds ->
                    navController.navigate(Screen.DictationSettings.createRoute(textbookId, selectedUnitIds))
                }
            )
        }

        // 4. Dictation Settings Screen (听写设置)
        composable(
            route = Screen.DictationSettings.route,
            arguments = listOf(
                navArgument("textbookId") { type = NavType.StringType },
                navArgument("unitIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tbId = backStackEntry.arguments?.getString("textbookId") ?: "pep_3_2"
            val rawUnits = backStackEntry.arguments?.getString("unitIds") ?: ""
            val unitIds = rawUnits.split(",").filter { it.isNotBlank() }

            val vm: DictationSettingsViewModel = viewModel(
                factory = DictationSettingsViewModel.Factory(
                    container.wordRepository,
                    container.preferencesManager,
                    tbId,
                    unitIds
                )
            )

            DictationSettingsScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStack() },
                onChangeRangeClick = { navController.popBackStack() },
                onStartDictationClick = { config ->
                    activeDictationConfig = config
                    val dictationVm = DictationViewModel(
                        wordRepository = container.wordRepository,
                        dictationRepository = container.dictationRepository,
                        mistakeRepository = container.mistakeRepository,
                        ttsManager = container.ttsManager,
                        config = config
                    )
                    activeDictationViewModel = dictationVm
                    navController.navigate(Screen.DictationSession.route)
                }
            )
        }

        // 5-9. Dictation Session Host (Paper, Input, Correct, Wrong, Result)
        composable(Screen.DictationSession.route) {
            val dictationVm = activeDictationViewModel
            val config = activeDictationConfig

            if (dictationVm != null && config != null) {
                val state by dictationVm.dictationState.collectAsState()

                when {
                    state == DictationState.COMPLETED -> {
                        // Screen 9: 听写结果 DictationResultScreen
                        DictationResultScreen(
                            viewModel = dictationVm,
                            mode = config.mode,
                            onViewMistakesClick = {
                                navController.navigate(Screen.MistakeBook.route) {
                                    popUpTo(Screen.Home.route)
                                }
                            },
                            onRetryClick = {
                                val retryVm = DictationViewModel(
                                    wordRepository = container.wordRepository,
                                    dictationRepository = container.dictationRepository,
                                    mistakeRepository = container.mistakeRepository,
                                    ttsManager = container.ttsManager,
                                    config = config
                                )
                                activeDictationViewModel = retryVm
                            },
                            onHomeClick = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    config.mode == DictationMode.PAPER -> {
                        // Screen 5: 纸笔听写 DictationPaperScreen
                        DictationPaperScreen(
                            viewModel = dictationVm,
                            onBackConfirmed = { navController.popBackStack() },
                            onFinish = { /* Handled via COMPLETED state */ }
                        )
                    }
                    config.mode == DictationMode.INPUT -> {
                        // Screen 6, 7, 8 in Input Mode
                        when (state) {
                            DictationState.FEEDBACK -> {
                                val words by dictationVm.words.collectAsState()
                                val currentIndex by dictationVm.currentIndex.collectAsState()
                                val currentWord = words.getOrNull(currentIndex)

                                if (currentWord?.isCorrect == true) {
                                    // Screen 7: 正确反馈 CorrectAnswerScreen
                                    CorrectAnswerScreen(
                                        viewModel = dictationVm,
                                        onBackConfirmed = { navController.popBackStack() }
                                    )
                                } else {
                                    // Screen 8: 错误反馈 WrongAnswerScreen
                                    WrongAnswerScreen(
                                        viewModel = dictationVm,
                                        onBackConfirmed = { navController.popBackStack() }
                                    )
                                }
                            }
                            else -> {
                                // Screen 6: 屏幕拼写 DictationInputScreen
                                DictationInputScreen(
                                    viewModel = dictationVm,
                                    onBackConfirmed = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            } else {
                HomeScreen(
                    onStartDictationClick = { navController.navigate(Screen.UnitSelection.createRoute(com.codex.listenandwrite.data.local.TextbookImporter.DEFAULT_TEXTBOOK_ID)) },
                    onWordBookClick = { navController.navigate(Screen.WordBook.route) },
                    onMistakeBookClick = { navController.navigate(Screen.MistakeBook.route) },
                    onLearningHistoryClick = { navController.navigate(Screen.LearningHistory.route) },
                    onSettingsClick = { navController.navigate(Screen.Mine.route) },
                    onTabSelected = {}
                )
            }
        }

        // 10. Mistake Book Screen
        composable(Screen.MistakeBook.route) {
            val vm: MistakeBookViewModel = viewModel(
                factory = MistakeBookViewModel.Factory(container.mistakeRepository, container.ttsManager)
            )
            MistakeBookScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStack() },
                onSpecialDictationClick = {
                    // Start special dictation using mistakes
                    val mistakeConfig = DictationConfig(
                        textbookTitle = "错词本专项",
                        unitRangeLabel = "全部易错词",
                        selectedUnitIds = listOf("u32_1", "u32_2", "u32_3", "u32_5"),
                        wordCount = 10,
                        mode = DictationMode.INPUT
                    )
                    activeDictationConfig = mistakeConfig
                    activeDictationViewModel = DictationViewModel(
                        wordRepository = container.wordRepository,
                        dictationRepository = container.dictationRepository,
                        mistakeRepository = container.mistakeRepository,
                        ttsManager = container.ttsManager,
                        config = mistakeConfig
                    )
                    navController.navigate(Screen.DictationSession.route)
                },
                currentTab = BottomNavTab.MISTAKES,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.HOME -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                        BottomNavTab.WORD_BOOK -> navController.navigate(Screen.WordBook.route)
                        BottomNavTab.MISTAKES -> {}
                        BottomNavTab.MINE -> navController.navigate(Screen.Mine.route)
                    }
                }
            )
        }

        // 11. Learning History Screen
        composable(Screen.LearningHistory.route) {
            val vm: LearningHistoryViewModel = viewModel(
                factory = LearningHistoryViewModel.Factory(container.dictationRepository)
            )
            LearningHistoryScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStack() },
                currentTab = BottomNavTab.HOME,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.HOME -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                        BottomNavTab.WORD_BOOK -> navController.navigate(Screen.WordBook.route)
                        BottomNavTab.MISTAKES -> navController.navigate(Screen.MistakeBook.route)
                        BottomNavTab.MINE -> navController.navigate(Screen.Mine.route)
                    }
                }
            )
        }

        // 12. Mine Screen
        composable(Screen.Mine.route) {
            MineScreen(
                ttsManager = container.ttsManager,
                preferencesManager = container.preferencesManager,
                wordRepository = container.wordRepository,
                currentTab = BottomNavTab.MINE,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.HOME -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                        BottomNavTab.WORD_BOOK -> navController.navigate(Screen.WordBook.route)
                        BottomNavTab.MISTAKES -> navController.navigate(Screen.MistakeBook.route)
                        BottomNavTab.MINE -> {}
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
