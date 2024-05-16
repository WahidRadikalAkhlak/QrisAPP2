package com.indopay.qrissapp.ui.transaction

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.indopay.qrissapp.R
import com.indopay.qrissapp.core.data.paging.adapter.ItemLoadingStateAdapter
import com.indopay.qrissapp.databinding.FragmentTransactionBinding
import com.indopay.qrissapp.ui.transaction.trx_detail.TransactionDetailActivity
import com.indopay.qrissapp.utils.DataIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TransactionFragment : Fragment(){

    private var dateFrom: Button? = null
    private var dateTo: Button? = null
    private var startFilter: ImageView? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var insertedDateFrom: String? = null
    private var insertedDateTo: String? = null
    private lateinit var emailMerchant: String
    private lateinit var authToken: String
    private lateinit var mID: String

    private lateinit var lastTrxAdapter: LastTrxAdapterWithPaging
    private lateinit var trxListByDateAdapter: TrxListByDateAdapter
    private val lasTrxViewModel: LastTrxViewModel by viewModels()
    private val trxByDateViewModel by viewModels<TrxListByDateViewModel>()

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding

    private var stateLivedata: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateFrom = binding?.dateFrom
        dateTo = binding?.dateTo
        startFilter = binding?.startFilter



        lastTrxAdapter = LastTrxAdapterWithPaging { data ->
            Intent(context, TransactionDetailActivity::class.java).also {
                it.putExtra(DataIntent.DATA_LAST_TRX_TO_DETAIL, data)
                startActivity(it)
            }
        }
        showRecyclerViewLastTrx()

        trxListByDateAdapter = TrxListByDateAdapter { data ->
            Intent(context, TransactionDetailActivity::class.java).also {
                it.putExtra(DataIntent.DATE_TRX_TO_DETAIL, data)
                startActivity(it)
            }
        }

        dateFrom?.setOnClickListener {
            initDatePickerFrom()
        }

        dateTo?.setOnClickListener {
            initDatePickerTo()
        }

        lastTrxAdapter.addLoadStateListener {
            binding?.swipeRefreshTrx?.isRefreshing = it.refresh is LoadState.Loading == true

            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000L)
                binding?.emptyState?.isVisible = it.refresh is LoadState.Loading && lastTrxAdapter.itemCount == 0
                if (lastTrxAdapter.itemCount == 0) {
                    showToast("Data is empty")
                }
            }
        }

        trxListByDateAdapter.addLoadStateListener {
            binding?.swipeRefreshTrx?.isRefreshing = it.refresh is LoadState.Loading == true

            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000L)
                if (it.refresh is LoadState.Loading && trxListByDateAdapter.itemCount == 0) {
                    showToast("Data is empty")
                    binding?.emptyState?.visibility = View.VISIBLE
                }

                if (it.refresh is LoadState.NotLoading && trxListByDateAdapter.itemCount > 0) {
                    binding?.emptyState?.visibility = View.GONE
                }
            }
        }

        binding?.swipeRefreshTrx?.setOnRefreshListener {
            lastTrxAdapter.refresh()
        }


        startFilter?.setOnClickListener {
            if (dateFrom?.text.isNullOrEmpty() || dateTo?.text.isNullOrEmpty()) {
                Toast.makeText(context, "Silahkan isi tanggal terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                showRecyclerViewTrxByDate()
                getResponseTrxByDate(
                    authToken,
                    emailMerchant,
                    mID,
                    dateFrom?.text.toString(),
                    dateTo?.text.toString()
                )
                binding?.swipeRefreshTrx?.setOnRefreshListener {
                    trxListByDateAdapter.refresh()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        fetchLastDataTrx()
        Toast.makeText(context, "onStart action!", Toast.LENGTH_SHORT).show()
    }

    private fun fetchLastDataTrx() {
        viewLifecycleOwner.lifecycleScope.launch {
            val getResult = listOf(
                async {
                    lasTrxViewModel.readTokenFromDataStore.observe(viewLifecycleOwner) { token ->
                        authToken = token
                    }
                },
                async {
                    lasTrxViewModel.readEmailFromDataStore.observe(viewLifecycleOwner) { email ->
                        emailMerchant = email
                    }
                },
                async {
                    lasTrxViewModel.readMerchantIdFromDataStore.observe(viewLifecycleOwner) { merchantId ->
                        mID = merchantId
                        getResponseLastTrx(authToken, emailMerchant, mID)
                    }
                },
            )

            getResult.awaitAll()
        }

    }

    override fun onStop() {
        super.onStop()
        /** dont need now
         * viewLifecycleOwnerLiveData.removeObservers(viewLifecycleOwner)
         **/
        Toast.makeText(context, "onStop Start!", Toast.LENGTH_SHORT).show()
    }

    private fun getResponseLastTrx(
        authToken: String,
        email: String,
        mID: String
    ) {
        lasTrxViewModel.getLastTrxWithPagingThree(authToken, email, mID).observe(viewLifecycleOwner) { pagingData ->
            lastTrxAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }
    }

    private fun getResponseTrxByDate(
        authToken: String,
        email: String,
        mID: String,
        firstDate: String,
        lastDate: String
    ) {
        trxByDateViewModel.getTrxListByDate(authToken, email, mID, firstDate, lastDate)
            .observe(viewLifecycleOwner) { pagingData ->
                trxListByDateAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }
    }

    private fun showRecyclerViewLastTrx() {
        binding?.trxListRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lastTrxAdapter.withLoadStateFooter(
                footer = ItemLoadingStateAdapter { lastTrxAdapter.retry() },
            )
        }
    }

    private fun showRecyclerViewTrxByDate() {
        binding?.trxListRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trxListByDateAdapter.withLoadStateFooter(
                footer = ItemLoadingStateAdapter { trxListByDateAdapter.retry() },
            )
        }
    }

    private fun initDatePickerFrom() {
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]
        // date picker dialog
        datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                val date = makeDateString(year, monthOfYear + 1, dayOfMonth)
                dateFrom?.text = date
            }, year, month, day
        )
        datePickerDialog?.show()
    }

    private fun makeDateString(year: Int, month: Int, day: Int): String {
        return year.toString() + "-" + getMonthFormat(month) + "-" + getDateFormat(day)
    }

    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "01"
        if (month == 2) return "02"
        if (month == 3) return "03"
        if (month == 4) return "04"
        if (month == 5) return "05"
        if (month == 6) return "06"
        if (month == 7) return "07"
        if (month == 8) return "08"
        if (month == 9) return "09"
        if (month == 10) return "10"
        if (month == 11) return "11"
        return if (month == 12) "12" else "01"
    }

    private fun getDateFormat(day: Int): String {
        if (day == 1) return "01"
        if (day == 2) return "02"
        if (day == 3) return "03"
        if (day == 4) return "04"
        if (day == 5) return "05"
        if (day == 6) return "06"
        if (day == 7) return "07"
        if (day == 8) return "08"
        if (day == 9) return "09"
        if (day == 10) return "10"
        if (day == 11) return "11"
        if (day == 12) return "12"
        if (day == 13) return "13"
        if (day == 14) return "14"
        if (day == 15) return "15"
        if (day == 16) return "16"
        if (day == 17) return "17"
        if (day == 18) return "18"
        if (day == 19) return "19"
        if (day == 20) return "20"
        if (day == 21) return "21"
        if (day == 22) return "22"
        if (day == 23) return "23"
        if (day == 24) return "24"
        if (day == 25) return "25"
        if (day == 26) return "26"
        if (day == 27) return "27"
        if (day == 28) return "28"
        if (day == 29) return "29"
        if (day == 30) return "30"
        return if (day == 31) "31" else "01"
    }

    private fun initDatePickerTo() {
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]

        // date picker dialog
        datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                val date = makeDateString(year, monthOfYear + 1, dayOfMonth)
                dateTo?.text = date
            }, year, month, day
        )
        datePickerDialog?.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}