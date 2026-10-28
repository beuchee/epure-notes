package com.alain.epure.notes.ui

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alain.epure.notes.domain.DEFAULT_TEXT_SIZE
import com.alain.epure.notes.domain.nextTextSize
import com.alain.epure.notes.data.SettingsStore
import com.alain.epure.notes.system.BackupFolder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// État de la sauvegarde pour l'écran Réglages : active ou non, et nom du dossier choisi (SPEC F-18.3).
data class BackupSetting(val active: Boolean, val folderName: String?)

class SettingsViewModel(private val settings: SettingsStore, backup: BackupFolder) : ViewModel() {

    val backupSetting: StateFlow<BackupSetting> = settings.backupFolder
        .map { uri ->
            BackupSetting(
                active = uri != null,
                folderName = uri?.let { backup.folderName(it.toUri()) }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), BackupSetting(false, null))

    val textSize: StateFlow<Int> = settings.textSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), DEFAULT_TEXT_SIZE)

    // Un toucher passe à la taille suivante (SPEC F-8.1).
    fun cycleTextSize() {
        viewModelScope.launch { settings.setTextSize(nextTextSize(textSize.value)) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
