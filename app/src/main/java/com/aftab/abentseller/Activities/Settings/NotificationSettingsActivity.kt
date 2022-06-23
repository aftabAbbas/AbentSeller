package com.aftab.abentseller.Activities.Settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivityNotificationSettingsBinding

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationSettingsBinding
    private lateinit var sh: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        sh = SharedPref(this)

        binding.swOrderNotification.isChecked = sh.getBoolean(Constants.ORDER_NOTI)
        binding.swReviewNotification.isChecked = sh.getBoolean(Constants.REVIEW_NOTI)
        binding.swDeliveryNotification.isChecked = sh.getBoolean(Constants.DELIVERY_NOTI)
        binding.swChatNotification.isChecked = sh.getBoolean(Constants.CHAT_NOTI)

    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.llOrderNotifications.setOnClickListener {
            if (binding.swOrderNotification.isChecked) {
                binding.swOrderNotification.isChecked = false
                sh.putBoolean(Constants.ORDER_NOTI, false)
            } else {
                binding.swOrderNotification.isChecked = true
                sh.putBoolean(Constants.ORDER_NOTI, true)
            }
        }

        binding.llReviewNotification.setOnClickListener {
            if (binding.swReviewNotification.isChecked) {
                binding.swReviewNotification.isChecked = false
                sh.putBoolean(Constants.REVIEW_NOTI, false)
            } else {
                binding.swReviewNotification.isChecked = true
                sh.putBoolean(Constants.REVIEW_NOTI, true)
            }
        }

        binding.llDeliveryNotification.setOnClickListener {
            if (binding.swDeliveryNotification.isChecked) {
                binding.swDeliveryNotification.isChecked = false
                sh.putBoolean(Constants.DELIVERY_NOTI, false)
            } else {
                binding.swDeliveryNotification.isChecked = true
                sh.putBoolean(Constants.DELIVERY_NOTI, true)
            }
        }

        binding.llChatNotification.setOnClickListener {
            if (binding.swChatNotification.isChecked) {
                binding.swChatNotification.isChecked = false
                sh.putBoolean(Constants.CHAT_NOTI, false)
            } else {
                binding.swChatNotification.isChecked = true
                sh.putBoolean(Constants.CHAT_NOTI, true)
            }
        }

    }
}