package com.alain.epure.notes.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.alain.epure.notes.data.NoteStore
import com.alain.epure.notes.data.SettingsStore
import com.alain.epure.notes.domain.DEFAULT_TEXT_SIZE
import com.alain.epure.notes.domain.firstMatchRange
import androidx.compose.ui.text.TextRange
import com.alain.epure.notes.system.BackupFolder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

// Porte la note en cours. Le texte vit ici (et non dans l'écran) pour survivre à une rotation.
class EditorViewModel(
    private val store: NoteStore,
    private val backup: BackupFolder,
    settings: SettingsStore
) : ViewModel() {

    val textState = TextFieldState()

    // Taille du texte, réglée dans Réglages (SPEC F-8.1).
    val textSize: StateFlow<Int> = settings.textSize
        .stateIn(viewModelScope, SharingStarted.Eagerly, DEFAULT_TEXT_SIZE)

    // « Effacer : oui » ou « non » (SPEC F-8.3).
    val deletionAllowed: StateFlow<Boolean> = settings.deletionAllowed
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

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
        val text = textState.text.toString()
        store.save(noteId, text)
        // Copie de sauvegarde à chaque enregistrement (SPEC F-18.4) ; une note vide n'a pas de copie.
        if (text.isBlank()) backup.delete(noteId) else backup.write(noteId, text)
    }

    // Nouvelle note vide ; sans effet si la note en cours est déjà vide (SPEC F-5.2).
    fun newNote() {
        if (textState.text.isBlank()) return
        saveNow()
        noteId = store.create()
        textState.setTextAndPlaceCursorAtEnd("")
    }

    // Ouvre une note choisie dans la liste (SPEC F-6.3). La note en cours est enregistrée avant.
    // Avec une recherche, la première occurrence est sélectionnée (SPEC F-11.3).
    fun openNote(id: String, query: String = "") {
        saveNow()
        noteId = id
        val text = store.read(id)
        textState.setTextAndPlaceCursorAtEnd(text)
        firstMatchRange(text, query)?.let { range ->
            textState.edit { selection = TextRange(range.first, range.last + 1) }
        }
    }

    // Nouvelle note contenant un texte reçu d'une autre app (SPEC F-10.1). Rien si le texte est vide.
    fun newNoteWith(content: String) {
        if (content.isBlank()) return
        saveNow()
        noteId = store.create()
        textState.setTextAndPlaceCursorAtEnd(content)
        saveNow()
    }

    // Si la note supprimée est celle qui est ouverte, on passe à la plus récente ou à une note vide (SPEC F-6.5).
    fun onNoteDeleted(id: String) {
        if (id != noteId) return
        noteId = store.latestId() ?: store.create()
        textState.setTextAndPlaceCursorAtEnd(store.read(noteId))
    }

    override fun onCleared() {
        saveNow()
    }

    private companion object {
        const val SAVE_DELAY_MS = 1_000L
    }
}
