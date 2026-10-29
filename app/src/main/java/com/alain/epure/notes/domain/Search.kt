package com.alain.epure.notes.domain

import java.text.Normalizer
import java.util.Locale

// Recherche insensible à la casse et aux accents (SPEC F-11.2) : « eleve » trouve « Élève ».
// Chaque caractère devient sa forme minuscule sans accent ; la position d'origine est conservée.
fun matchesSearch(text: String, query: String): Boolean =
    query.isBlank() || searchKey(text).contains(searchKey(query.trim()))

// Plage (indices du texte d'origine) de la première occurrence de la recherche, pour sélectionner (SPEC F-11.3).
fun firstMatchRange(text: String, query: String): IntRange? {
    val needle = searchKey(query.trim())
    if (needle.isEmpty()) return null
    val keys = StringBuilder()
    val positions = mutableListOf<Int>()
    text.forEachIndexed { index, char ->
        charKey(char).forEach { key ->
            keys.append(key)
            positions.add(index)
        }
    }
    val start = keys.indexOf(needle)
    if (start < 0) return null
    return positions[start]..positions[start + needle.length - 1]
}

private fun searchKey(text: String): String = text.map { charKey(it) }.joinToString("")

private fun charKey(char: Char): String =
    Normalizer.normalize(char.lowercase(Locale.ROOT), Normalizer.Form.NFD).replace(ACCENT, "")

private val ACCENT = Regex("\\p{Mn}+")
