package com.indopay.qrissapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.indopay.qrissapp.R
import com.indopay.qrissapp.core.network.utils.ErrorCode
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.databinding.ActivityLoginBinding
import com.indopay.qrissapp.ui.otp.OtpActivity
import com.indopay.qrissapp.utils.DataIntent
import com.indopay.qrissapp.utils.DialogLoading
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.system.exitProcess

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var inputEmail: EditText
    private lateinit var loginBtn: Button
    private var passwordVisible = false
    var email: String? = null
    var password: String? = null

    private lateinit var dialogLoading: DialogLoading

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dialogLoading = DialogLoading(this)



        inputEmail = binding.inputEmail
        loginBtn = binding.loginBtn

        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                validateEmail(input.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        loginBtn.setOnClickListener {
            if (validateInput()) {
                getLoginResponse(inputEmail.text.toString())
            }
        }
    }

    private fun getLoginResponse(email: String) {
        viewModel.loginRequest(email).observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    dialogLoading.startDialogLoading()
                }

                is Resource.Success -> {
                    dialogLoading.dismissDialog()
                    if (result.data?.status == "00") {
                        val i = Intent(this@LoginActivity, OtpActivity::class.java)
                        i.putExtra(DataIntent.DATA_SIGN_TO_OTP, email)
                        startActivity(i)
                        Toast.makeText(
                            this@LoginActivity,
                            "${result.data.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }

                    Snackbar.make(
                        binding.root,
                        "Terjadi Kesalahan! ${result.data?.message} response code: ${result.data?.status}",
                        Snackbar.LENGTH_LONG
                    )
                        .setTextColor(getColor(R.color.white))
                        .setBackgroundTint(getColor(R.color.card_bg)).show()

                }

                is Resource.Error -> {
                    dialogLoading.dismissDialog()
                    when (result.statusCode) {
                        ErrorCode.UNAUTHORIZATION_ERR -> {
                            Snackbar.make(
                                binding.root,
                                "Unauthorization, Please login again!",
                                Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                            inputEmail.startAnimation(shakeError())
                        }

                        ErrorCode.NOT_FOUND_ERR -> {
                            Snackbar.make(
                                binding.root,
                                "Not Found Account",
                                Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                            inputEmail.startAnimation(shakeError())
                        }

                        ErrorCode.SERVER_ERR -> {
                            Snackbar.make(
                                binding.root,
                                "Something wrong with server connection: ${result.message}",
                                Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                            inputEmail.startAnimation(shakeError())
                        }

                        ErrorCode.ERR_INTERNET_CONNECTION -> {
                            Snackbar.make(
                                binding.root,
                                "No Connection Internet!",
                                Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                            inputEmail.startAnimation(shakeError())
                        }

                        ErrorCode.REQUEST_TIME_OUT -> {
                            Snackbar.make(
                                binding.root,
                                "Request Timeout!",
                                Snackbar.LENGTH_LONG
                            )
                                .setTextColor(getColor(R.color.white))
                                .setBackgroundTint(getColor(R.color.card_bg)).show()
                            inputEmail.startAnimation(shakeError())
                        }

                        ErrorCode.ERR_EXCEPTION_CODE -> {
                            Toast.makeText(
                                this,
                                "Err exception code: ${result.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this,
                                "Terjadi Kesalahan! Error message: ${result.message} response code ${result.statusCode}",
                                Toast.LENGTH_LONG
                            ).show()
                            inputEmail.startAnimation(shakeError())
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

    private fun validateEmail(input: String) {
        if (input.trim().isEmpty()) {
            inputEmail.error = "Email tidak boleh kosong"
        } else {
            inputEmail.error = null
        }
    }


    private fun validateInput(): Boolean {
        return when {
            inputEmail.text?.trim()?.isEmpty() == true -> {
                inputEmail.error = "Email tidak boleh kosong"
                false
            }

            (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString()).matches()) -> {
                inputEmail.error = "Format email tidak sesuai"
                inputEmail.requestFocus()
                false
            }

            else -> {
                inputEmail.error = null
                true
            }
        }

    }

    companion object {
        private val isRooted: Boolean
            get() = findBinary("su")

        private fun findBinary(binaryName: String): Boolean {
            var found = false
            if (!found) {
                val places = arrayOf(
                    "/sbin/", "/system/bin/",
                    "/system/xbin/", "/data/local/xbin/",
                    "/data/local/bin/", "/system/sd/xbin/",
                    "/system/bin/failsafe/", "/data/local/"
                )
                for (where in places) {
                    if (File(where + binaryName).exists()) {
                        found = true
                        break
                    }
                }
            }
            return found
        }
    }

    private fun checkRoot() {
        val rootBeer = RootBeer(applicationContext)
        if (rootBeer.isRooted) {
            exitProcess(0)
        }
    }
}