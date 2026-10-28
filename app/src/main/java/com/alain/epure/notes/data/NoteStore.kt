package com.alain.epure.notes.data

import androidx.core.util.AtomicFile
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Dépôt des notes : un fichier texte `.md` par note, dans un dossier privé de l'app (DEC-0007).
// Le nom du fichier vient de la date de création ; le titre n'est jamais dans le nom.
class NoteStore(private val directory: File) {

    init {
        directory.mkdirs()
    }

    // Nouvel identifiant (nom du fichier sans extension). Rien n'est écrit tant que la note n'a pas de texte.
    fun create(now: LocalDateTime = LocalDateTime.now()): String {
        val base = now.format(FILE_NAME_FORMAT)
        var id = base
        var suffix = 2
        while (file(id).exists()) {
            id = "$base-$suffix"
            suffix++
        }
        return id
    }

    fun read(id: String): String {
        val file = file(id)
        return if (file.exists()) file.readText(Charsets.UTF_8) else ""
    }

    // Écriture atomique : une coupure pendant l'enregistrement ne corrompt jamais la note (SPEC F-3.2).
    // Une note vide n'est jamais conservée (SPEC F-3.4) : elle est supprimée.
    fun save(id: String, text: String) {
        if (text.isBlank()) {
            delete(id)
            return
        }
        val atomicFile = AtomicFile(file(id))
        val output = atomicFile.startWrite()
        try {
            output.write(text.toByteArray(Charsets.UTF_8))
            atomicFile.finishWrite(output)
        } catch (error: IOException) {
            atomicFile.failWrite(output)
            throw error
        }
    }

    // Identifiant de la note modifiée en dernier, ou null s'il n'existe aucune note (SPEC F-1.1).
    fun latestId(): String? =
        directory.listFiles { file -> file.extension == NOTE_EXTENSION }
            ?.maxByOrNull { it.lastModified() }
            ?.nameWithoutExtension

    fun delete(id: String) {
        file(id).delete()
    }

    private fun file(id: String) = File(directory, "$id.$NOTE_EXTENSION")

    private companion object {
        const val NOTE_EXTENSION = "md"
        val FILE_NAME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")
    }
}
