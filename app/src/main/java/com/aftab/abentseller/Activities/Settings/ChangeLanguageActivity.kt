package com.aftab.abentseller.Activities.Settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aftab.abentseller.R
import com.aftab.abentseller.databinding.ActivityChangeLanguageBinding

class ChangeLanguageActivity : AppCompatActivity() {

    private lateinit var binding:ActivityChangeLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChangeLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListeners()
    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

    }
}