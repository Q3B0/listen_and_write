package com.codex.listenandwrite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codex.listenandwrite.ui.theme.BorderOutline
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.Dimensions
import com.codex.listenandwrite.ui.theme.PrimaryBlue

@Composable
fun RoundedCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardBackground,
    cornerRadius: Dp = Dimensions.CardCornerRadius,
    isSelected: Boolean = false,
    selectedBorderColor: Color = PrimaryBlue,
    unselectedBorderColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val borderStroke = when {
        isSelected -> BorderStroke(2.dp, selectedBorderColor)
        unselectedBorderColor != null -> BorderStroke(1.dp, unselectedBorderColor)
        else -> null
    }

    val shape = RoundedCornerShape(cornerRadius)

    Surface(
        modifier = modifier
            .clip(shape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        shape = shape,
        color = backgroundColor,
        border = borderStroke,
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Box(modifier = Modifier.padding(Dimensions.CardInternalPadding)) {
            content()
        }
    }
}
