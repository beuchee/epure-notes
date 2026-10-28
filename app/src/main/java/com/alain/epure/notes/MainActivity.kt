package com.alain.epure.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alain.epure.notes.ui.EditorScreen
import com.alain.epure.notes.ui.EditorViewModel
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
        val container = AppContainer(filesDir)
        val editorViewModel = ViewModelProvider(
            this,
            viewModelFactory { initializer { EditorViewModel(container.noteStore) } }
        )[EditorViewModel::class.java]
        // Enregistrement à l'arrêt de l'app (SPEC F-3.1).
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) editorViewModel.saveNow()
        })
        setContent {
            EpureNotesTheme {
                EditorScreen(editorViewModel)
            }
        }
    }
}
