package com.example.myuserstory.data.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myuserstory.data.remote.response.SignInResult
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

class UserPreferenceDatastore private constructor(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("User")

    suspend fun saveUser(name: String, userId: String, userToken: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME] = name
            preferences[USER_ID] = userId
            preferences[USER_TOKEN] = userToken
        }
    }

    fun getUser(): Flow<SignInResult> {
        return context.dataStore.data.map { preferences ->
            SignInResult(
                preferences[NAME] ?: "",
                preferences[USER_ID] ?: "",
                preferences[USER_TOKEN]?: ""
            )
        }
    }

    suspend fun signout() {
        context.dataStore.edit { preferences ->
            preferences[NAME] = ""
            preferences[USER_ID] = ""
            preferences[USER_TOKEN] = ""
        }
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: UserPreferenceDatastore? = null

        private val NAME = stringPreferencesKey("name")
        private val USER_ID = stringPreferencesKey("userId")
        private val USER_TOKEN = stringPreferencesKey("token")

        fun getInstance(context: Context): UserPreferenceDatastore {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferenceDatastore(context)
                INSTANCE = instance
                instance
            }
        }
    }
}