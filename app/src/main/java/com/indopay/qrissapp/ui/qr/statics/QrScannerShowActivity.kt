package com.indopay.qrissapp.ui.qr.statics

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.indopay.qrissapp.R
import com.indopay.qrissapp.databinding.ActivityDynamicQrShowBinding
import com.indopay.qrissapp.databinding.ActivityQrScannerBinding
import com.indopay.qrissapp.databinding.ActivityQrScannerShowBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class QrScannerShowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrScannerShowBinding
    private lateinit var terminal: TextView
    var menagih: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScannerShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        terminal = binding.terminalIdStaticQr

        menagih = 10000.0

        val totalAmount = menagih
        val rupiahFormater = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val formatRp = DecimalFormatSymbols()
        formatRp.currencySymbol = "Rp. "
        formatRp.monetaryDecimalSeparator = ','
        formatRp.groupingSeparator = '.'

        rupiahFormater.decimalFormatSymbols = formatRp
        terminal.text = rupiahFormater.format(totalAmount).toString()
    }
}