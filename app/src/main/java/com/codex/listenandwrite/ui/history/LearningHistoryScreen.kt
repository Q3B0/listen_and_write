package com.codex.listenandwrite.ui.history

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.codex.listenandwrite.data.entity.DictationSessionEntity
import com.codex.listenandwrite.ui.components.AppBottomNav
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.BottomNavTab
import com.codex.listenandwrite.ui.components.RoundedCard
import com.codex.listenandwrite.ui.theme.BorderOutline
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.DividerColor
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.SuccessGreen
import com.codex.listenandwrite.ui.theme.TitleText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LearningHistoryScreen(
    viewModel: LearningHistoryViewModel,
    onBackClick: () -> Unit,
    currentTab: BottomNavTab = BottomNavTab.HOME,
    onTabSelected: (BottomNavTab) -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val chartPoints by viewModel.chartPoints.collectAsState()

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PageBackground)
                    .statusBarsPadding()
            ) {
                AppTopBar(
                    title = "学习记录",
                    onBackClick = onBackClick,
                    applyStatusBarsPadding = false
                )

                val tabs = listOf("周统计", "月统计", "全部")
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
            AppBottomNav(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = PageBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimensions.ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Trend Line Chart Card
                LineChartView(points = chartPoints)
            }

            item {
                Text(
                    text = "最近听写",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleText,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (sessions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无听写历史", color = SecondaryText)
                    }
                }
            } else {
                items(sessions, key = { it.id }) { session ->
                    HistorySessionCard(session = session, dateString = sdf.format(Date(session.timestamp)))
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun HistorySessionCard(
    session: DictationSessionEntity,
    dateString: String
) {
    RoundedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Green Checkmark Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "${session.textbookTitle} ${session.unitRange}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TitleText
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = dateString,
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            Text(
                text = "${session.correctCount}/${session.totalCount}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TitleText
            )
        }
    }
}
