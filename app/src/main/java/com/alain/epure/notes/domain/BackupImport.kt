package com.alain.epure.notes.domain

// Notes valides d'un dossier de sauvegarde (SPEC F-18.7) : fichiers `.md` au nom d'une note
// (date de création, comme dans l'app) et au texte non vide. Les autres fichiers sont ignorés.
fun validNoteFiles(files: List<Pair<String, String>>): List<Pair<String, String>> =
    files.mapNotNull { (fileName, text) ->
        val id = fileName.removeSuffix(".md")
        val isNote = fileName.endsWith(".md") && NOTE_ID.matches(id) && text.isNotBlank()
        if (isNote) id to text else null
    }

// Notes à importer : les notes valides dont l'identifiant n'existe pas déjà.
// Une note déjà présente n'est jamais écrasée (SPEC F-18.7, DEC-0019).
fun notesToImport(existingIds: Set<String>, files: List<Pair<String, String>>): List<Pair<String, String>> =
    validNoteFiles(files).filter { (id, _) -> id !in existingIds }

private val NOTE_ID = Regex("""\d{4}-\d{2}-\d{2}-\d{6}(-\d+)?""")
