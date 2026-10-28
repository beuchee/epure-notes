package com.alain.epure.notes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Thème sombre seul : pas de couleur dynamique, pas de thème clair (charte A.5, sauf DEC-0009 à venir).
private val NightColorScheme = darkColorScheme(
    primary = TextPrimary,
    onPrimary = Black,
    background = Black,
    onBackground = TextPrimary,
    surface = Black,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

@Composable
fun EpureNotesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NightColorScheme,
        typography = Typography,
        content = content
    )
}
