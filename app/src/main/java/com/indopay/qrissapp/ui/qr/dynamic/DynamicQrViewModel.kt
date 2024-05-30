package com.indopay.qrissapp.ui.qr.dynamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indopay.qrissapp.core.data.datastore.DataStorePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DynamicQrViewModel @Inject constructor(
    val dataStorePreference: DataStorePreference
): ViewModel() {

    fun savecodestring (qrString: String){
        viewModelScope.launch {
            dataStorePreference.saveDynamicToDataStore(qrString)
        }
    }

}