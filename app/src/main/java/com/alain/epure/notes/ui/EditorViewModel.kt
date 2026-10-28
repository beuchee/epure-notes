package com.alain.epure.notes.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alain.epure.notes.data.NoteStore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

// Porte la note en cours. Le texte vit ici (et non dans l'écran) pour survivre à une rotation.
class EditorViewModel(private val store: NoteStore) : ViewModel() {

    val textState = TextFieldState()

    private var noteId: String

    init {
        noteId = store.latestId() ?: store.create()
        textState.setTextAndPlaceCursorAtEnd(store.read(noteId))
        saveAfterPause()
    }

    // Enregistrement après une pause de frappe (SPEC F-3.1).
    @OptIn(FlowPreview::class)
    private fun saveAfterPause() {
        viewModelScope.launch {
            snapshotFlow { textState.text.toString() }
                .drop(1)
                .debounce(SAVE_DELAY_MS)
                .collect { saveNow() }
        }
    }

    // Appelé aussi à l'arrêt de l'app (SPEC F-3.1, onStop).
    fun saveNow() {
        store.save(noteId, textState.text.toString())
    }

    // Nouvelle note vide ; sans effet si la note en cours est déjà vide (SPEC F-5.2).
    fun newNote() {
        if (textState.text.isBlank()) return
        saveNow()
        noteId = store.create()
        textState.setTextAndPlaceCursorAtEnd("")
    }

    override fun onCleared() {
        saveNow()
    }

    private companion object {
        const val SAVE_DELAY_MS = 1_000L
    }
}
