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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codex.listenandwrite.domain.model.DictationState
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.ConfirmationDialog
import com.codex.listenandwrite.ui.components.HeadphoneIllustration
import com.codex.listenandwrite.ui.components.PrimaryButton
import com.codex.listenandwrite.ui.theme.BodyText
import com.codex.listenandwrite.ui.theme.BorderOutline
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.TitleText

@Composable
fun DictationInputScreen(
    viewModel: DictationViewModel,
    onBackConfirmed: () -> Unit
) {
    val words by viewModel.words.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val state by viewModel.dictationState.collectAsState()
    val inputAnswer by viewModel.inputAnswer.collectAsState()
    val hintText by viewModel.hintText.collectAsState()
    val showExitDialog by viewModel.showExitDialog.collectAsState()

    val totalWords = words.size
    val currentDisplayIndex = if (totalWords > 0) currentIndex + 1 else 0
    val progress = if (totalWords > 0) (currentIndex + 1).toFloat() / totalWords.toFloat() else 0f
    val isSpeaking = state == DictationState.SPEAKING

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
                            modifier = Modifier.padding(end = 8.dp)
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
            Spacer(modifier = Modifier.height(16.dp))

            // Central Area: Headphone Illustration and Replay Button
            Box(contentAlignment = Alignment.Center) {
                HeadphoneIllustration(
                    illustrationSize = 200.dp,
                    isSpeaking = isSpeaking
                )

                // Speaker replay button on the right
                IconButton(
                    onClick = { viewModel.replayCurrentWord() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LightBlueBg)
                ) {
                    Icon(
                        imageVector = Icons.Filled.VolumeUp,
                        contentDescription = "重听",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input and Action Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hint display if available
                AnimatedVisibility(visible = hintText != null) {
                    hintText?.let { hint ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LightBlueBg.copy(alpha = 0.5f))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "提示：$hint",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryBlue
                            )
                        }
                    }
                }

                // English Word Input Field
                OutlinedTextField(
                    value = inputAnswer,
                    onValueChange = viewModel::onInputAnswerChanged,
                    placeholder = {
                        Text(
                            text = "请输入你听到的英文单词",
                            fontSize = 15.sp,
                            color = SecondaryText
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.TextFieldCornerRadius),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BorderOutline
                    ),
                    trailingIcon = {
                        if (inputAnswer.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onInputAnswerChanged("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "清除",
                                    tint = SecondaryText
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (inputAnswer.isNotBlank()) {
                                viewModel.submitAnswer()
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Action Buttons: [重听] [提示] [提交]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Replay button
                    ActionButtonRound(
                        icon = Icons.Outlined.Refresh,
                        label = "重听",
                        onClick = { viewModel.replayCurrentWord() }
                    )

                    // Hint button
                    ActionButtonRound(
                        icon = Icons.Filled.HelpOutline,
                        label = "提示",
                        onClick = { viewModel.requestHint() }
                    )

                    // Submit Primary Button
                    PrimaryButton(
                        text = "提交",
                        enabled = inputAnswer.isNotBlank(),
                        onClick = { viewModel.submitAnswer() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtonRound(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(LightBlueBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = BodyText
        )
    }
}
