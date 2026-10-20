package com.codex.listenandwrite.ui.unit

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.codex.listenandwrite.data.entity.UnitEntity
import com.codex.listenandwrite.ui.components.AppTopBar
import com.codex.listenandwrite.ui.components.PrimaryButton
import com.codex.listenandwrite.ui.components.RoundedCard
import com.codex.listenandwrite.ui.theme.BorderOutline
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.DividerColor
import com.codex.listenandwrite.ui.theme.PageBackground
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText
import com.codex.listenandwrite.ui.theme.TitleText

@Composable
fun UnitSelectionScreen(
    viewModel: UnitSelectionViewModel,
    onBackClick: () -> Unit,
    onNextClick: (textbookId: String, selectedUnitIds: List<String>) -> Unit
) {
    val textbook by viewModel.currentTextbook.collectAsState()
    val units by viewModel.units.collectAsState()
    val selectedUnitIds by viewModel.selectedUnitIds.collectAsState()

    val isAllSelected = units.isNotEmpty() && selectedUnitIds.size == units.size
    val selectedCount = selectedUnitIds.size
    val totalWords = viewModel.selectedWordCount

    Scaffold(
        topBar = {
            AppTopBar(
                title = "选择单元",
                onBackClick = onBackClick
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
                        .padding(horizontal = Dimensions.ScreenHorizontalPadding, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "已选择 $selectedCount 个单元 · $totalWords 词",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TitleText
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    PrimaryButton(
                        text = "下一步",
                        enabled = selectedCount > 0,
                        onClick = {
                            textbook?.let { tb ->
                                onNextClick(tb.id, selectedUnitIds.toList())
                            }
                        }
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
                .padding(horizontal = Dimensions.ScreenHorizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Subheader: Textbook name & "全选" checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = textbook?.title ?: "三年级 下册",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleText
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.toggleSelectAll() }
                ) {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = { viewModel.toggleSelectAll() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryBlue,
                            uncheckedColor = SecondaryText
                        )
                    )
                    Text(
                        text = "全选",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TitleText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Unit Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.ListItemSpacing)
            ) {
                items(units, key = { it.id }) { unit ->
                    val isChecked = selectedUnitIds.contains(unit.id)
                    UnitCardItem(
                        unit = unit,
                        isChecked = isChecked,
                        onToggle = { viewModel.toggleUnit(unit.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun UnitCardItem(
    unit: UnitEntity,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    RoundedCard(
        modifier = Modifier.fillMaxWidth(),
        isSelected = false,
        unselectedBorderColor = if (isChecked) PrimaryBlue.copy(alpha = 0.5f) else BorderOutline,
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unit.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TitleText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${unit.wordCount} 词",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            }

            Checkbox(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue,
                    uncheckedColor = BorderOutline
                )
            )
        }
    }
}
