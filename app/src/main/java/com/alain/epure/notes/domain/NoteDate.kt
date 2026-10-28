package com.alain.epure.notes.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm", Locale.FRENCH)
private val DAY_MONTH_FORMAT = DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH)
private val DAY_MONTH_YEAR_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.FRENCH)

// Date affichée dans la liste des notes (SPEC F-6.2) :
// « 16:30 » si c'est aujourd'hui, « 28 oct. » si c'est cette année, « 28 oct. 2025 » sinon.
fun formatNoteDate(modified: LocalDateTime, now: LocalDateTime): String = when {
    modified.toLocalDate() == now.toLocalDate() -> modified.format(TIME_FORMAT)
    modified.year == now.year -> modified.format(DAY_MONTH_FORMAT)
    else -> modified.format(DAY_MONTH_YEAR_FORMAT)
}
