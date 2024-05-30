package com.indopay.qrissapp.ui.qr.dynamic

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.indopay.qrissapp.databinding.ActivityDynamicQrShowBinding
import com.indopay.qrissapp.ui.qr.QrScannerActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@AndroidEntryPoint
class DynamicQrShowActivity : AppCompatActivity() {

    private val viewModel: DynamicQrShowViewModel by viewModels()
    private lateinit var binding: ActivityDynamicQrShowBinding
    private lateinit var qrCodeImageView: ImageView
    private lateinit var nominalTextView: TextView
    private lateinit var doneButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicQrShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        qrCodeImageView = binding.staticQr
        nominalTextView = binding.nominalDynamicQr
        doneButton = binding.btnSelesai

        val nominal = intent.getStringExtra("nominal")

        val formattedNominal = nominal?.let { formatRupiah(it.toDouble()) }

        nominalTextView.text = formattedNominal

        val qrBitmap = intent.getParcelableExtra<Bitmap>("qr_bitmap")

        qrCodeImageView.setImageBitmap(qrBitmap)

        viewModel.getcodestring().observe(this) { codeString ->
            val qrBitmapsec = generateQRCode(codeString)
            qrCodeImageView.setImageBitmap(qrBitmapsec)
        }

            doneButton.setOnClickListener {
                val intent = Intent(this, QrScannerActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
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
        val localeID = Locale("in", "ID")
        val formatRupiah = DecimalFormat.getCurrencyInstance(localeID) as DecimalFormat
        val formatRp = DecimalFormatSymbols()

        formatRp.currencySymbol = "Rp."
        formatRp.monetaryDecimalSeparator = ','
        formatRp.groupingSeparator = '.'

        formatRupiah.decimalFormatSymbols = formatRp

        return formatRupiah.format(number)
    }
}