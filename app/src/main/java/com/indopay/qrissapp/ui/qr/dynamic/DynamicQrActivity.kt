package com.indopay.qrissapp.ui.qr.dynamic

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.indopay.qrissapp.R
import com.indopay.qrissapp.databinding.ActivityDynamicQrBinding
import java.text.NumberFormat
import java.util.Locale

class DynamicQrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDynamicQrBinding

    var nominal: EditText? = null
    var textViewAmount: TextView? = null
    private var submit: Button? = null
    private var totalTagihan: String? = null
    private var handler: Handler? = null
    var r: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nominal = binding.jumlahTagihan
        submit = binding.submitBtnDynamic
        textViewAmount = binding.amountText

        totalTagihan = nominal?.text.toString()

//        handler = Handler()
//
//        r = Runnable {
//            val i = Intent(this@DynamicQrActivity, SplashScreenActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(i)
//        }
//
//        startHandler()
        nominal?.addTextChangedListener(object : TextWatcher {
            private var setTextView = nominal?.text.toString().trim { it <= ' ' }

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != setTextView) {
                    textViewAmount?.removeTextChangedListener(this)
                    val replace = s.toString().replace("[Rp.]".toRegex(), "")
                    if (replace.isNotEmpty()) {
                        setTextView = formatRupiah(replace.toDouble())
                    }

                    textViewAmount?.text = setTextView
                    textViewAmount?.setTextColor(Color.BLACK)
                    textViewAmount?.addTextChangedListener(this)
                    nominal?.hint = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        submit?.setOnClickListener {
            if (nominal?.length() != 0) {
                val snack = Snackbar.make(
                    binding.root,
                    "Create dynamic Bar code",
                    Snackbar.LENGTH_SHORT
                )
                snack.show()

            } else {
                nominal?.error = "Masukan nominal!"
            }
        }

    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("IND", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val formatRupiah = numberFormat.format(number)
        val split = formatRupiah.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val length = split[0].length
        return split[0].substring(0, 2) + ". " + split[0].substring(2, length)
    }

}