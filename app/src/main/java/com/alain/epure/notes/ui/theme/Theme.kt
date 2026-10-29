package com.alain.epure.notes.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Les trois jetons d'un thème : fond, texte principal, texte secondaire.
data class NoteColors(val background: Color, val textPrimary: Color, val textSecondary: Color)

val NightColors = NoteColors(Black, TextPrimary, TextSecondary)
val DayColors = NoteColors(DayBackground, DayTextPrimary, DayTextSecondary)

// Les écrans lisent leurs couleurs ici : LocalNoteColors.current.textPrimary, etc.
val LocalNoteColors = staticCompositionLocalOf { NightColors }

// Pas de couleur dynamique (charte A.5). Le thème Nuit est celui par défaut ; le thème Jour est une
// dérogation décidée (DEC-0009). Les menus et dialogues prennent le fond du thème, sans teinte.
@Composable
fun EpureNotesTheme(day: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (day) DayColors else NightColors
    CompositionLocalProvider(LocalNoteColors provides colors) {
        MaterialTheme(
            colorScheme = noteColorScheme(colors, day),
            typography = Typography,
            content = content
        )
    }
}

private fun noteColorScheme(colors: NoteColors, day: Boolean): ColorScheme {
    val base = if (day) lightColorScheme() else darkColorScheme()
    return base.copy(
        primary = colors.textPrimary,
        onPrimary = colors.background,
        background = colors.background,
        onBackground = colors.textPrimary,
        surface = colors.background,
        onSurface = colors.textPrimary,
        onSurfaceVariant = colors.textSecondary,
        surfaceContainerLowest = colors.background,
        surfaceContainerLow = colors.background,
        surfaceContainer = colors.background,
        surfaceContainerHigh = colors.background,
        surfaceContainerHighest = colors.background
    )
}
