package com.indopay.qrissapp.ui.qr.dynamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.indopay.qrissapp.core.data.datastore.DataStorePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DynamicQrShowViewModel @Inject constructor(
    val dataStorePreference: DataStorePreference
): ViewModel() {

    fun getcodestring() =
            dataStorePreference.readDynamicQrFromDataStore().asLiveData()
}