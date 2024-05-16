package com.indopay.qrissapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indopay.qrissapp.core.data.datastore.DataStorePreference
import com.indopay.qrissapp.domain.usecase.QrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: QrisUseCase,
    private val dataStorePreference: DataStorePreference
): ViewModel(){

    fun getHomeInformationFromProfile(token: String, email: String) =
        useCase.getProfileMerchant(token, email)

    fun getListLastTrx(
        token: String,
        email: String,
        mId: String,
    ) = useCase.getLastTransaction(token, email, mId)

    fun getEmailFromDataStore() : Flow<String> {
        return dataStorePreference.readEmailFromDataStore()
    }
    fun getTokenFromDataStore() : Flow<String> {
        return dataStorePreference.readTokenFromDataStore()
    }

    fun saveMerchantIdToDataStore(mId: String) {
        viewModelScope.launch {
            dataStorePreference.saveMidToDataStore(mId)
        }
    }

    fun logoutApplication() {
        viewModelScope.launch { dataStorePreference.logout() }
    }

}