package com.alain.epure.notes

import android.content.Context
import com.alain.epure.notes.data.NoteStore
import com.alain.epure.notes.data.SettingsStore
import com.alain.epure.notes.data.TaskStore
import com.alain.epure.notes.system.BackupFolder
import java.io.File

// Point unique où sont créées les dépendances de l'app. Pas de framework d'injection (CLAUDE.md).
class AppContainer(context: Context) {
    val noteStore = NoteStore(File(context.filesDir, "notes"))
    val settings = SettingsStore(context)
    val taskStore = TaskStore(File(context.filesDir, "taches.md"))
    val backup = BackupFolder(context.contentResolver)
}
