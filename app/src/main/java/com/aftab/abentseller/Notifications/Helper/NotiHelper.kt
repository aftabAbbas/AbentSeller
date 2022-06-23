package com.aftab.abentseller.Notifications.Helper

import android.content.Context
import android.widget.Toast
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Notifications.Network.ApiClient
import com.aftab.abentseller.Notifications.Network.ApiService
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.Constants.getRemoteMessageHeaders
import com.aftab.abentseller.Utils.FireRef
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("deprecation")
object NotiHelper {
    fun getUserFcmToken(
        context: Context,
        hisId: String,
        title: String,
        msgBody: String,
        msgType: String,
        myId: String
    ) {
        FireRef.USERS_REF.document(hisId).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val user = task.result.toObject(Users::class.java)
                    if (user != null) {
                        sendNotification(context, user.fcm, title, msgBody, msgType, hisId, myId)
                    }
                }
            }
    }

    fun sendNotification(
        context: Context,
        hisToken: String,
        title: String,
        msgBody: String,
        msgType: String,
        hisId: String,
        myId: String
    ) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task: Task<InstanceIdResult?> ->
            if (task.isSuccessful && task.result != null) {
                val myToken = task.result!!.token
                initiateNotification(
                    context,
                    hisToken,
                    myToken,
                    title,
                    msgBody,
                    msgType,
                    hisId,
                    myId
                )
            }
        }
    }

    private fun initiateNotification(
        context: Context,
        receiverToken: String?,
        senderToken: String,
        title: String,
        msgBody: String,
        msgType: String,
        hisId: String,
        myId: String
    ) {
        try {
            val token = JSONArray()
            if (receiverToken != null) {
                token.put(receiverToken)
            }
            val body = JSONObject()
            val data = JSONObject()
            data.put(Constants.REMOTE_MSG_TYPE, msgType)
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, senderToken)
            data.put(Constants.SEND_ID, myId)
            data.put(Constants.KEY_TITLE, title)
            data.put(Constants.KEY_BODY, msgBody)
            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token)
            sendRemoteMessage(context, body.toString())
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendRemoteMessage(context: Context, remoteInvitation: String) {
        ApiClient.getClient().create(ApiService::class.java).sendRemoteMessage(
            getRemoteMessageHeaders(), remoteInvitation
        ).enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun sendNotificationToMulti(
        context: Context,
        receiverTokens: ArrayList<Users>,
        title: String,
        msgBody: String,
        msgType: String,
        hisId: String,
        myId: String
    ) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task: Task<InstanceIdResult?> ->
            if (task.isSuccessful && task.result != null) {
                val myToken = task.result!!.token
                initiateNotificationToMulti(
                    context,
                    receiverTokens,
                    myToken,
                    title,
                    msgBody,
                    msgType,
                    hisId,
                    myId
                )
            }
        }
    }

    private fun initiateNotificationToMulti(
        context: Context,
        receiverTokens: ArrayList<Users>,
        senderToken: String,
        title: String,
        msgBody: String,
        msgType: String,
        hisId: String,
        myId: String
    ) {
        try {
            val token = JSONArray()
            if (receiverTokens.size > 0) {

                for (i in receiverTokens) {
                    token.put(i.fcm)
                }
            }
            val body = JSONObject()
            val data = JSONObject()
            data.put(Constants.REMOTE_MSG_TYPE, msgType)
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, senderToken)
            data.put(Constants.SEND_ID, myId)
            data.put(Constants.KEY_TITLE, title)
            data.put(Constants.KEY_BODY, msgBody)
            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token)

            sendRemoteMessage(context, body.toString())
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}