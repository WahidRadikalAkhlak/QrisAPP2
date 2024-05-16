package com.indopay.qrissapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.indopay.qrissapp.core.data.datastore.PREF_NAME
import dagger.hilt.android.HiltAndroidApp

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREF_NAME)

@HiltAndroidApp
class QrisApp : Application()