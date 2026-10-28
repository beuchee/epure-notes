package com.alain.epure.notes.domain

// Texte reçu d'une autre app (SPEC F-10.1) : le sujet éventuel devient la première ligne de la note.
fun sharedNoteText(subject: String?, text: String): String {
    val title = subject?.trim().orEmpty()
    return when {
        title.isEmpty() -> text
        text.isBlank() -> title
        else -> "$title\n$text"
    }
}
