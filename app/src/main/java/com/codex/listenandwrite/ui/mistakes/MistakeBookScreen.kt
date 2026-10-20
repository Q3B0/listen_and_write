package com.codex.listenandwrite.ui.mistakes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codex.listenandwrite.data.entity.MistakeRecordEntity
import com.codex.listenandwrite.ui.components.AppBottomNav
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.BottomNavTab
import com.codex.listenandwrite.ui.components.PrimaryButton
import com.codex.listenandwrite.ui.components.RoundedCard
import com.codex.listenandwrite.ui.theme.BodyText
import com.codex.listenandwrite.ui.theme.BorderOutline
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.DividerColor
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.TitleText
import com.codex.listenandwrite.ui.theme.WarningYellow

@Composable
fun MistakeBookScreen(
    viewModel: MistakeBookViewModel,
    onBackClick: () -> Unit,
    onSpecialDictationClick: () -> Unit,
    currentTab: BottomNavTab = BottomNavTab.MISTAKES,
    onTabSelected: (BottomNavTab) -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val mistakes by viewModel.mistakes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PageBackground)
                    .statusBarsPadding()
            ) {
                AppTopBar(
                    title = "错词本",
                    onBackClick = onBackClick,
                    applyStatusBarsPadding = false
                )

                val tabs = listOf("全部错词", "按单元", "按时间")
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
        bottomBar = {
            Column {
                if (mistakes.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CardBackground,
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            PrimaryButton(
                                text = "错词专项听写 (${mistakes.size})",
                                onClick = onSpecialDictationClick
                            )
                        }
                    }
                }
                AppBottomNav(
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }
        },
        containerColor = PageBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimensions.ScreenHorizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (mistakes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无错词记录，太棒啦！",
                        fontSize = 15.sp,
                        color = SecondaryText
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.ListItemSpacing)
                ) {
                    items(mistakes, key = { it.id }) { mistake ->
                        MistakeWordCard(
                            mistake = mistake,
                            onPlay = { viewModel.playWord(mistake.word) },
                            onToggleStar = { viewModel.toggleFavorite(mistake) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MistakeWordCard(
    mistake: MistakeRecordEntity,
    onPlay: () -> Unit,
    onToggleStar: () -> Unit
) {
    RoundedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Word info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mistake.word,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitleText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mistake.phonetic,
                        fontSize = 13.sp,
                        color = SecondaryText
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mistake.translation,
                    fontSize = 14.sp,
                    color = BodyText
                )
            }

            // Right Actions: Blue play button + Yellow star
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Audio Play Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LightBlueBg)
                        .clickable(onClick = onPlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "朗读",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Favorite / Bookmark Star
                IconButton(
                    onClick = onToggleStar,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = if (mistake.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "收藏",
                        tint = if (mistake.isFavorite) WarningYellow else SecondaryText,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
