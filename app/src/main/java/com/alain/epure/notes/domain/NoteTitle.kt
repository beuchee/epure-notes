package com.alain.epure.notes.domain

// Titre d'une note (SPEC F-4.1) : sa première ligne non vide, sans espaces de début et de fin.
// Recalculé à chaque fois à partir du texte, jamais stocké (F-4.2). Vide si la note n'a que des espaces.
fun noteTitle(text: String): String =
    text.lineSequence()
        .map { it.trim() }
        .firstOrNull { it.isNotEmpty() }
        .orEmpty()
