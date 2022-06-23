package com.aftab.abentseller.Activities.Settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aftab.abentseller.R
import com.aftab.abentseller.databinding.ActivityTermsAndConditionsBinding

class TermsAndConditionsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityTermsAndConditionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTermsAndConditionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListeners()
    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

    }
}