package com.codex.listenandwrite.ui.wordbook

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codex.listenandwrite.data.entity.TextbookEntity
import com.codex.listenandwrite.data.entity.WordBookEntity
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.PrimaryButton
import com.codex.listenandwrite.ui.components.RoundedCard
import com.codex.listenandwrite.ui.components.SecondaryButton
import com.codex.listenandwrite.ui.theme.BodyText
import com.codex.listenandwrite.ui.theme.BorderOutline
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.DividerColor
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.SuccessGreen
import com.codex.listenandwrite.ui.theme.TitleText

@Composable
fun WordBookScreen(
    viewModel: WordBookViewModel,
    onBackClick: () -> Unit,
    onTextbookSelected: (String) -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val textbooks by viewModel.textbooks.collectAsState()
    val customBooks by viewModel.customWordBooks.collectAsState()
    val isSearchVisible by viewModel.isSearchVisible.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val importMessage by viewModel.importStatusMessage.collectAsState()
    val selectedStage by viewModel.selectedStage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(importMessage) {
        importMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearImportMessage()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PageBackground)
                    .statusBarsPadding()
            ) {
                AppTopBar(
                    title = "选择单词包",
                    onBackClick = onBackClick,
                    applyStatusBarsPadding = false,
                    actions = {
                        IconButton(onClick = { viewModel.toggleSearch() }) {
                            Icon(
                                imageVector = if (isSearchVisible) Icons.Filled.Close else Icons.Filled.Search,
                                contentDescription = "搜索",
                                tint = TitleText
                            )
                        }
                    }
                )

                AnimatedVisibility(visible = isSearchVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PageBackground)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
                            placeholder = { Text("搜索教材或词包名称", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // 3 Tabs matching reference
                val tabs = listOf("教材词库", "我的词包", "导入词包")
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = PageBackground,
                    contentColor = PrimaryBlue,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 3.dp,
                            color = PrimaryBlue
                        )
                    },
                    divider = {
                        HorizontalDivider(thickness = 0.5.dp, color = DividerColor)
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { viewModel.selectTab(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 15.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) PrimaryBlue else SecondaryText
                                )
                            }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PageBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TextbookListContent(
                    textbooks = textbooks,
                    selectedStage = selectedStage,
                    onStageSelect = viewModel::selectStage,
                    onSelect = { textbook ->
                        viewModel.selectTextbook(textbook.id)
                        onTextbookSelected(textbook.id)
                    }
                )
                1 -> CustomWordBookContent(
                    customBooks = customBooks,
                    onSelectBook = { book ->
                        // Custom word book action
                        onTextbookSelected(book.id)
                    }
                )
                2 -> ImportWordBookContent(
                    onImport = { json ->
                        viewModel.importJson(json) {
                            // On success
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TextbookListContent(
    textbooks: List<TextbookEntity>,
    selectedStage: String,
    onStageSelect: (String) -> Unit,
    onSelect: (TextbookEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(Dimensions.ListItemSpacing)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Stage Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val stages = listOf(
                    "all" to "全部",
                    "primary" to "小学 (3-6年级)",
                    "junior" to "初中 (7-9年级)",
                    "senior" to "高中 (2019版)"
                )
                stages.forEach { (key, label) ->
                    val isSelected = selectedStage == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryBlue else Color(0xFFEDF2F7))
                            .clickable { onStageSelect(key) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else SecondaryText
                        )
                    }
                }
            }

            Text(
                text = when (selectedStage) {
                    "primary" -> "人教版 PEP 小学英语（2024新版教材）"
                    "junior" -> "人教版初中英语（7-9年级新教材）"
                    "senior" -> "人教版高中英语（新课标）"
                    else -> "萤光小语 flyabc.fun 全量词库（小学 + 初中 + 高中）"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryText,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(textbooks, key = { it.id }) { textbook ->
            TextbookCardItem(
                textbook = textbook,
                isSelected = textbook.isSelected,
                onClick = { onSelect(textbook) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TextbookCardItem(
    textbook: TextbookEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    RoundedCard(
        modifier = Modifier.fillMaxWidth(),
        isSelected = isSelected,
        selectedBorderColor = PrimaryBlue,
        unselectedBorderColor = BorderOutline,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Cover Graphic Thumbnail
            Surface(
                modifier = Modifier
                    .size(width = 54.dp, height = 64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = when (textbook.grade) {
                    3 -> Color(0xFFFFE8B3)
                    4 -> Color(0xFFD6F5E3)
                    5 -> Color(0xFFFFD6D9)
                    else -> LightBlueBg
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PEP",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${textbook.grade}年级",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitleText
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textbook Title and Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = textbook.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TitleText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${textbook.unitCount} 个单元 · ${textbook.wordCount} 词",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            }

            // Radio Button Selection State matching Reference UI
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "已选择",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.5.dp, BorderOutline, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun CustomWordBookContent(
    customBooks: List<WordBookEntity>,
    onSelectBook: (WordBookEntity) -> Unit
) {
    if (customBooks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无自定义词包，可在「导入词包」中添加", color = SecondaryText)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(customBooks) { book ->
                RoundedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSelectBook(book) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LightBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TitleText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = book.description,
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "共 ${book.wordCount} 个单词",
                                fontSize = 12.sp,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportWordBookContent(
    onImport: (String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    val sampleJson = """
    {
      "title": "自定义期末高频复习",
      "description": "期末考试易错重点单词",
      "words": [
        {"word": "family", "phonetic": "['fæməli]", "translation": "家庭，家人", "unit": "Unit 2"},
        {"word": "teacher", "phonetic": "[ˈtiːtʃə(r)]", "translation": "老师", "unit": "Unit 1"},
        {"word": "student", "phonetic": "[ˈstjuːdnt]", "translation": "学生", "unit": "Unit 1"},
        {"word": "giraffe", "phonetic": "[dʒəˈrɑːf]", "translation": "长颈鹿", "unit": "Unit 3"},
        {"word": "watermelon", "phonetic": "[ˈwɔːtəmelən]", "translation": "西瓜", "unit": "Unit 5"}
      ]
    }
    """.trimIndent()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "粘贴 JSON 格式单词包",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TitleText
        )

        OutlinedTextField(
            value = jsonText,
            onValueChange = { jsonText = it },
            placeholder = { Text("在此粘贴 JSON 数据，包含 title, description 及 words 列表...") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = "填入示例",
                onClick = { jsonText = sampleJson },
                modifier = Modifier.weight(1f)
            )

            PrimaryButton(
                text = "立即导入",
                onClick = { if (jsonText.isNotBlank()) onImport(jsonText) },
                enabled = jsonText.isNotBlank(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
