package com.alain.epure.notes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alain.epure.notes.domain.sharedNoteText
import com.alain.epure.notes.ui.AboutScreen
import com.alain.epure.notes.ui.EditorScreen
import com.alain.epure.notes.ui.EditorViewModel
import com.alain.epure.notes.ui.NotesScreen
import com.alain.epure.notes.ui.NotesViewModel
import com.alain.epure.notes.ui.SettingsScreen
import com.alain.epure.notes.ui.theme.Black
import com.alain.epure.notes.ui.theme.EpureNotesTheme

// Écrans de l'app. Navigation par état simple (CLAUDE.md), sans bibliothèque de navigation.
enum class Screen { Editor, Notes, Settings, About }

class MainActivity : ComponentActivity() {

    // Texte reçu d'une autre app, en attente d'être mis dans une nouvelle note.
    private var sharedText by mutableStateOf<String?>(null)

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
        val notesViewModel = ViewModelProvider(
            this,
            viewModelFactory { initializer { NotesViewModel(container.noteStore) } }
        )[NotesViewModel::class.java]
        // Enregistrement à l'arrêt de l'app (SPEC F-3.1).
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) editorViewModel.saveNow()
        })
        // Après une rotation, l'intention est encore là : on ne recrée pas la note.
        if (savedInstanceState == null) handleShareIntent(intent)
        setContent {
            EpureNotesTheme {
                var screen by rememberSaveable { mutableStateOf(Screen.Editor) }
                val shared = sharedText
                LaunchedEffect(shared) {
                    if (shared != null) {
                        editorViewModel.newNoteWith(shared)
                        screen = Screen.Editor
                        sharedText = null
                    }
                }
                when (screen) {
                    Screen.Editor -> EditorScreen(
                        viewModel = editorViewModel,
                        onOpenNotes = {
                            editorViewModel.saveNow()
                            screen = Screen.Notes
                        },
                        onOpenSettings = {
                            editorViewModel.saveNow()
                            screen = Screen.Settings
                        }
                    )
                    Screen.Settings -> SettingsScreen(
                        onOpenAbout = { screen = Screen.About },
                        onBack = { screen = Screen.Editor }
                    )
                    Screen.About -> AboutScreen(onBack = { screen = Screen.Settings })
                    Screen.Notes -> NotesScreen(
                        viewModel = notesViewModel,
                        onOpenNote = { id ->
                            editorViewModel.openNote(id)
                            screen = Screen.Editor
                        },
                        onDeleteNote = { id ->
                            container.noteStore.delete(id)
                            editorViewModel.onNoteDeleted(id)
                        },
                        onBack = { screen = Screen.Editor }
                    )
                }
            }
        }
    }

    // L'app est déjà ouverte et reçoit un nouveau texte (launchMode singleTop).
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND || intent.type != "text/plain") return
        sharedText = sharedNoteText(
            subject = intent.getStringExtra(Intent.EXTRA_SUBJECT),
            text = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
        )
    }
}
