package com.indopay.qrissapp.ui.qr.dynamic

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.indopay.qrissapp.databinding.ActivityDynamicQrBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DynamicQrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDynamicQrBinding

    private var nominal: EditText? = null
    private var textViewAmount: TextView? = null
    private var submit: Button? = null
    private val viewModel: DynamicQrViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nominal = binding.jumlahTagihan
        submit = binding.submitBtnDynamic
        textViewAmount = binding.amountText

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
                val inputNominal = nominal?.text.toString()

                val qrBitmap = generateQRCode(inputNominal)

                Intent(this, DynamicQrShowActivity::class.java).also {
                    it.putExtra("nominal", inputNominal)
                    it.putExtra("qr_bitmap", qrBitmap)
                    viewModel.savecodestring(inputNominal)
                    startActivity(it)
                }
            } else {
                nominal?.error = "Masukkan nominal!"
            }
        }
    }
    private fun generateQRCode(nominal: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix = writer.encode(nominal, BarcodeFormat.QR_CODE, 512, 512)
        val width: Int = bitMatrix.width
        val height: Int = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
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