package com.codex.listenandwrite.ui.dictation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.codex.listenandwrite.domain.model.DictationState
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.ConfirmationDialog
import com.codex.listenandwrite.ui.components.HeadphoneIllustration
import com.codex.listenandwrite.ui.theme.BodyText
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.TitleText

@Composable
fun DictationPaperScreen(
    viewModel: DictationViewModel,
    onBackConfirmed: () -> Unit,
    onFinish: () -> Unit
) {
    val words by viewModel.words.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val state by viewModel.dictationState.collectAsState()
    val isSlowSpeed by viewModel.isSlowSpeed.collectAsState()
    val showExitDialog by viewModel.showExitDialog.collectAsState()

    val totalWords = words.size
    val currentDisplayIndex = if (totalWords > 0) currentIndex + 1 else 0
    val progress = if (totalWords > 0) (currentIndex + 1).toFloat() / totalWords.toFloat() else 0f
    val isSpeaking = state == DictationState.SPEAKING
    val isPaused = state == DictationState.PAUSED

    BackHandler {
        viewModel.requestExit()
    }

    if (showExitDialog) {
        ConfirmationDialog(
            title = "退出听写",
            message = "确定要退出本次听写吗？当前进度将会中断并保存已完成部分。",
            confirmText = "退出",
            dismissText = "继续听写",
            onConfirm = {
                viewModel.dismissExitDialog()
                onBackConfirmed()
            },
            onDismiss = { viewModel.dismissExitDialog() }
        )
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
                    title = "$currentDisplayIndex / $totalWords",
                    onBackClick = { viewModel.requestExit() },
                    applyStatusBarsPadding = false,
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { /* Quick settings if needed */ }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "设置",
                                tint = TitleText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "设置",
                                fontSize = 14.sp,
                                color = TitleText
                            )
                        }
                    }
                )
                // Thin progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = PrimaryBlue,
                    trackColor = LightBlueBg
                )
            }
        },
        containerColor = PageBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimensions.ScreenHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Central Area: Headphone Illustration and State
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HeadphoneIllustration(
                    illustrationSize = 230.dp,
                    isSpeaking = isSpeaking
                )

                Spacer(modifier = Modifier.height(28.dp))

                val statusText = when (state) {
                    DictationState.SPEAKING -> "正在播放..."
                    DictationState.WAITING -> "请作答中..."
                    DictationState.PAUSED -> "已暂停"
                    DictationState.PREPARING -> "准备中..."
                    else -> "准备听写"
                }

                Text(
                    text = statusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TitleText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "请认真听，写下你听到的单词",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }

            // Bottom Controls Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Upper Control Row: Prev, Big Pause/Play, Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous
                    IconButton(
                        onClick = { viewModel.previousWord() },
                        enabled = currentIndex > 0,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(LightBlueBg.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastRewind,
                            contentDescription = "上一个",
                            tint = if (currentIndex > 0) PrimaryBlue else SecondaryText
                        )
                    }

                    // Large Circular Play/Pause Button
                    Surface(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.togglePause() },
                        shape = CircleShape,
                        color = PrimaryBlue,
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                contentDescription = if (isPaused) "继续" else "暂停",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // Next
                    IconButton(
                        onClick = { viewModel.nextWord() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(LightBlueBg.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastForward,
                            contentDescription = "下一个",
                            tint = PrimaryBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Lower Control Row: [重读] [慢速] [下一个]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickActionItem(
                        icon = Icons.Filled.Refresh,
                        label = "重读",
                        onClick = { viewModel.replayCurrentWord() }
                    )

                    QuickActionItem(
                        icon = Icons.Filled.SlowMotionVideo,
                        label = if (isSlowSpeed) "恢复" else "慢速",
                        isActive = isSlowSpeed,
                        onClick = { viewModel.toggleSlowSpeed() }
                    )

                    QuickActionItem(
                        icon = Icons.Filled.ChevronRight,
                        label = "下一个",
                        onClick = { viewModel.nextWord() }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isActive) PrimaryBlue else LightBlueBg.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) PrimaryBlue else BodyText
        )
    }
}
