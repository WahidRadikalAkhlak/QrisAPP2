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
    private var mID: String? = null
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
            intent.getParcelableExtra(DataIntent.DATA_LAST_TRX_TO_DETAIL, DataLastTransactionItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(DataIntent.DATA_LAST_TRX_TO_DETAIL)
        }

        val dataFromByDateTrx =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

    private fun getDataDummyFromHome(data: DataLastTransactionItem) {
        with(binding) {
            merchantIdDetail.text = "1234567890"
            transactionDateDetail.text = data.date
            amountDetail.text = data.amount
            merchantNetAmount.text = data.netAmount
            trxDetailAmount.text = data.amount
            statusImage.setAnimation(R.raw.animation_success)
            transactionStatusDetail.text = data.status
        }
    }

    private fun getDetailResponseById(idTrx: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val getResult = listOf(
                    async {
                        viewModel.getTokenFromDataStore.collect { tokenDataStore ->
                            token = tokenDataStore
                        }
                    },
                    async {
                        viewModel.getEmailFromDataStore.collect { emailDataStore ->
                            email = emailDataStore
                        }
                    },
                    async {
                        viewModel.getMerchantIdFromDataStore.collect { mIdDataStore ->
                            mID = mIdDataStore
                        }
                    },
                    async {
                        viewModel.getTransactionDetailById(
                            token as String,
                            mID as String,
                            email as String,
                            idTrx
                        ).distinctUntilChanged()
                            .collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        dialogLoading.startDialogLoading()
                                    }

                                    is Resource.Success -> {
                                        dialogLoading.dismissDialog()
                                        val item = result.data

                                        with(binding) {
                                            merchantIdDetail.text = mID
                                            transactionDateDetail.text = item?.dateTransaction
                                            amountDetail.text = item?.amount
                                            merchantNetAmount.text = item?.netAmount
                                            trxDetailAmount.text = item?.amount
                                            statusImage.setAnimation(R.raw.animation_success)
                                            transactionStatusDetail.text = item?.status
                                            trxDetailAmount.text = item?.amount
                                            trxType.text = item?.idTrx
                                            amountDetail.text = item?.netAmount
                                        }

                                        if (item?.status == "pending") {
                                            binding.statusImage.setAnimation(R.raw.animation_pending)
                                        }

                                        Toast.makeText(
                                            this@TransactionDetailActivity,
                                            "Membuka detail transaksi ${item?.message} response code ${item?.statusResponse}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }

                                    is Resource.Error -> {
                                        dialogLoading.dismissDialog()
                                        when (result.statusCode) {
                                            ErrorCode.SERVER_ERR -> {
                                                Toast.makeText(
                                                    this@TransactionDetailActivity,
                                                    "Terjadi kesalahan! ${result.message} response code ${result.statusCode}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            ErrorCode.ERR_EXCEPTION_CODE -> {
                                                Toast.makeText(
                                                    this@TransactionDetailActivity,
                                                    "${result.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            else -> {
                                                Toast.makeText(
                                                    this@TransactionDetailActivity,
                                                    "Terjadi kesalahan! ${result.message} response code ${result.statusCode}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                    }
                                }
                            }

                    },
                )

                getResult.awaitAll()
            }
        }
    }
}