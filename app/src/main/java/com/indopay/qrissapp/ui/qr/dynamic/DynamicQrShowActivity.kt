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

//        accessToken = intent.extras?.getString("accessToken")
//        mid = intent.extras?.getString("mid")
//        merchantName = intent.extras?.getString("merchantName")
        tagihan = 10000.0

        val totalAmount = tagihan
        val rupiahFormater = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val formatRp = DecimalFormatSymbols()
        formatRp.currencySymbol = "Rp. "
        formatRp.monetaryDecimalSeparator = ','
        formatRp.groupingSeparator = '.'

        rupiahFormater.decimalFormatSymbols = formatRp
        nominal.text = rupiahFormater.format(totalAmount).toString()

//        handler = Handler()
//        r = Runnable {
//            val i = Intent(this@DynamicQrShowActivity, SplashScreenActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(i)
//        }

//        startHandler()
    }

//    override fun onUserInteraction() {
//        super.onUserInteraction()
//        startHandler()
//        stopHandler()
//    }

//    private val merchantData: Unit
//        get() {
//            val queue = Volley.newRequestQueue(applicationContext)
//            val stringRequest: StringRequest =
//                object : StringRequest(Method.POST, Config.API_GENERATE_QR, Response.Listener { response ->
//                    try {
//                        val json = JSONObject(response)
//                        val isSuccess = json.getString("success")
//                        if (isSuccess == "true") {
//                            val qrString = json.getString("qr_string")
//                        } else {
//                            showToast("Volley Error!")
//                        }
//                    } catch (e: JSONException) {
//                        showToast("Catching Exception!")
//                        e.printStackTrace()
//                    }
//                }, Response.ErrorListener { }) {
//                    @Throws(AuthFailureError::class)
//                    public override fun getParams(): Map<String, String>? {
//                        val accessTokenParam = "$bearer $accessToken"
//                        val asd = ""
//                        val parameter: MutableMap<String, String> = HashMap()
//                        parameter["merchant_id"] = mid!!
//                        parameter["qr_type"] = "dynamic"
//                        parameter["nominal"] = tagihan!!
//                        return parameter
//                    }
//
//                    @Throws(AuthFailureError::class)
//                    override fun getHeaders(): Map<String, String> {
//                        val accessTokenParam = "$bearer $accessToken"
//                        val asd = ""
//                        val parameter: MutableMap<String, String> = HashMap()
////                parameter.put("Content-Length", "0");
////                parameter.put("Host", "<calculated when request is sent>");
////                parameter.put("User-Agent", "PostmanRuntime/7.31.0");
////                parameter.put("Accept", "*/*");
////                parameter.put("Accept-Encoding", "gzip, deflate, br");
////                parameter.put("Connection", "keep-alive");
////                parameter.put("content-type", "application/json");
//                        parameter["Authorization"] = accessTokenParam
//                        return parameter
//                    }
//                }
//            queue.add(stringRequest)
//        }

//    private fun stopHandler() {
//        handler?.removeCallbacks(r!!)
//    }
//
//    private fun startHandler() {
//        handler?.postDelayed(r!!, (5000 * 1000).toLong())
//    }
}