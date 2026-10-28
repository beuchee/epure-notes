package com.alain.epure.notes

import com.alain.epure.notes.data.NoteStore
import java.io.File

// Point unique où sont créées les dépendances de l'app. Pas de framework d'injection (CLAUDE.md).
class AppContainer(filesDir: File) {
    val noteStore = NoteStore(File(filesDir, "notes"))
}
