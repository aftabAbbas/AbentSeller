package com.aftab.abentseller.Utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.aftab.abentseller.R

class LoadingDialog(context: Context, text: String?) :
    Dialog(context) {
    init {
        setContentView(R.layout.loading_dialog)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)
        val tv = findViewById<TextView>(R.id.tv_title)
        tv.text = text
    }
}