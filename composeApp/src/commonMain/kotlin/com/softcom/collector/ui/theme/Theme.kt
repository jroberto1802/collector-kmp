package com.softcom.collector.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CollectorOrange = Color(0xFFFF6E07)
val CollectorText = Color(0xFF1F1F1F)
val CollectorMuted = Color(0xFF8A8A8A)
val CollectorBorder = Color(0xFFE1E4E8)
val CollectorSurface = Color(0xFFF5F4F3)

private val CollectorColors = lightColorScheme(
    primary = CollectorOrange,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = CollectorText,
    surface = Color.White,
    onSurface = CollectorText,
    outline = CollectorBorder,
    error = Color(0xFFB3261E),
)

@Composable
fun CollectorTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CollectorColors, content = content)
}
