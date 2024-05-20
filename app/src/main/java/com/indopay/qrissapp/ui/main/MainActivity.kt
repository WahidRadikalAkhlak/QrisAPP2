package com.indopay.qrissapp.ui.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.indopay.qrissapp.R
import com.indopay.qrissapp.databinding.ActivityMainBinding
import com.indopay.qrissapp.domain.model.LoginVerify
import com.indopay.qrissapp.ui.home.HomeFragment
import com.indopay.qrissapp.ui.qr.QrScannerActivity
import com.indopay.qrissapp.ui.transaction.TransactionFragment
import com.indopay.qrissapp.utils.DataIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var handler: Handler? = null
    var merchantName: String? = null
    var merchantEmail: String? = null
    var mid: String? = null

    var confirmBack: Boolean = false

    private lateinit var homeFragment: HomeFragment
    private lateinit var transactionFragment: TransactionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeFragment = HomeFragment()
        transactionFragment = TransactionFragment()

        binding.qrNavBar.setOnClickListener {
            val intent = Intent(this@MainActivity, QrScannerActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavBar.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(homeFragment)
                }

                R.id.transactionList -> {
                    replaceFragment(transactionFragment)
                }

                R.id.showQr -> {}
            }
            true
        }

        binding.bottomNavBar.selectedItemId = R.id.home

        exitApplication()
    }

    private fun stopHandler() {
    }

    private fun startHandler() {

    }

    private fun exitApplication() {
        onBackPressedDispatcher.addCallback(this@MainActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (confirmBack) {
                    finish()
                }

                confirmBack = true
                Toast.makeText(
                    this@MainActivity,
                    "Tekan sekali lagi apabila anda ingin keluar dari aplikasi",
                    Toast.LENGTH_SHORT
                ).show()

                lifecycleScope.launch {
                    delay(2.seconds)
                    confirmBack = false
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val data  = intent.getParcelableExtra<LoginVerify>(DataIntent.DATA_OTP_TO_MAIN)
        val bundle = Bundle()
        bundle.putString(EMAIL, data?.email)
        bundle.putString(TOKEN, data?.token)
        fragment.arguments = bundle

        fragmentTransaction.replace(R.id.frameNavigation, fragment)
        fragmentTransaction.commit()
    }

    companion object {
        const val EMAIL = "email"
        const val TOKEN = "TOKEN"
    }
}