package com.indopay.qrissapp.ui.transaction.trx_detail

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.indopay.qrissapp.R
import com.indopay.qrissapp.core.network.utils.ErrorCode
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.databinding.ActivityTransactionDetailBinding
import com.indopay.qrissapp.domain.model.DataLastTransactionItem
import com.indopay.qrissapp.domain.model.DataTrxItemByDate
import com.indopay.qrissapp.domain.model.TransactionDetail
import com.indopay.qrissapp.utils.ConnectionDetector
import com.indopay.qrissapp.utils.DataIntent
import com.indopay.qrissapp.utils.DialogLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionDetailBinding

    private var email: String? = null
    private var token: String? = null
    private var merchantId: String? = null
    private lateinit var idTrx: String

    private val viewModel: TransactionDetailViewModel by viewModels()
    private lateinit var dialogLoading: DialogLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogLoading = DialogLoading(this)
        val connectionDetector = ConnectionDetector(this)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSelesai.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val dataFromLastTrx = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                DataIntent.DATA_LAST_TRX_TO_DETAIL,
                DataLastTransactionItem::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(DataIntent.DATA_LAST_TRX_TO_DETAIL)
        }

        val dataFromByDateTrx = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DataIntent.DATE_TRX_TO_DETAIL, DataTrxItemByDate::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(DataIntent.DATE_TRX_TO_DETAIL)
        }

        if (dataFromLastTrx != null) {
            idTrx = dataFromLastTrx.idTrx.toString()
        }

        if (dataFromByDateTrx != null) {
            idTrx = dataFromByDateTrx.idTrx.toString()
        }

        if (connectionDetector.isConnectingToInternet()) {
            getDetailResponseById(idTrx)
        } else {
            Toast.makeText(this, "No Connection internet", Toast.LENGTH_SHORT).show()
        }


    }

    private fun getDetailResponseById(idTrx: String) {
        lifecycleScope.launch {
            viewModel.getTokenFromDataStore.collect { tokenDataStore ->
                token = tokenDataStore
                viewModel.getEmailFromDataStore.collect { emailDataStore ->
                    email = emailDataStore
                    viewModel.getMerchantIdFromDataStore.collect { mIdDataStore ->
                        merchantId = mIdDataStore
                        viewModel.getTransactionDetailById(token!!, merchantId!!, email!!, idTrx)
                            .distinctUntilChanged()
                            .collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        dialogLoading.startDialogLoading()
                                    }

                                    is Resource.Success -> {
                                        dialogLoading.dismissDialog()
                                        val item = result.data
                                        if (item != null) {
                                            with(binding) {
                                                merchantIdDetail.text = item.mID
                                                transactionDateDetail.text = item.dateTrx
                                                amountDetail.text = item.amount
                                                merchantNetAmount.text = item.netAmount
                                                trxDetailAmount.text = item.amount
                                                mdrAmount.text = item.mdrAmount
                                                transactionStatusDetail.text = item.status
                                                trxType.text = item.idTrx
                                                when (item.status) {
                                                    "pending" -> statusImage.setAnimation(R.raw.animation_pending)
                                                    else -> statusImage.setAnimation(R.raw.animation_success)
                                                }
                                            }
                                            Toast.makeText(
                                                this@TransactionDetailActivity,
                                                "Membuka detail transaksi ${item.message} response code ${item.statusResponse}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@TransactionDetailActivity,
                                                "Data transaksi tidak ditemukan",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    is Resource.Error -> {
                                        dialogLoading.dismissDialog()
                                        val errorMessage = when (result.statusCode) {
                                            ErrorCode.SERVER_ERR -> "Terjadi kesalahan! ${result.message} response code ${result.statusCode}"
                                            ErrorCode.ERR_EXCEPTION_CODE -> result.message
                                                ?: "Terjadi kesalahan pada server"

                                            else -> "Terjadi kesalahan! ${result.message} response code ${result.statusCode}"
                                        }
                                        Toast.makeText(
                                            this@TransactionDetailActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                    }
                }
            }
        }
    }
}