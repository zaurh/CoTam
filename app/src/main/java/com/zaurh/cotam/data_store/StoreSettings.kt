package com.zaurh.cotam.data_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("cotam")


class StoreSettings(private val context: Context) {

    private val dataStore = context.dataStore


    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val getDarkMode: Flow<Boolean?> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: false
        }

    suspend fun saveDarkMode(switched: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = switched
        }
    }

}