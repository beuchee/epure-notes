package com.alain.epure.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.alain.epure.notes.ui.PlaceholderScreen
import com.alain.epure.notes.ui.theme.Black
import com.alain.epure.notes.ui.theme.EpureNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Barres système noires, icônes claires, quel que soit le thème du téléphone.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Black.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Black.toArgb())
        )
        setContent {
            EpureNotesTheme {
                PlaceholderScreen()
            }
        }
    }
}
