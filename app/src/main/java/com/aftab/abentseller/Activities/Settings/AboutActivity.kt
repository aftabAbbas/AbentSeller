package com.aftab.abentseller.Activities.Settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.BuildConfig
import com.aftab.abentseller.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private lateinit var versionName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        versionName = BuildConfig.VERSION_NAME
        binding.tvVersion.text = versionName

    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

    }
}