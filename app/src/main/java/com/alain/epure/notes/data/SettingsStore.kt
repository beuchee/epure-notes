package com.alain.epure.notes.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

    private companion object {
        val BACKUP_FOLDER = stringPreferencesKey("backup_folder")
    }
}
