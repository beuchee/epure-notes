package com.alain.epure.notes.ui

import androidx.lifecycle.ViewModel
import com.alain.epure.notes.data.NoteStore
import com.alain.epure.notes.domain.formatNoteDate
import com.alain.epure.notes.domain.matchesSearch
import com.alain.epure.notes.domain.noteTitle
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Une ligne de la liste « Notes ».
data class NoteRow(val id: String, val title: String, val text: String, val dateLabel: String)

// Liste des notes, filtrée par la recherche (SPEC F-11). Rechargée chaque fois que l'écran s'ouvre.
class NotesViewModel(private val store: NoteStore) : ViewModel() {

    private var allRows: List<NoteRow> = emptyList()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _rows = MutableStateFlow<List<NoteRow>>(emptyList())
    val rows: StateFlow<List<NoteRow>> = _rows.asStateFlow()

    fun refresh() {
        val now = LocalDateTime.now()
        allRows = store.summaries().map { note ->
            NoteRow(
                id = note.id,
                title = noteTitle(note.text),
                text = note.text,
                dateLabel = formatNoteDate(note.modified, now)
            )
        }
        applyFilter()
    }

    fun setQuery(query: String) {
        _query.value = query
        applyFilter()
    }

    private fun applyFilter() {
        _rows.value = allRows.filter { matchesSearch(it.text, _query.value) }
    }
}
