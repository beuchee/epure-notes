package com.alain.epure.notes.data

import androidx.core.util.AtomicFile
import java.io.File
import java.io.IOException

// Fichier `taches.md` dans le stockage privé de l'app (DEC-0014). Écriture atomique (SPEC F-3.2).
class TaskStore(private val file: File) {

    fun read(): String = if (file.exists()) file.readText(Charsets.UTF_8) else ""

    fun save(content: String) {
        val atomicFile = AtomicFile(file)
        val output = atomicFile.startWrite()
        try {
            output.write(content.toByteArray(Charsets.UTF_8))
            atomicFile.finishWrite(output)
        } catch (error: IOException) {
            atomicFile.failWrite(output)
            throw error
        }
    }
}
