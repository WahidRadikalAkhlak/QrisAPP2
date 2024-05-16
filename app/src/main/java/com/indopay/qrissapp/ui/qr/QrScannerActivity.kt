package com.indopay.qrissapp.ui.qr

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.indopay.qrissapp.R
import com.indopay.qrissapp.databinding.ActivityQrScannerBinding
import com.indopay.qrissapp.ui.qr.dynamic.DynamicQrActivity
import com.indopay.qrissapp.ui.qr.dynamic.DynamicQrShowActivity
import com.indopay.qrissapp.ui.qr.statics.QrScannerShowActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class QrScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrScannerBinding

    var handler: Handler? = null
    var r: Runnable? = null

    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        codeScanner = CodeScanner(this, binding.codeScanner)


        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    codeScanner.startPreview()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        this@QrScannerActivity,
                        "You must accept this permission",
                        Toast.LENGTH_LONG).show()
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    codeScanner.startPreview()
                }
            }).check()

        binding.createQrDynamic.setOnClickListener {
            vibrator.vibrate(100)
            val i = Intent(this@QrScannerActivity, DynamicQrActivity::class.java)
            startActivity(i)
        }

        binding.showQrDynamic.setOnClickListener {
            vibrator.vibrate(100)
            val i = Intent(this@QrScannerActivity, DynamicQrShowActivity::class.java)
            startActivity(i)
        }

        binding.showQrStatic.setOnClickListener {
            vibrator.vibrate(100)
            Intent(this, QrScannerShowActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.createQrStatic.setOnClickListener {
            vibrator.vibrate(100)
            Toast.makeText(
                this,
                "Create Static QR...",
                Toast.LENGTH_SHORT
            ).show()
        }

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {

                val vibe = getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibe.vibrate(100)
                val i = Intent(this@QrScannerActivity, QrScannerShowActivity::class.java)
                i.putExtra("testString", it.text)
                startActivity(i)
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Camera initialization error ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}