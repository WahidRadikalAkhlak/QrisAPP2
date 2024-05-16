package com.indopay.qrissapp.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.indopay.qrissapp.R
import com.indopay.qrissapp.core.network.utils.ErrorCode
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.databinding.FragmentHomeBinding
import com.indopay.qrissapp.ui.login.LoginActivity
import com.indopay.qrissapp.ui.notification.NotificationActivity
import com.indopay.qrissapp.ui.profile.ViewProfileActivity
import com.indopay.qrissapp.ui.transaction.trx_detail.TransactionDetailActivity
import com.indopay.qrissapp.utils.ConnectionDetector
import com.indopay.qrissapp.utils.DataIntent
import com.indopay.qrissapp.utils.DialogLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var popView: ImageView? = null
    private var profileBtn: ImageView? = null
    private var notification: ImageView? = null
    private var todaysDate: TextView? = null
    private var merchantNameHome: TextView? = null
    private val homeViewModel: HomeViewModel by viewModels()

    private var mid: String? = null
    private var mEmail: String? = null
    private var mToken: String? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var amountTextView: TextView
    private lateinit var eyeIcon: ImageView

    private var locationUpdatesJob: Job? = null

    private lateinit var dialogLoading: DialogLoading
    private lateinit var adapter: HomeListTrxAdapter

    private var lineChart: LineChart? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lineChart = view.findViewById(R.id.chart)
        setupChart()

        amountTextView = binding.amount
        eyeIcon = binding.eyeicon

        var isAmountVisible = true

        eyeIcon.setOnClickListener {
            isAmountVisible = !isAmountVisible
            if (isAmountVisible) {
                amountTextView.visibility = View.VISIBLE
                eyeIcon.setImageResource(R.drawable.icon_eye)
            } else {
                amountTextView.visibility = View.INVISIBLE
                eyeIcon.setImageResource(R.drawable.eye_off)
            }
        }
        showLastFiveTransactionDates()
        val connectionDetector = ConnectionDetector(requireContext())

        dialogLoading = DialogLoading(activity)
        popView = binding.popUpView
        todaysDate = binding.todaysDate
        merchantNameHome = binding.merchantNameHome
        profileBtn = binding.profileBtn
        notification = binding.notification

        val date: String
        val cal = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault())
        date = dateFormat.format(cal)
        todaysDate?.text = date

        getHomeInformationProfile()

        adapter = HomeListTrxAdapter { data ->
            val intent = Intent(context, TransactionDetailActivity::class.java)
            intent.putExtra(DataIntent.DATA_LAST_TRX_TO_DETAIL, data)
            startActivity(intent)
        }

        with(binding) {
            lastTransactionRecyclerView.layoutManager = LinearLayoutManager(context)
            lastTransactionRecyclerView.adapter = adapter
        }

        popView?.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to log out")
            builder.setPositiveButton("Sure") { _, _ ->
                Intent(context, LoginActivity::class.java).also {
                    startActivity(it)
                    homeViewModel.logoutApplication()
                    showToast("Logout Success")
                    activity?.finish()
                }
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }

        binding.notification.setOnClickListener {
            Intent(context, NotificationActivity::class.java).also {
                startActivity(it)
                showToast("Notification page")
            }
        }

        profileBtn?.setOnClickListener {
            Intent(context, ViewProfileActivity::class.java).also {
                it.putExtra(DataIntent.EMAIL_MERCHANT, mEmail ?: "none")
                it.putExtra(DataIntent.TOKEN_MERCHANT, mToken ?: "none")
                it.putExtra(DataIntent.MID_MERCHANT, mid ?: "none")
                startActivity(it)
            }
        }
    }
    private fun getHomeInformationProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    homeViewModel.getTokenFromDataStore(),
                    homeViewModel.getEmailFromDataStore(),
                ) { token, email ->
                    mToken = token
                    mEmail = email

                    homeViewModel.getHomeInformationFromProfile(
                        mToken as String,
                        mEmail as String
                    )
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
                                        merchantId.text = item?.mID
                                        todaySales.text = item?.amount
                                        netsales.text = item?.amount
                                        merchantProvince.text = item?.province
                                        trxValue.text = item?.jmlTrx
                                        amount.text = item?.amount
                                        merchantNameHome.text = item?.merchantName
                                    }
                                    mid = item?.mID
                                    homeViewModel.saveMerchantIdToDataStore(item?.mID.toString())
                                    showLastTransaction(
                                        mToken as String,
                                        mEmail as String,
                                        mid.toString()
                                    )
                                }

                                is Resource.Error -> {
                                    dialogLoading.dismissDialog()
                                    when (result.statusCode) {
                                        ErrorCode.SERVER_ERR -> {
                                            Snackbar.make(
                                                binding.root,
                                                "A Server Error Occurred ${result.message} response code error ${result.statusCode}",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }

                                        ErrorCode.REQUEST_TIME_OUT -> {
                                            Snackbar.make(
                                                binding.root,
                                                "Request timeout ${result.message} response code error ${result.statusCode}",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }

                                        ErrorCode.ERR_INTERNET_CONNECTION -> {
                                            Toast.makeText(
                                                context,
                                                "No connection internet ${result.message} response code error ${result.statusCode}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        ErrorCode.ERR_EXCEPTION_CODE -> {
                                            Toast.makeText(
                                                context,
                                                "${result.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        else -> {
                                            Snackbar.make(
                                                binding.root,
                                                "There is an error ${result.message} response code ${result.statusCode}",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }

                }.collect()
            }
        }
    }

    private fun setupChart() {
        val transactionData = arrayOf(500f, 600f, 700f, 550f, 750f, 800f, 650f)

        lineChart?.apply {
            val entries = ArrayList<Entry>()
            for (i in transactionData.indices) {
                entries.add(Entry(i.toFloat(), transactionData[i]))
            }

            val lineDataSet = LineDataSet(entries, "Transaksi")
            lineDataSet.color = Color.BLUE
            lineDataSet.valueTextColor = Color.BLACK

            val lineData = LineData(lineDataSet)
            data = lineData

            // Styling the chart
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"))
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            description.isEnabled = false

            val legend: Legend = legend
            legend.form = Legend.LegendForm.LINE

            invalidate()
        }
    }


    private fun showLastFiveTransactionDates() {
        val dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
        val today = Calendar.getInstance()

        val currentDate = today.time

        val lastFiveTransactionDates = (0 until 5).map {
            today.add(Calendar.DAY_OF_MONTH, -1)
            dateFormat.format(today.time)
        }

        val datetransTextView = binding.datetrans

        datetransTextView.text = "${lastFiveTransactionDates.last()} - ${lastFiveTransactionDates.first()}"
    }

    private suspend fun showLastTransaction(
        token: String,
        email: String,
        mid: String,
    ) {
        homeViewModel.getListLastTrx(token, email, mid)
            .onStart { delay(1000L) }
            .distinctUntilChanged()
            .collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.lastTransactionRecyclerView.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.lastTransactionRecyclerView.visibility = View.VISIBLE
                        if (result.data.isNullOrEmpty()) {
                            binding.emptyReference.visibility = View.VISIBLE
                        }
                        if (result.data?.let { it.size >= 5 } == true) {
                            adapter.submitList(result.data.slice(0..4))
                        } else {
                            adapter.submitList(result.data)
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.lastTransactionRecyclerView.visibility = View.VISIBLE
                        Snackbar.make(
                            binding.root,
                            "Terjadi kesalahan! ${result.message} response code ${result.statusCode}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}