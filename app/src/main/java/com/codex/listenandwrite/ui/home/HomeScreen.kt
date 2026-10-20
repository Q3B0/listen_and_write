package com.codex.listenandwrite.ui.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codex.listenandwrite.ui.components.AppBottomNav
import com.codex.listenandwrite.ui.components.BottomNavTab
import com.codex.listenandwrite.ui.components.ChildBannerIllustration
import com.codex.listenandwrite.ui.theme.BodyText
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.FeatureDictationBg
import com.codex.listenandwrite.ui.theme.FeatureHistoryBg
import com.codex.listenandwrite.ui.theme.FeatureHistoryText
import com.codex.listenandwrite.ui.theme.FeatureMistakesBg
import com.codex.listenandwrite.ui.theme.FeatureMistakesText
import com.codex.listenandwrite.ui.theme.FeatureWordBookBg
import com.codex.listenandwrite.ui.theme.FeatureWordBookText
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.TitleText

@Composable
fun HomeScreen(
    onStartDictationClick: () -> Unit,
    onWordBookClick: () -> Unit,
    onMistakeBookClick: () -> Unit,
    onLearningHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    currentTab: BottomNavTab = BottomNavTab.HOME,
    onTabSelected: (BottomNavTab) -> Unit
) {
    Scaffold(
        bottomBar = {
            AppBottomNav(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = PageBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensions.ScreenHorizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Top Greeting Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hi，小朋友",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitleText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "今天也要加油哦！",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "设置",
                        tint = TitleText
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Child Reading Banner Illustration
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(Dimensions.CardCornerRadius),
                color = LightBlueBg
            ) {
                ChildBannerIllustration()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Feature Card 1: 开始听写 (Large Blue Card)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(Dimensions.CardCornerRadius))
                    .clickable(onClick = onStartDictationClick),
                shape = RoundedCornerShape(Dimensions.CardCornerRadius),
                color = FeatureDictationBg,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Headphones,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "开始听写",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "选择词包，开始练习",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Feature Card 2: 单词包 (Warm Yellow Card)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(Dimensions.CardCornerRadius))
                    .clickable(onClick = onWordBookClick),
                shape = RoundedCornerShape(Dimensions.CardCornerRadius),
                color = FeatureWordBookBg,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB800).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "单词包",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TitleText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "教材 · 自建 · 导入",
                            fontSize = 13.sp,
                            color = FeatureWordBookText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Two Cards in a Row: 错词本 (Green) & 学习记录 (Purple)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left: 错词本
                FeatureSubCard(
                    title = "错词本",
                    subtitle = "复习易错单词",
                    icon = Icons.Filled.Assignment,
                    bgColor = FeatureMistakesBg,
                    iconColor = FeatureMistakesText,
                    onClick = onMistakeBookClick,
                    modifier = Modifier.weight(1f)
                )

                // Right: 学习记录
                FeatureSubCard(
                    title = "学习记录",
                    subtitle = "查看历史成绩",
                    icon = Icons.Filled.AutoGraph,
                    bgColor = FeatureHistoryBg,
                    iconColor = FeatureHistoryText,
                    onClick = onLearningHistoryClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureSubCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(Dimensions.CardCornerRadius))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimensions.CardCornerRadius),
        color = bgColor,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = iconColor
                )
            }
        }
    }
}
