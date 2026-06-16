package com.example.garden.appsettings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

// Расширение для контекста, чтобы обращаться к хранилищу из любого места
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "anime_settings")

data class AnimeSettingsState(
    var showChildsName: Boolean = false,
    var childsNamePosition: Int = 0,
)

data class SettingsState(
    var showChildsName: Boolean = false,
    var childsNamePosition: Int = 0,
)

class SettingsManager(private val context: Context) {

    companion object {
        // Ключи для хранения данных
        val SHOW_CHILDS_NAME = booleanPreferencesKey("show_childs_name")
        val CHILDS_NAME_POSITION = intPreferencesKey("childs_name_position")
    }

    // Чтение настроек (возвращает поток данных Flow)
    val showChildsName: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_CHILDS_NAME] ?: false // По умолчанию false
        }

    val childsNamePosition: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[CHILDS_NAME_POSITION] ?: 0 // По умолчанию 0 (например, снизу)
        }

    // Внутри SettingsManager
    val settingsStateFlow: Flow<SettingsState> = combine(
        showChildsName,
        childsNamePosition
    ) { show, position ->
        SettingsState(show, position)
    }

    // Запись настроек (suspend функции для вызова из CoroutineScope)
    suspend fun setShowChildsName(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_CHILDS_NAME] = show
        }
    }

    suspend fun setChildsNamePosition(position: Int) {
        context.dataStore.edit { preferences ->
            preferences[CHILDS_NAME_POSITION] = position
        }
    }
}