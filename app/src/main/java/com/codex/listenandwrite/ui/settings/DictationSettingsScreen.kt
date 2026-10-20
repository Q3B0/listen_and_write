package com.codex.listenandwrite.ui.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.codex.listenandwrite.domain.model.DictationConfig
import com.codex.listenandwrite.domain.model.DictationMode
import com.codex.listenandwrite.domain.model.SpeechAccent
import com.codex.listenandwrite.domain.model.VoiceSource
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.PrimaryButton
import com.codex.listenandwrite.ui.components.RoundedCard
import com.codex.listenandwrite.ui.components.TextActionButton
import com.codex.listenandwrite.ui.theme.BodyText
import com.codex.listenandwrite.ui.theme.BorderOutline
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.PrimaryDarkBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.TitleText

@Composable
fun DictationSettingsScreen(
    viewModel: DictationSettingsViewModel,
    onBackClick: () -> Unit,
    onChangeRangeClick: () -> Unit,
    onStartDictationClick: (DictationConfig) -> Unit
) {
    val config by viewModel.config.collectAsState()
    val previewWords by viewModel.previewWords.collectAsState()
    val showPreviewDialog by viewModel.showPreviewDialog.collectAsState()

    if (showPreviewDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.togglePreviewDialog(false) },
            title = {
                Text("预览词库 (${previewWords.size} 词)", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    previewWords.forEach { word ->
                        Text(
                            text = "• $word",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.togglePreviewDialog(false) }) {
                    Text("关闭", color = PrimaryBlue)
                }
            },
            containerColor = CardBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "听写设置",
                onBackClick = onBackClick,
                actions = {
                    TextActionButton(
                        text = "预览单词",
                        onClick = { viewModel.togglePreviewDialog(true) }
                    )
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = Dimensions.ScreenHorizontalPadding, vertical = 12.dp)
                ) {
                    PrimaryButton(
                        text = "开始听写",
                        onClick = { onStartDictationClick(config) }
                    )
                }
            }
        },
        containerColor = PageBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensions.ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Card 1: 听写范围
            RoundedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "听写范围",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TitleText
                        )
                        Text(
                            text = "修改",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue,
                            modifier = Modifier.clickable(onClick = onChangeRangeClick)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${config.textbookTitle} · ${config.unitRangeLabel}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = BodyText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "共 ${config.totalAvailableWords} 个单词",
                        fontSize = 13.sp,
                        color = SecondaryText
                    )
                }
            }

            // Card 2: 听写数量
            RoundedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "听写数量",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TitleText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val options = listOf(
                        config.totalAvailableWords to "全部 (${config.totalAvailableWords})",
                        10 to "10",
                        20 to "20",
                        30 to "30",
                        50 to "50"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        options.forEach { (count, label) ->
                            val isSelected = config.wordCount == count ||
                                    (count == config.totalAvailableWords && config.wordCount >= config.totalAvailableWords)

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setWordCount(count) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) PrimaryBlue else LightBlueBg.copy(alpha = 0.5f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else BodyText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 3: 播放设置
            RoundedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "播放设置",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TitleText
                    )

                    // 朗读次数 Stepper
                    SettingStepperRow(
                        label = "朗读次数",
                        valueText = "${config.repeatCount} 次",
                        onMinus = { viewModel.setRepeatCount(config.repeatCount - 1) },
                        onPlus = { viewModel.setRepeatCount(config.repeatCount + 1) }
                    )

                    // 间隔时间 Stepper
                    SettingStepperRow(
                        label = "间隔时间",
                        valueText = "${config.intervalSeconds} 秒",
                        onMinus = { viewModel.setIntervalSeconds(config.intervalSeconds - 1) },
                        onPlus = { viewModel.setIntervalSeconds(config.intervalSeconds + 1) }
                    )

                    // 语速 Stepper
                    val speedLabel = when (config.speechRate) {
                        0.8f -> "慢速"
                        1.2f -> "快速"
                        else -> "正常"
                    }
                    SettingStepperRow(
                        label = "语速",
                        valueText = speedLabel,
                        onMinus = { viewModel.setSpeechRate(0.8f) },
                        onPlus = { viewModel.setSpeechRate(1.2f) }
                    )

                    // 发音类型 Segmented Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "发音类型",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = BodyText
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LightBlueBg.copy(alpha = 0.5f))
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SpeechAccent.entries.forEach { accent ->
                                val isSelected = config.speechAccent == accent
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { viewModel.setSpeechAccent(accent) },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) PrimaryBlue else Color.Transparent
                                ) {
                                    Text(
                                        text = accent.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else SecondaryText,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 发音模式 Segmented Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "发音模式",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = BodyText
                            )
                            Text(
                                text = if (config.voiceSource == VoiceSource.REAL_HUMAN) "真人原声 · 自动离线缓存" else "本地系统 TTS 合成",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LightBlueBg.copy(alpha = 0.5f))
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            VoiceSource.entries.forEach { source ->
                                val isSelected = config.voiceSource == source
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { viewModel.setVoiceSource(source) },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) PrimaryBlue else Color.Transparent
                                ) {
                                    Text(
                                        text = source.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else SecondaryText,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 朗读中文释义
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "朗读中文释义",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = BodyText
                            )
                            Text(
                                text = "朗读英文后播报词意",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }

                        Switch(
                            checked = config.readChinese,
                            onCheckedChange = { viewModel.setReadChinese(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryBlue
                            )
                        )
                    }
                }
            }

            // Card 4: 听写模式
            RoundedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "听写模式",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TitleText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 纸笔听写
                        ModeSelectorCard(
                            title = "纸笔听写",
                            subtitle = "只播放声音\n在纸上写",
                            icon = Icons.Filled.Headphones,
                            isSelected = config.mode == DictationMode.PAPER,
                            onClick = { viewModel.setDictationMode(DictationMode.PAPER) },
                            modifier = Modifier.weight(1f)
                        )

                        // 屏幕拼写
                        ModeSelectorCard(
                            title = "屏幕拼写",
                            subtitle = "输入单词\n自动判分",
                            icon = Icons.Filled.Edit,
                            isSelected = config.mode == DictationMode.INPUT,
                            onClick = { viewModel.setDictationMode(DictationMode.INPUT) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingStepperRow(
    label: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = BodyText
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightBlueBg.copy(alpha = 0.5f))
                    .clickable(onClick = onMinus),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "减少",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = valueText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TitleText,
                modifier = Modifier.width(50.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightBlueBg.copy(alpha = 0.5f))
                    .clickable(onClick = onPlus),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "增加",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ModeSelectorCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) LightBlueBg else CardBackground,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryBlue else BorderOutline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PrimaryBlue else LightBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) PrimaryDarkBlue else TitleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = SecondaryText
                )
            }
        }
    }
}
