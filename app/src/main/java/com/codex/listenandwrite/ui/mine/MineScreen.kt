package com.codex.listenandwrite.ui.mine

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codex.listenandwrite.data.local.PreferencesManager
import com.codex.listenandwrite.data.repository.WordRepository
import com.codex.listenandwrite.domain.model.SpeechAccent
import com.codex.listenandwrite.domain.model.VoiceSource
import com.codex.listenandwrite.tts.TtsManager
import kotlinx.coroutines.flow.first
import com.codex.listenandwrite.ui.components.AppBottomNav
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.BottomNavTab
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
import com.codex.listenandwrite.ui.theme.SuccessGreen
import com.codex.listenandwrite.ui.theme.TitleText
import kotlinx.coroutines.launch

@Composable
fun MineScreen(
    ttsManager: TtsManager,
    preferencesManager: PreferencesManager,
    wordRepository: WordRepository,
    currentTab: BottomNavTab = BottomNavTab.MINE,
    onTabSelected: (BottomNavTab) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val voiceSourcePref by preferencesManager.voiceSource.collectAsState(initial = "REAL_HUMAN")
    val currentEngineName by ttsManager.currentEngineName.collectAsState()

    var cachedWordsCount by remember { mutableIntStateOf(ttsManager.getCachedWordsCount()) }
    var cacheSizeMB by remember { mutableFloatStateOf(ttsManager.getCacheSizeMB()) }

    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadStatusText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "个人中心",
                onBackClick = onBackClick
            )
        },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensions.ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. User Info Header Card
            RoundedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(LightBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "英语小达人",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TitleText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "坚持每天听写，英语更棒！",
                            fontSize = 13.sp,
                            color = SecondaryText
                        )
                    }
                }
            }

            // 2. 语音朗读与发音品质优化 Card (核心优化板块)
            RoundedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(LightBlueBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "发音品质与引擎设置",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TitleText
                                )
                                Text(
                                    text = "彻底告别机械音，享受地道真人发音",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = DividerColor)

                    // 发音模式切换
                    Text(
                        text = "首选朗读模式",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = BodyText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isRealHuman = voiceSourcePref == "REAL_HUMAN"

                        // 真人原声选项
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isRealHuman) 1.8.dp else 1.dp,
                                    color = if (isRealHuman) PrimaryBlue else BorderOutline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    scope.launch {
                                        preferencesManager.setVoiceSource("REAL_HUMAN")
                                        ttsManager.voiceSource = VoiceSource.REAL_HUMAN
                                    }
                                },
                            color = if (isRealHuman) LightBlueBg.copy(alpha = 0.4f) else CardBackground,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "真人原声 (推荐)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRealHuman) PrimaryBlue else TitleText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "纯正英美音标发音\n自动离线持久缓存",
                                    fontSize = 11.sp,
                                    color = SecondaryText,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        // 系统合成选项
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (!isRealHuman) 1.8.dp else 1.dp,
                                    color = if (!isRealHuman) PrimaryBlue else BorderOutline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    scope.launch {
                                        preferencesManager.setVoiceSource("SYSTEM_TTS")
                                        ttsManager.voiceSource = VoiceSource.SYSTEM_TTS
                                    }
                                },
                            color = if (!isRealHuman) LightBlueBg.copy(alpha = 0.4f) else CardBackground,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "系统 TTS 合成",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isRealHuman) PrimaryBlue else TitleText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "本机语音引擎合成\n支持英美双发音库",
                                    fontSize = 11.sp,
                                    color = SecondaryText,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // 真人原声离线缓存统计与一键下载
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PageBackground,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "离线真人发音缓存",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TitleText
                                )
                                Text(
                                    text = "已缓存 $cachedWordsCount 词 (${String.format("%.1f", cacheSizeMB)} MB)",
                                    fontSize = 12.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (isDownloading) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { downloadProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = PrimaryBlue,
                                    trackColor = LightBlueBg
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = downloadStatusText,
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            } else if (downloadStatusText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = downloadStatusText,
                                    fontSize = 11.sp,
                                    color = SuccessGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isDownloading = true
                                            val selectedTb = try { preferencesManager.selectedTextbookId.first() } catch (_: Exception) { com.codex.listenandwrite.data.local.TextbookImporter.DEFAULT_TEXTBOOK_ID }
                                            val tb = wordRepository.getTextbookById(selectedTb)
                                            val tbTitle = tb?.title ?: "当前教材"
                                            downloadStatusText = "正在下载${tbTitle}真人发音..."
                                            val units = wordRepository.getUnitsListForTextbook(selectedTb).map { it.id }
                                            val words = wordRepository.getWordsForUnits(units).map { it.word }
                                            ttsManager.preCacheWords(words, SpeechAccent.US) { done, total ->
                                                downloadProgress = if (total > 0) done.toFloat() / total.toFloat() else 1f
                                                downloadStatusText = "已下载 $done / $total 词"
                                            }
                                            isDownloading = false
                                            cachedWordsCount = ttsManager.getCachedWordsCount()
                                            cacheSizeMB = ttsManager.getCacheSizeMB()
                                            downloadStatusText = "✓ ${tbTitle}发音包已全部离线完成！"
                                        }
                                    },
                                    enabled = !isDownloading,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudDownload,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "一键预下载教材发音",
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }

                                if (cachedWordsCount > 0) {
                                    OutlinedButton(
                                        onClick = {
                                            ttsManager.clearCache()
                                            cachedWordsCount = ttsManager.getCachedWordsCount()
                                            cacheSizeMB = ttsManager.getCacheSizeMB()
                                            downloadStatusText = "缓存已清空"
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeleteOutline,
                                            contentDescription = null,
                                            tint = SecondaryText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 系统 TTS 引擎状态与系统设置引导
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PageBackground,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "当前系统 TTS 引擎",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TitleText
                                )
                                Text(
                                    text = currentEngineName,
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "提示：部分手机内置默认 TTS（如早期 Pico 引擎）发音较生硬。建议进入系统设置安装「Google 语音服务」或下载高清离线语音包，可获得出色的发音效果。",
                                fontSize = 11.sp,
                                color = SecondaryText,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { ttsManager.openSystemTtsSettings(context) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "打开系统 TTS 语音设置",
                                    fontSize = 13.sp,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }

                    // 试听按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                ttsManager.voiceSource = VoiceSource.REAL_HUMAN
                                ttsManager.speak(
                                    text = "watermelon",
                                    onStart = {},
                                    onDone = {
                                        Toast.makeText(context, "真人原声播放完毕：watermelon", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LightBlueBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "试听真人原声",
                                fontSize = 12.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                ttsManager.voiceSource = VoiceSource.SYSTEM_TTS
                                ttsManager.speak(
                                    text = "watermelon",
                                    onStart = {},
                                    onDone = {
                                        Toast.makeText(context, "系统 TTS 播放完毕：watermelon", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LightBlueBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "试听系统 TTS",
                                fontSize = 12.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 3. Learning Guarantees & Features Card
            RoundedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "保障与说明",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitleText
                    )

                    SettingItemRow(
                        icon = Icons.Filled.Security,
                        title = "离线保护",
                        desc = "核心功能完全本地离线运行，保护儿童隐私安全"
                    )

                    SettingItemRow(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = "双发音引擎与英美双音",
                        desc = "支持真人原声与原生 TextToSpeech 双引擎自适应切换"
                    )

                    SettingItemRow(
                        icon = Icons.Filled.Info,
                        title = "软件版本",
                        desc = "听写小助手 V2.0.0 (高清真人语音 + 小学教材全覆盖版)"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingItemRow(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LightBlueBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TitleText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
    }
}
