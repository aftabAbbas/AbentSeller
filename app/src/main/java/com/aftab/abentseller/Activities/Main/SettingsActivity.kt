package com.aftab.abentseller.Activities.Main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Activities.Settings.AboutActivity
import com.aftab.abentseller.Activities.Settings.ChangePasswordActivity
import com.aftab.abentseller.Activities.Settings.NotificationSettingsActivity
import com.aftab.abentseller.Activities.Settings.TermsAndConditionsActivity
import com.aftab.abentseller.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListeners()
    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        binding.llNotifications.setOnClickListener {

            startActivity(Intent(this, NotificationSettingsActivity::class.java))

        }

        binding.llChangePassword.setOnClickListener {

            startActivity(Intent(this, ChangePasswordActivity::class.java))

        }

        binding.llTermCondition.setOnClickListener {

            startActivity(Intent(this, TermsAndConditionsActivity::class.java))

        }

        binding.llAbout.setOnClickListener {

            startActivity(Intent(this, AboutActivity::class.java))

        }

        binding.llContact.setOnClickListener {

            startActivity(Intent(this, HelpActivity::class.java))

        }
    }
}