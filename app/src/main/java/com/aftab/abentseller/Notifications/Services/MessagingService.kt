package com.aftab.abentseller.Notifications.Services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aftab.abentseller.Activities.Main.HomeActivity
import com.aftab.abentseller.Notifications.Helper.NotificationHelper
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.SharedPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

@Suppress("deprecation")
class MessagingService : FirebaseMessagingService() {
    var sp: SharedPref? = null
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        sp = SharedPref(this)
        val type = remoteMessage.data[Constants.REMOTE_MSG_TYPE]
        if (type != null) {
            val title = remoteMessage.data[Constants.KEY_TITLE]
            val body = remoteMessage.data[Constants.KEY_BODY]
            val hisId = remoteMessage.data[Constants.SEND_ID]
            when (type) {
                Constants.REMOTE_MSG_CHAT -> if (sp!!.getBoolean(Constants.CHAT_NOTI)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        showNotificationAPI26ForChat(title, body, HomeActivity::class.java, hisId)
                    } else {
                        showNotificationForChat(title, body, HomeActivity::class.java, hisId)
                    }
                }

                Constants.REMOTE_MSG_ORDER -> if (sp!!.getBoolean(Constants.ORDER_NOTI)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        showNotificationAPI26ForChat(title, body, HomeActivity::class.java, hisId)
                    } else {
                        showNotificationForChat(title, body, HomeActivity::class.java, hisId)
                    }
                }
            }
        }
    }

    private fun showNotificationAPI26(
        title: String,
        body: String,
        nClass: Class<*>,
        hisId: String
    ) {
        val builder: Notification.Builder
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(this, nClass)
        intent.putExtra(Constants.SEND_ID, hisId)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val helper = NotificationHelper(this)
        builder = helper.getNotification(title, body, soundUri, resultPendingIntent)
        helper.manager!!.notify(Random().nextInt(), builder.build())
    }

    fun showNotification(title: String?, message: String?, nClass: Class<*>?, hisId: String?) {
        val intent = Intent(this, nClass)
        intent.putExtra(Constants.SEND_ID, hisId)
        @SuppressLint(
            "UnspecifiedImmutableFlag",
            "LaunchActivityFromNotification"
        ) val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        @SuppressLint("LaunchActivityFromNotification") val builder =
            NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(Random().nextInt(), builder.build())
    }

    private fun showNotificationAPI26ForChat(
        title: String?,
        body: String?,
        nClass: Class<*>,
        hisId: String?
    ) {
        val builder: Notification.Builder
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(this, nClass)
        intent.putExtra(Constants.SEND_ID, hisId)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent =
            stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val helper = NotificationHelper(this)
        builder = helper.getNotification(title, body, soundUri, resultPendingIntent)
        helper.manager!!.notify(Random().nextInt(), builder.build())
    }

    private fun showNotificationForChat(
        title: String?,
        message: String?,
        nClass: Class<*>?,
        hisId: String?
    ) {
        val intent = Intent(this, nClass)
        intent.putExtra(Constants.SEND_ID, hisId)
        @SuppressLint(
            "UnspecifiedImmutableFlag",
            "LaunchActivityFromNotification"
        ) val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        @SuppressLint("LaunchActivityFromNotification") val builder =
            NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(Random().nextInt(), builder.build())
    }
}