package com.indopay.qrissapp.core.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.indopay.qrissapp.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


const val PREF_NAME = "DATASTOREPREF"

class DataStorePreference(private val context: Context) {

    private object PreferenceKey {
        val DYNAMIC = stringPreferencesKey(DYNAMIC_KEY)
        val STATE_LOGIN = booleanPreferencesKey(LOGIN_KEY)
        val TOKEN_ACCESS = stringPreferencesKey(TOKEN_KEY)
        val EMAIL = stringPreferencesKey(EMAIL_KEY)
        val MID = stringPreferencesKey(MID_KEY)
    }

    suspend fun saveMidToDataStore(mid: String) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferenceKey.MID] = mid
        }
    }

    fun readMidFromDataStore(): Flow<String> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("MidDataStore", exception.message.toString())
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { value ->
                value[PreferenceKey.MID] ?: "none"
            }
    }

    suspend fun saveEmailToDataStore(email: String) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferenceKey.EMAIL] = email
        }
    }

    suspend fun saveDynamicToDataStore(qrString: String) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferenceKey.DYNAMIC] = qrString
        }
    }


    fun readEmailFromDataStore(): Flow<String> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("EmailDataStore", exception.message.toString())
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { value ->
                value[PreferenceKey.EMAIL] ?: "none"
            }
    }

    suspend fun saveTokenToDataStore(token: String) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferenceKey.TOKEN_ACCESS] = token
        }
    }

    fun readTokenFromDataStore(): Flow<String> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("TokenDataStore", exception.message.toString())
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { value ->
                value[PreferenceKey.TOKEN_ACCESS] ?: "none"
            }
    }

    fun readDynamicQrFromDataStore(): Flow<String> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("TokenDataStore", exception.message.toString())
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { value ->
                value[PreferenceKey.DYNAMIC] ?: "none"
            }
    }


    suspend fun saveLoginStateDataStore(state: Boolean = true) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferenceKey.STATE_LOGIN] = state
        }
    }

    val readStateLoginFromDataStore: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { value ->
            value[PreferenceKey.STATE_LOGIN] ?: false
        }

    suspend fun logout() {
        context.dataStore.edit {
            it.clear()
        }
    }

    companion object {
        const val DYNAMIC_KEY = "dynamic_key"
        const val LOGIN_KEY = "login_key"
        const val TOKEN_KEY = "token_key"
        const val EMAIL_KEY = "email_key"
        const val MID_KEY = "mid_key"
    }
}