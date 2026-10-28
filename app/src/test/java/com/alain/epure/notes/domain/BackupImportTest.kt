package com.alain.epure.notes.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class BackupImportTest {

    @Test
    fun onlyNoteNamedFilesWithTextAreValid() {
        val files = listOf(
            "2026-10-28-163012.md" to "Une note",
            "taches.md" to "- [ ] appeler",
            "2026-10-28-163012-2.md" to "   ",
            "notes-perso.md" to "texte",
            "2026-10-28-163012.txt" to "pas une note"
        )
        assertEquals(listOf("2026-10-28-163012" to "Une note"), validNoteFiles(files))
    }

    @Test
    fun existingNotesAreNeverOverwritten() {
        val files = listOf(
            "2026-10-01-090000.md" to "ancienne version",
            "2026-10-02-090000.md" to "nouvelle"
        )
        val existing = setOf("2026-10-01-090000")
        assertEquals(listOf("2026-10-02-090000" to "nouvelle"), notesToImport(existing, files))
    }

    @Test
    fun emptyFolderImportsNothing() {
        assertEquals(emptyList<Pair<String, String>>(), notesToImport(emptySet(), emptyList()))
    }
}
