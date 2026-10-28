package com.alain.epure.notes.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alain.epure.notes.domain.DEFAULT_TEXT_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Réglages de l'app (DataStore Preferences, DEC-0004, DEC-0026).
class SettingsStore(context: Context) {

    private val dataStore = context.settingsDataStore

    // Adresse du dossier de sauvegarde choisi, ou null si la sauvegarde est désactivée (SPEC F-18.3).
    val backupFolder: Flow<String?> = dataStore.data.map { it[BACKUP_FOLDER] }

    suspend fun setBackupFolder(uri: String?) {
        dataStore.edit { prefs ->
            if (uri == null) prefs.remove(BACKUP_FOLDER) else prefs[BACKUP_FOLDER] = uri
        }
    }

    // Taille du texte de l'éditeur, en sp (SPEC F-8.1).
    val textSize: Flow<Int> = dataStore.data.map { it[TEXT_SIZE] ?: DEFAULT_TEXT_SIZE }

    suspend fun setTextSize(size: Int) {
        dataStore.edit { prefs -> prefs[TEXT_SIZE] = size }
    }

    private companion object {
        val BACKUP_FOLDER = stringPreferencesKey("backup_folder")
        val TEXT_SIZE = intPreferencesKey("text_size")
    }
}
