package com.aftab.abentseller.Activities.Main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Adapters.Recycler.NotificationsAdapter
import com.aftab.abentseller.Model.Notifications
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users

    private var notificationsList = ArrayList<Notifications>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
        getNotifications()
    }

    private fun initUI() {

        sh = SharedPref(this)
        usersData = sh.getUsers()!!


    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.srlNotifications.setOnRefreshListener(this::getNotifications)

    }

    private fun getNotifications() {

        binding.srlNotifications.isRefreshing = true

        FireRef.NOTIFICATIONS_REF
            .whereEqualTo(Constants.RECEIVER_ID, usersData.uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    notificationsList.clear()

                    for (document in it.result) {

                        val notifications: Notifications =
                            document.toObject(Notifications::class.java)

                        notificationsList.add(notifications)

                    }


                }

                setAdapter()
                binding.srlNotifications.isRefreshing = false


            }

    }


    private fun setAdapter() {

        if (notificationsList.size > 0) {

            binding.llNoData.visibility = View.GONE
            markAsRead()

        } else {

            binding.llNoData.visibility = View.VISIBLE

        }

        notificationsAdapter = NotificationsAdapter(this, notificationsList)
        binding.rvNotification.adapter = notificationsAdapter

    }

    private fun markAsRead() {

        for (notification in notificationsList) {

            if (!notification.read) {

                val userInfo = HashMap<String, Any>()
                userInfo[Constants.READ] = true

                FireRef.NOTIFICATIONS_REF
                    .document(notification.id)
                    .update(userInfo)

            }

        }

    }
}