package com.codex.listenandwrite.ui.result

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.codex.listenandwrite.domain.model.DictationMode
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.PrimaryButton
import com.codex.listenandwrite.ui.components.RoundedCard
import com.codex.listenandwrite.ui.components.SecondaryButton
import com.codex.listenandwrite.ui.components.TrophyIllustration
import com.codex.listenandwrite.ui.dictation.DictationViewModel
import com.codex.listenandwrite.ui.theme.BodyText
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.DividerColor
import com.codex.listenandwrite.ui.theme.ErrorLightBg
import com.codex.listenandwrite.ui.theme.ErrorRed
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.SuccessGreen
import com.codex.listenandwrite.ui.theme.SuccessLightBg
import com.codex.listenandwrite.ui.theme.TitleText

@Composable
fun DictationResultScreen(
    viewModel: DictationViewModel,
    mode: DictationMode,
    onViewMistakesClick: () -> Unit,
    onRetryClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val words by viewModel.words.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()

    var showSelfCheckList by remember { mutableStateOf(mode == DictationMode.PAPER) }

    val correctCount = words.count { it.isCorrect == true }
    val wrongCount = words.count { it.isCorrect == false }
    val totalCount = words.size
    val accuracyPercent = if (totalCount > 0) ((correctCount.toFloat() / totalCount.toFloat()) * 100).toInt() else 0

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "听写结果",
                onBackClick = onHomeClick
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = Dimensions.ScreenHorizontalPadding, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrimaryButton(
                        text = "查看错词",
                        onClick = onViewMistakesClick
                    )

                    SecondaryButton(
                        text = "再听一次",
                        onClick = onRetryClick
                    )
                }
            }
        },
        containerColor = PageBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimensions.ScreenHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Trophy with sparkles
                TrophyIllustration(size = 130.dp)
            }

            item {
                // Score text
                Text(
                    text = "$correctCount / $totalCount",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "完成度 $accuracyPercent%",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }

            item {
                // Stats Row Card
                RoundedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Correct Count
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "$correctCount",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TitleText
                                )
                                Text(
                                    text = "正确",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        // Wrong Count
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "$wrongCount",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TitleText
                                )
                                Text(
                                    text = "错误",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        // Duration
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = timeFormatted,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TitleText
                                )
                                Text(
                                    text = "用时",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                    }
                }
            }

            // Word verification list (for paper mode self-check or reviewing mistakes)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (mode == DictationMode.PAPER) "对照答案自行批改" else "听写单词明细",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TitleText
                    )
                }
            }

            itemsIndexed(words) { index, item ->
                RoundedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${index + 1}. ${item.word}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TitleText
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.phonetic,
                                    fontSize = 13.sp,
                                    color = SecondaryText
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.translation,
                                fontSize = 13.sp,
                                color = BodyText
                            )
                            if (mode == DictationMode.INPUT && item.userInput.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "输入：${item.userInput}",
                                    fontSize = 12.sp,
                                    color = if (item.isCorrect == true) SuccessGreen else ErrorRed
                                )
                            }
                        }

                        // For Paper mode: clickable Correct / Wrong toggles
                        if (mode == DictationMode.PAPER) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { viewModel.setPaperWordEvaluation(index, true) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (item.isCorrect == true) SuccessGreen else SuccessLightBg)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "正确",
                                        tint = if (item.isCorrect == true) Color.White else SuccessGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.setPaperWordEvaluation(index, false) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (item.isCorrect == false) ErrorRed else ErrorLightBg)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "错误",
                                        tint = if (item.isCorrect == false) Color.White else ErrorRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        } else {
                            // In Input mode: just display the result icon
                            Icon(
                                imageVector = if (item.isCorrect == true) Icons.Filled.CheckCircle else Icons.Filled.HighlightOff,
                                contentDescription = null,
                                tint = if (item.isCorrect == true) SuccessGreen else ErrorRed,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
