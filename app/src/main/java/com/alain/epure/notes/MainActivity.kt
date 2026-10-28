package com.alain.epure.notes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alain.epure.notes.domain.sharedNoteText
import com.alain.epure.notes.ui.AboutScreen
import com.alain.epure.notes.ui.EditorScreen
import com.alain.epure.notes.ui.EditorViewModel
import com.alain.epure.notes.ui.NotesScreen
import com.alain.epure.notes.ui.NotesViewModel
import com.alain.epure.notes.ui.SettingsScreen
import com.alain.epure.notes.ui.SettingsViewModel
import com.alain.epure.notes.ui.theme.Black
import com.alain.epure.notes.ui.theme.EpureNotesTheme
import kotlinx.coroutines.launch

// Écrans de l'app. Navigation par état simple (CLAUDE.md), sans bibliothèque de navigation.
enum class Screen { Editor, Notes, Settings, About }

class MainActivity : ComponentActivity() {

    // Texte reçu d'une autre app, en attente d'être mis dans une nouvelle note.
    private var sharedText by mutableStateOf<String?>(null)

    private lateinit var container: AppContainer
    private lateinit var editorViewModel: EditorViewModel
    private lateinit var pickBackupFolder: ActivityResultLauncher<Uri?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Barres système noires, icônes claires, quel que soit le thème du téléphone.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Black.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Black.toArgb())
        )
        container = AppContainer(applicationContext)
        editorViewModel = ViewModelProvider(
            this,
            viewModelFactory { initializer { EditorViewModel(container.noteStore, container.backup) } }
        )[EditorViewModel::class.java]
        val notesViewModel = ViewModelProvider(
            this,
            viewModelFactory { initializer { NotesViewModel(container.noteStore) } }
        )[NotesViewModel::class.java]
        val settingsViewModel = ViewModelProvider(
            this,
            viewModelFactory { initializer { SettingsViewModel(container.settings, container.backup) } }
        )[SettingsViewModel::class.java]

        // Le choix du dossier de sauvegarde passe par le sélecteur du système (SPEC F-18.3).
        pickBackupFolder = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) enableBackup(uri)
        }
        // Le dossier mémorisé est relu au démarrage, pour que les copies reprennent.
        lifecycleScope.launch {
            container.settings.backupFolder.collect { uri ->
                container.backup.treeUri = uri?.let { it.toUri() }
            }
        }

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
                    Screen.Notes -> NotesScreen(
                        viewModel = notesViewModel,
                        onOpenNote = { id ->
                            editorViewModel.openNote(id)
                            screen = Screen.Editor
                        },
                        onDeleteNote = { id ->
                            container.noteStore.delete(id)
                            container.backup.delete(id)
                            editorViewModel.onNoteDeleted(id)
                        },
                        onBack = { screen = Screen.Editor },
                        backupActive = container.backup.treeUri != null
                    )
                    Screen.Settings -> SettingsScreen(
                        viewModel = settingsViewModel,
                        onStartBackup = { pickBackupFolder.launch(null) },
                        onStopBackup = ::disableBackup,
                        onOpenAbout = { screen = Screen.About },
                        onBack = { screen = Screen.Editor }
                    )
                    Screen.About -> AboutScreen(onBack = { screen = Screen.Settings })
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

    // Activation : mémorise le dossier et recopie toutes les notes (SPEC F-18.5).
    private fun enableBackup(uri: Uri) {
        contentResolver.takePersistableUriPermission(uri, PERMISSION_FLAGS)
        container.backup.treeUri = uri
        editorViewModel.saveNow()
        container.backup.writeAll(container.noteStore.summaries().map { it.id to it.text })
        lifecycleScope.launch { container.settings.setBackupFolder(uri.toString()) }
    }

    // Arrêt : libère l'accès au dossier ; les copies existantes restent (SPEC F-18.9).
    private fun disableBackup() {
        container.backup.treeUri?.let { uri ->
            try {
                contentResolver.releasePersistableUriPermission(uri, PERMISSION_FLAGS)
            } catch (error: SecurityException) {
                // L'accès n'était déjà plus accordé : rien à libérer.
            }
        }
        container.backup.treeUri = null
        lifecycleScope.launch { container.settings.setBackupFolder(null) }
    }

    private companion object {
        const val PERMISSION_FLAGS = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    }
}
