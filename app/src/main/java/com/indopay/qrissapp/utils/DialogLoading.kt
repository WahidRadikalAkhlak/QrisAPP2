package com.indopay.qrissapp.utils

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import androidx.fragment.app.FragmentActivity
import com.indopay.qrissapp.databinding.ItemDialogLoadingBinding

class DialogLoading(private val activity: FragmentActivity?) {

    private var alertDialog: AlertDialog? = null

    fun startDialogLoading() {
        val builder = AlertDialog.Builder(activity)

        val binding = activity?.let { ItemDialogLoadingBinding.inflate(it.layoutInflater) }
        builder.setView(binding?.root)
        builder.setCancelable(false)

        alertDialog = builder.create()
        val inset = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 250)
        alertDialog?.window?.setBackgroundDrawable(inset)
        alertDialog?.show()
    }

    fun dismissDialog() {
        alertDialog?.dismiss()
    }
}