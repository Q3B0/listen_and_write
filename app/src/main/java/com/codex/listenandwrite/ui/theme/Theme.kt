package com.codex.listenandwrite.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = CardBackground,
    primaryContainer = LightBlueBg,
    onPrimaryContainer = PrimaryDarkBlue,
    secondary = PrimaryDarkBlue,
    onSecondary = CardBackground,
    background = PageBackground,
    onBackground = TitleText,
    surface = CardBackground,
    onSurface = TitleText,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = BodyText,
    outline = BorderOutline,
    outlineVariant = DividerColor,
    error = ErrorRed,
    onError = CardBackground,
    errorContainer = ErrorLightBg,
    onErrorContainer = ErrorRed
)

@Composable
fun ListenAndWriteTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
