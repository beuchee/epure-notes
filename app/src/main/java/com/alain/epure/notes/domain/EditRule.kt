package com.alain.epure.notes.domain

// Règle « Effacer : non » (SPEC F-8.3, DEC-0015) : une modification est acceptée si elle ne retire aucun caractère.
// Une insertion, ou un remplacement qui ne raccourcit pas le texte, reste permis.
fun isEditAllowed(oldLength: Int, newLength: Int, deletionAllowed: Boolean): Boolean =
    deletionAllowed || newLength >= oldLength
