package com.indopay.qrissapp.ui.otp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chaos.view.PinView
import com.google.android.material.snackbar.Snackbar
import com.indopay.qrissapp.R
import com.indopay.qrissapp.core.network.utils.ErrorCode
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.databinding.ActivityOtpBinding
import com.indopay.qrissapp.ui.main.MainActivity
import com.indopay.qrissapp.utils.DataIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding

    private var otpInput: PinView? = null
    var email: String? = null
    var confirmBack: Boolean = false

    private val viewModel: OtpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        otpInput = binding.otpInput

        val email = intent.extras?.getString(DataIntent.DATA_SIGN_TO_OTP)

        otpInput?.requestFocus()

        otpInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(input: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(input: Editable) {
                if (input.toString().length == 6) {
                    getOtpResponse(email.toString(), input.toString())
                    val inputMethodsManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodsManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
            }
        })

        onBackPressedDispatcher.addCallback(this@OtpActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (confirmBack) {
                    finish()
                }

                confirmBack = true
                Toast.makeText(
                    this@OtpActivity,
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

    private fun getOtpResponse(email: String, otpNumber: String) {
        viewModel.requestVerifyLoginWithOtp(email, otpNumber).observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.loadingProgress.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.loadingProgress.visibility = View.GONE
                    viewModel.saveTokenToDataStore(result.data?.token as String)
                    viewModel.saveEmailToDataStore(result.data.email as String)
                    viewModel.saveLoginState()

                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@OtpActivity,
                                getString(R.string.get_data_loading),
                                Toast.LENGTH_SHORT
                            ).show()
                            delay(2.seconds)
                            Intent(this@OtpActivity, MainActivity::class.java).also {
                                it.putExtra(DataIntent.DATA_OTP_TO_MAIN, result.data)
                                startActivity(it)
                                Toast.makeText(
                                    this@OtpActivity,
                                    "Login berhasil! ${result.data.message}, response code: ${result.data.status}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    binding.loadingProgress.visibility = View.GONE
                    when (result.statusCode) {
                        ErrorCode.UNAUTHORIZATION_ERR -> {
                            otpInput?.text?.clear()
                            otpInput?.startAnimation(shakeError())
                            Snackbar.make(
                                binding.root,
                                result.message.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                        }

                        ErrorCode.NOT_FOUND_ERR -> {
                            otpInput?.text?.clear()
                            otpInput?.startAnimation(shakeError())
                            Snackbar.make(
                                binding.root, result.message.toString(), Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                        }

                        ErrorCode.SERVER_ERR -> {
                            otpInput?.text?.clear()
                            otpInput?.startAnimation(shakeError())
                            Snackbar.make(
                                binding.root, result.message.toString(), Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                        }

                        ErrorCode.ERR_INTERNET_CONNECTION -> {
                            otpInput?.text?.clear()
                            otpInput?.startAnimation(shakeError())
                            Snackbar.make(
                                binding.root, "No internet connection", Snackbar.LENGTH_LONG
                            ).setBackgroundTint(getColor(R.color.card_bg)).show()
                        }

                        ErrorCode.REQUEST_TIME_OUT -> {
                            otpInput?.text?.clear()
                            otpInput?.startAnimation(shakeError())
                            Snackbar.make(
                                binding.root, "Request timeout", Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                        }

                        ErrorCode.ERR_EXCEPTION_CODE -> {
                            Toast.makeText(
                                this,
                                "${result.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            otpInput?.text?.clear()
                            otpInput?.startAnimation(shakeError())
                            Snackbar.make(
                                binding.root,
                                "Terjadi kesalahan atau kode OTP salah! ${result.message}, response code: ${result.statusCode}",
                                Snackbar.LENGTH_SHORT
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                        }
                    }

                }
            }
        }
    }

    private fun shakeError(): TranslateAnimation {
        val shake = TranslateAnimation(0f, 7f, 0f, 0f)
        shake.duration = 500
        shake.interpolator = CycleInterpolator(7f)
        return shake
    }

}