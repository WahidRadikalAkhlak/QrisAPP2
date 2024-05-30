package com.indopay.qrissapp.ui.transaction.trx_detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.indopay.qrissapp.R
import com.indopay.qrissapp.core.network.utils.ErrorCode
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.databinding.ActivityTransactionDetailBinding
import com.indopay.qrissapp.domain.model.DataLastTransactionItem
import com.indopay.qrissapp.domain.model.DataTrxItemByDate
import com.indopay.qrissapp.domain.model.TransactionDetail
import com.indopay.qrissapp.utils.DataIntent
import com.indopay.qrissapp.utils.DialogLoading
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionDetailBinding
    private lateinit var dialogLoading: DialogLoading
    private val viewModel: TransactionDetailViewModel by viewModels()

    private var email: String? = null
    private var token: String? = null
    private var merchantId: String? = null
    private lateinit var idTrx: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogLoading = DialogLoading(this)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnSelesai.setOnClickListener {
            onBackPressed()
        }

        val dataFromLastTrx = intent.getParcelableExtra<DataLastTransactionItem>(
            DataIntent.DATA_LAST_TRX_TO_DETAIL
        )
        val dataFromByDateTrx = intent.getParcelableExtra<DataTrxItemByDate>(
            DataIntent.DATE_TRX_TO_DETAIL
        )

        idTrx = dataFromLastTrx?.idTrx ?: dataFromByDateTrx?.idTrx ?: ""
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.getTokenFromDataStore.collect { tokenDataStore ->
                token = tokenDataStore
                viewModel.getEmailFromDataStore.collect { emailDataStore ->
                    email = emailDataStore
                    viewModel.getMerchantIdFromDataStore.collect { mIdDataStore ->
                        merchantId = mIdDataStore
                        getDetailResponseById(idTrx)
                    }
                }
            }
        }
    }

    private fun getDetailResponseById(idTrx: String) {
        lifecycleScope.launchWhenStarted {
            viewModel.getTransactionDetailById(token!!, merchantId!!, email!!, idTrx)
                .observe(this@TransactionDetailActivity) { result ->
                    when (result) {
                        is Resource.Loading -> dialogLoading.startDialogLoading()
                        is Resource.Success -> {
                            dialogLoading.dismissDialog()
                            result.data?.let { item ->
                                updateUIWithData(item)
                                showToast("Transaction detail opened: ${item.idTrx}, Response code: ${item.statusResponse}")
                            } ?: showToast("Transaction data not found")
                        }
                        is Resource.Error -> {
                            dialogLoading.dismissDialog()
                            val errorMessage = when (result.statusCode) {
                                ErrorCode.SERVER_ERR -> "An error occurred! ${result.message}, Response code: ${result.statusCode}"
                                ErrorCode.ERR_EXCEPTION_CODE -> result.message ?: "An error occurred on the server"
                                else -> "An error occurred! ${result.message}, Response code: ${result.statusCode}"
                            }
                            showToast(errorMessage)
                        }
                    }
                }
        }
    }

    private fun updateUIWithData(item: TransactionDetail) {
        with(binding) {
            trxDetailAmount.text = item.amount
            transactionStatusDetail.text = item.status
            trxType.text = item.idTrx
            transactionDateDetail.text = item.dateTrx
            merchantIdDetail.text = item.mID
            amountDetail.text = item.amount
            mdrAmount.text = item.mdrAmount
            merchantNetAmount.text = item.netAmount
            statusImage.setAnimation(
                if (item.status == "pending") R.raw.animation_pending else R.raw.animation_success
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@TransactionDetailActivity, message, Toast.LENGTH_SHORT).show()
    }
}