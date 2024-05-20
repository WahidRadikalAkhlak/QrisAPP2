package com.indopay.qrissapp.ui.qr.dynamic

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.indopay.qrissapp.databinding.ActivityDynamicQrShowBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DynamicQrShowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDynamicQrShowBinding

    private lateinit var nominal: TextView
    private var handler: Handler? = null
    var r: Runnable? = null
    var tagihan: Double? = null
    var mid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicQrShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nominal = binding.nominalDynamicQr

        tagihan = 10000.0

        val totalAmount = tagihan
        val rupiahFormater = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val formatRp = DecimalFormatSymbols()
        formatRp.currencySymbol = "Rp. "
        formatRp.monetaryDecimalSeparator = ','
        formatRp.groupingSeparator = '.'

        rupiahFormater.decimalFormatSymbols = formatRp
        nominal.text = rupiahFormater.format(totalAmount).toString()
    }
}