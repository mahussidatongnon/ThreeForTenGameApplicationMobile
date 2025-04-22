package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "player_data")

class PlayerDataStoreManager(private val context: Context) {

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username_key")
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
        }
    }

    fun loadUsername(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[USERNAME_KEY]
        }
    }
}
