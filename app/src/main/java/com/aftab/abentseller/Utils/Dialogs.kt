package com.aftab.abentseller.Utils

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

@SuppressLint("StaticFieldLeak")
object Dialogs {
    private var sp: SharedPref? = null
    private lateinit var usersData: Users

    // Upload provider files or documents here
    fun uploadImage(
        context: Context,
        uri1: Uri?,
        storageReference: StorageReference,
        userId: String,
        fileName: String,
        type: String,
        size: String,
        filePath: String,
        marketPlaceId: String,
        productId: String
    ) {
        sp = SharedPref(context)
        if (uri1 != null) {
            val progressDialog = ProgressDialog(context, R.style.AppCompatAlertDialogStyle)
            progressDialog.setCancelable(false)
            progressDialog.show()
            val ref = storageReference.child("chat/")
            ref.child(System.currentTimeMillis().toString() + "").putFile(uri1)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    val uri = taskSnapshot.storage.downloadUrl
                    uri.addOnCompleteListener { task: Task<Uri>? ->
                        if (uri.isSuccessful) {
                            val imgURL = Objects.requireNonNull(uri.result).toString()
                            progressDialog.dismiss()
                            val msgKey = FireRef.CHATS.child(userId).push().key
                            uploadMyImg(
                                context,
                                imgURL,
                                userId,
                                msgKey,
                                fileName,
                                type,
                                size,
                                filePath,
                                marketPlaceId,
                                productId
                            )
                        }
                    }
                }.addOnFailureListener { e: Exception ->
                    progressDialog.dismiss()
                    Functions.showSnackBar(context, "Failed " + e.message)
                }.addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    progressDialog.setMessage("Sending " + progress.toInt() + "%")
                }
        }
    }

    private fun uploadMyImg(
        context: Context,
        imgURL: String,
        userId: String,
        msgKey: String?,
        fileName: String,
        type: String,
        size: String,
        filePath: String,
        marketPlaceId: String,
        productId: String
    ) {
        sp = SharedPref(context)
        usersData = sp!!.getUsers()!!
        if (msgKey != null) {
            val messages = Messages(
                Functions.getCurrentDate(), usersData.uid, fileName, msgKey,
                type, imgURL, size, "1", userId, filePath, "", productId
            )
            FireRef.CHATS.child(usersData.uid).child(userId)
                .child(productId)
                .child(msgKey)
                .setValue(messages)
                .addOnCompleteListener { task: Task<Void?>? ->
                    uploadHisImg(
                        context,
                        imgURL,
                        userId,
                        msgKey,
                        fileName,
                        type,
                        size,
                        filePath,
                        marketPlaceId,
                        productId
                    )
                }
                .addOnFailureListener { e: Exception ->
                    Toast.makeText(
                        context,
                        "" + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun uploadHisImg(
        context: Context,
        imgURL: String,
        userId: String,
        msgKey: String?,
        fileName: String,
        type: String,
        size: String,
        filePath: String,
        marketPlaceId: String,
        productId: String
    ) {

        sp = SharedPref(context)
        usersData = sp!!.getUsers()!!

        if (msgKey != null) {
            val messages = Messages(
                Functions.getCurrentDate(), usersData.uid, fileName, msgKey,
                type, imgURL, size, "0", userId, filePath, "", productId
            )
            FireRef.CHATS.child(userId).child(usersData.uid).child(productId).child(msgKey)
                .setValue(messages)
        }
    }
}