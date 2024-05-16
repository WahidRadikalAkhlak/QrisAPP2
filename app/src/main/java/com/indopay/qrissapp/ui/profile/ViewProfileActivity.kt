package com.indopay.qrissapp.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.indopay.qrissapp.R
import com.indopay.qrissapp.core.network.utils.ErrorCode
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.databinding.ActivityViewProfileBinding
import com.indopay.qrissapp.ui.model.RequestUser
import com.indopay.qrissapp.utils.ConnectionDetector
import com.indopay.qrissapp.utils.DataIntent
import com.indopay.qrissapp.utils.DialogLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewProfileBinding

    private lateinit var token: String
    private lateinit var merchantEmail: String
    private lateinit var mid: String
    private lateinit var bodyUsername: RequestUser

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var dialogLoading: DialogLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connectionDetector = ConnectionDetector(this)
        dialogLoading = DialogLoading(this)

        getProfileMerchant()
    }

    private fun getProfileMerchant() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                merchantEmail = intent.extras?.getString(DataIntent.EMAIL_MERCHANT).toString()
                mid = intent.extras?.getString(DataIntent.MID_MERCHANT).toString()
                token = intent.extras?.getString(DataIntent.TOKEN_MERCHANT).toString()
                profileViewModel.getProfileMerchant(token, merchantEmail)
                    .distinctUntilChanged()
                    .collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                dialogLoading.startDialogLoading()
                            }

                            is Resource.Success -> {
                                dialogLoading.dismissDialog()
                                val item = result.data
                                with(binding) {
                                    merchantIdProfile.text = item?.mID ?: mid
                                    merchantNameProfile.text = item?.merchantName
                                    merchantEmailProfile.text = item?.username ?: merchantEmail
                                    merchantProvince.text = item?.province
                                    district.text = item?.district
                                    merchantAddressProfile.text = StringBuilder()
                                        .append(item?.subdistrict)
                                        .append(item?.kelurahan)
                                        .append(item?.posCode)
                                        .append(item?.address1)
                                        .append(item?.address2)
                                    jumlahTrxProfile.text = item?.jmlTrx
                                    amount.text = item?.amount
                                }

                                Toast.makeText(
                                    this@ViewProfileActivity,
                                    "Membuka Profile merchant ${result.data?.message} response code ${result.data?.status}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is Resource.Error -> {
                                dialogLoading.dismissDialog()
                                when (result.statusCode) {
                                    ErrorCode.ERR_INTERNET_CONNECTION -> {
                                        Toast.makeText(
                                            this@ViewProfileActivity,
                                            "No connection internet ${result.message} response code error ${result.statusCode}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    ErrorCode.ERR_EXCEPTION_CODE -> {
                                        Toast.makeText(
                                            this@ViewProfileActivity,
                                            "${result.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    else -> {
                                        Snackbar.make(
                                            binding.root,
                                            "Data tidak berhasil diperbaharui ${result.message}, response code ${result.statusCode}",
                                            Snackbar.LENGTH_SHORT
                                        ).setTextColor(getColor(R.color.white))
                                            .setBackgroundTint(getColor(androidx.cardview.R.color.cardview_dark_background)).show()
                                    }
                                }

                            }
                        }
                    }
            }
        }
    }
}