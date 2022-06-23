package com.aftab.abentseller.Activities.Main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.databinding.ActivitySeeImageBinding
import com.bumptech.glide.Glide

class SeeImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeeImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        Functions.hideSystemUI(this)
        getIntentValues()
    }

    private fun getIntentValues() {
        val img = intent.getStringExtra(Constants.SEND_IMAGE)
        Glide.with(this).load(img).placeholder(R.drawable.place_holder_img).into(binding.ivZoom)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        Functions.hideKeyboard(this)
    }
}