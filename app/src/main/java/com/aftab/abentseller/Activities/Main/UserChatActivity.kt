package com.aftab.abentseller.Activities.Main

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Adapters.Recycler.MessagesAdapter
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.RecentChat
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityUserChatBinding
import com.aftab.abentseller.Notifications.Helper.NotiHelper
import com.aftab.abentseller.Utils.ChatFunctions
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.storage.StorageReference
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*

@SuppressLint("NotifyDataSetChanged")
@Suppress("deprecation")
class UserChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserChatBinding
    private lateinit var productsIntent: Products
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users
    private lateinit var receiverData: Users
    private lateinit var recentChat: RecentChat
    private lateinit var storageReference: StorageReference
    private var locationPermissionListener: PermissionListener? = null
    private var galleryPermissionListener: PermissionListener? = null
    private var filePermissionListener: PermissionListener? = null
    private var videoPermissionListener: PermissionListener? = null

    private var messagesList: ArrayList<Messages> = ArrayList<Messages>()
    private lateinit var messagesAdapter: MessagesAdapter

    private var name = ""
    private var fileSize = ""
    private var dp = ""
    private var isLocation = false
    private var isOneMsgSent = false
    private var onChildAddedCalled = false
    private var fName = ""
    private var lName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
        setAdapter()
    }

    private fun initUI() {

        sh = SharedPref(this)
        usersData = sh.getUsers()!!

        storageReference = FireRef.storageRef.reference

        loadingDialog = LoadingDialog(this, "Loading")

        productsIntent = intent.getSerializableExtra(Constants.PRODUCTS) as Products
        recentChat = intent.getSerializableExtra(Constants.KEY_REF_CHATS) as RecentChat

        receiverData = recentChat.users

        setUI()

    }

    private fun setUI() {

        fName = receiverData.fname
        lName = receiverData.lname
        fName = fName.substring(0, 1).toUpperCase(Locale.ROOT) + fName.substring(1)
            .toLowerCase(
                Locale.ROOT
            )
        lName = lName.substring(0, 1).toUpperCase(Locale.ROOT) + lName.substring(1)
            .toLowerCase(
                Locale.ROOT
            )

        name = "$fName $lName"

        dp = receiverData.dp

        binding.toolbar.title = name

        Glide.with(this)
            .load(dp)
            .placeholder(R.drawable.place_holder)
            .into(binding.ivUser)

        getUserMessages(receiverData.uid)
    }


    private fun clickListeners() {

        binding.ivLink.setOnClickListener {
            openMediaDialog()
        }

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }


        binding.etMsg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.ivLink.visibility = View.VISIBLE
                } else {
                    binding.ivLink.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.btnSend.setOnClickListener {
            sendMsg()
        }


    }

    private fun sendMsg() {
        val msg: String = binding.etMsg.text.toString().trim { it <= ' ' }
        val msgKey: String = FireRef.CHATS.child(receiverData.uid).push().key.toString()
        if (msg.isEmpty()) {
            Toast.makeText(this, "Type a message first!", Toast.LENGTH_SHORT).show()
        } else {

            addMsgToMyDB(msg, msgKey, Constants.TEXT, "")
            binding.etMsg.setText("")
            binding.rvChat.smoothScrollToPosition(messagesList.size)

            // send notification
            NotiHelper.sendNotification(
                this,
                receiverData.fcm,
                "${usersData.fname} ${usersData.lname}",
                msg,
                Constants.REMOTE_MSG_CHAT,
                receiverData.uid,
                usersData.uid
            )
        }
    }

    private fun addMsgToHisDB(msg: String, msgKey: String?, type: String, url: String) {
        if (msgKey != null) {
            val messages = Messages(
                DateUtils.getCurrentDate(), usersData.uid, msg, msgKey, type, url, "",
                "0", receiverData.uid, "", "", productsIntent.id
            )

            FireRef.CHATS.child(receiverData.uid).child(usersData.uid).child(productsIntent.id)
                .child(msgKey)
                .setValue(messages)
                .addOnFailureListener { e ->

                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

                }
        }
    }

    private fun addMsgToMyDB(msg: String, msgKey: String?, type: String, url: String) {
        if (msgKey != null) {
            val messages = Messages(
                DateUtils.getCurrentDate(), usersData.uid, msg, msgKey, type, url, "",
                "0", receiverData.uid, "", "", productsIntent.id
            )

            FireRef.CHATS.child(usersData.uid).child(receiverData.uid).child(productsIntent.id)
                .child(msgKey)
                .setValue(messages)
                .addOnFailureListener { e ->
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

                }
                .addOnCompleteListener { addMsgToHisDB(msg, msgKey, type, url) }
        }
    }


    private fun openMediaDialog() {
        val dialog = Dialog(this@UserChatActivity)
        dialog.setContentView(R.layout.chose_media_dialog)
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )


        Functions.setDialogGravity(dialog)

        val layoutCamera: LinearLayout = dialog.findViewById(R.id.layout_camera)
        val layoutGallery: LinearLayout = dialog.findViewById(R.id.layout_gallery)
        val layoutVideo: LinearLayout = dialog.findViewById(R.id.layout_video)
        val layoutLocation: LinearLayout = dialog.findViewById(R.id.layout_location)
        val layoutFile: LinearLayout = dialog.findViewById(R.id.layout_file)
        layoutCamera.setOnClickListener {
            choseImage()
            dialog.dismiss()
        }
        layoutLocation.setOnClickListener {
            TedPermission.with(this).setPermissionListener(locationPermissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION).check()
        }

        locationPermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val i = Intent(this@UserChatActivity, LocationPickerActivity::class.java)
                startActivityForResult(i, Constants.OPEN_MAP_ACTIVITY)
                isLocation = true
                dialog.dismiss()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }

        filePermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Functions.pickFile(this@UserChatActivity)
                isLocation = false
                dialog.dismiss()

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }

        galleryPermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Functions.pickImageFromGallery(this@UserChatActivity)
                isLocation = false
                dialog.dismiss()

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }

        videoPermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Functions.pickVideoFromGallery(this@UserChatActivity)
                isLocation = false
                dialog.dismiss()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }

        layoutFile.setOnClickListener {
            TedPermission.with(this).setPermissionListener(filePermissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).check()

        }
        layoutGallery.setOnClickListener {
            TedPermission.with(this).setPermissionListener(galleryPermissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).check()

        }
        layoutVideo.setOnClickListener {
            TedPermission.with(this).setPermissionListener(videoPermissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).check()

        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun choseImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    private fun getUserMessages(receiverId: String) {

        binding.progressBar.visibility = View.VISIBLE

        FireRef.CHATS.child(usersData.uid).child(receiverId)
            .child(productsIntent.id)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    onChildAddedCalled = true

                    val messages = snapshot.getValue(Messages::class.java)
                    if (messages != null) {
                        messagesList.add(messages)

                        if (!isOneMsgSent && messages.relatedTo == productsIntent.id) {

                            isOneMsgSent = true

                        }
                    }


//                FireRef.CHATS.child(sp.get(Constants.USER_ID)).child(userId).child(messages.getMsgKey()).child(Constants.IS_MSG_READ).setValue("1");
                    messagesAdapter.notifyDataSetChanged()
                    val linearLayoutManager =
                        LinearLayoutManager(this@UserChatActivity, RecyclerView.VERTICAL, false)
                    binding.rvChat.layoutManager = linearLayoutManager
                    linearLayoutManager.scrollToPositionWithOffset(
                        messagesList.size - 1,
                        -999999999
                    )
                    binding.progressBar.visibility = View.GONE
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    onChildAddedCalled = true
                    messagesAdapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })



        Handler(Looper.getMainLooper()).postDelayed({

            if (!onChildAddedCalled) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "" + resources.getString(R.string.no_data_found),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }, 3000)


    }

    private fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        messagesAdapter =
            MessagesAdapter(this, messagesList, usersData.uid, receiverData.uid,productsIntent!!.id)
        binding.rvChat.layoutManager = linearLayoutManager
        binding.rvChat.adapter = messagesAdapter
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            assert(data != null)
            if (data!!.data != null) {
                if (!isLocation) {

                    val relatedTo: String = if (isOneMsgSent) {

                        ""

                    } else {

                        productsIntent.id

                    }

                    val filePath = data.data
                    if (filePath != null) {
                        val type: String? = ChatFunctions.getMimeType(this, filePath)
                        val fileName: String? = ChatFunctions.getFileName(this, filePath)
                        val realFilePath: String? = ChatFunctions.getPath(this, filePath)
                        fileSize = if (filePath.toString().contains("content://")) {
                            ChatFunctions.getFileSize(this, filePath).toString()
                        } else {
                            ChatFunctions.getFileSize2(filePath).toString()
                        }
                        val size: String =
                            ChatFunctions.getStringSizeLengthFile(fileSize).toString()
                        when (type) {
                            Constants.JPG -> if (!filePath.path?.isEmpty()!!)
                                Dialogs.uploadImage(
                                    this,
                                    filePath,
                                    storageReference,
                                    receiverData.uid,
                                    fileName!!,
                                    Constants.JPG,
                                    size,
                                    realFilePath!!,
                                    relatedTo,
                                    productsIntent.id
                                )
                            Constants.PNG -> if (!filePath.path?.isEmpty()!!)
                                Dialogs.uploadImage(
                                    this,
                                    filePath,
                                    storageReference,
                                    receiverData.uid,
                                    fileName!!,
                                    Constants.PNG,
                                    size,
                                    realFilePath!!,
                                    relatedTo,
                                    productsIntent.id
                                )
                            Constants.MP4 -> if (!filePath.path?.isEmpty()!!
                            ) Dialogs.uploadImage(
                                this,
                                filePath,
                                storageReference,
                                receiverData.uid,
                                fileName!!,
                                Constants.MP4,
                                size,
                                realFilePath!!,
                                relatedTo,
                                productsIntent.id
                            )
                            Constants.PDF -> if (!filePath.path?.isEmpty()!!
                            ) Dialogs.uploadImage(
                                this,
                                filePath,
                                storageReference,
                                receiverData.uid,
                                fileName!!,
                                Constants.PDF,
                                size,
                                realFilePath!!,
                                relatedTo,
                                productsIntent.id
                            )
                            Constants.APK -> if (!filePath.path?.isEmpty()!!)
                                Dialogs.uploadImage(
                                    this,
                                    filePath,
                                    storageReference,
                                    receiverData.uid,
                                    fileName!!,
                                    Constants.APK,
                                    size,
                                    realFilePath!!,
                                    relatedTo,
                                    productsIntent.id
                                )
                            Constants.DOCX -> if (!filePath.path?.isEmpty()!!)
                                Dialogs.uploadImage(
                                    this,
                                    filePath,
                                    storageReference,
                                    receiverData.uid,
                                    fileName!!,
                                    Constants.DOCX,
                                    size,
                                    realFilePath!!,
                                    relatedTo,
                                    productsIntent.id
                                )
                            Constants.ZIP -> if (!filePath.path?.isEmpty()!!)
                                Dialogs.uploadImage(
                                    this,
                                    filePath,
                                    storageReference,
                                    receiverData.uid,
                                    fileName!!,
                                    Constants.ZIP,
                                    size,
                                    realFilePath!!,
                                    relatedTo,
                                    productsIntent.id
                                )
                            Constants.JAVA -> if (!filePath.path?.isEmpty()!!)
                                Dialogs.uploadImage(
                                    this,
                                    filePath,
                                    storageReference,
                                    receiverData.uid,
                                    fileName!!,
                                    Constants.JAVA,
                                    size,
                                    realFilePath!!,
                                    relatedTo,
                                    productsIntent.id
                                )
                            Constants.XML -> if (!filePath.path?.isEmpty()!!)
                                Dialogs.uploadImage(
                                    this,
                                    filePath,
                                    storageReference,
                                    receiverData.uid,
                                    fileName!!,
                                    Constants.XML,
                                    size,
                                    realFilePath!!,
                                    relatedTo,
                                    productsIntent.id
                                )
                            else -> Toast.makeText(
                                this,
                                "Invalid selected file type!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                if (requestCode == Constants.OPEN_MAP_ACTIVITY) {
                    val lat = data.getStringExtra("lat")
                    val lng = data.getStringExtra("lon")
                    val msg = "$lat,$lng"
                    val msgKey = FireRef.CHATS.child(receiverData.uid).push().key
                    isLocation = false
                    if (lat!!.isNotEmpty() && lng!!.isNotEmpty()) {
                        try {
                            val geocoder = Geocoder(this, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(
                                lat.toDouble(), lng.toDouble(), 1
                            )
                            val address = addresses[0].getAddressLine(0)
                            addMsgToMyDB(address, msgKey, Constants.LOCATION, msg)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    /*override fun onResume() {
        super.onResume()
        if (sellerData.uid != null) {
            //  checkUserOnlineStatus(sellerData.uid)
        }
    }*/

    /* private fun checkUserOnlineStatus(hisId: String) {
         FireRef.USERS.document(hisId).get().addOnCompleteListener { task ->
             if (task.isSuccessful()) {
                 val user: User = task.getResult().toObject(User::class.java)
                 if (user != null) {
                     lastSeenTime = user.getOnlineStatus()
                     calculateTimeDifference(hisId)
                 }
             }
         }
     }*//*

    private fun calculateTimeDifference(hisId: String) {
        if (lastSeenTime != null) {
            val timeDifference: Long = Functions.checkTimeDifference(
                lastSeenTime,
                Functions.getCurrentDate(),
                "dd/MM/yyyy HH:mm:ss Z"
            )
            if (timeDifference < 10) {
                ivStatus.setVisibility(View.VISIBLE)
            } else {
                ivStatus.setVisibility(View.GONE)
            }
        }
        Handler(Looper.myLooper()!!).postDelayed({
            checkUserOnlineStatus(
                hisId
            )
        }, 5000)
    }*/

    override fun onDestroy() {
        super.onDestroy()
        if (!this.isDestroyed) {
            Glide.with(this).pauseRequests()
        }
    }

/*
    private fun openMediaDialog() {
        val dialog = Dialog(this@UserChatActivity)
        dialog.setContentView(R.layout.chose_media_dialog)
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )


        Functions.setDialogGravity(dialog)

        val layoutCamera: LinearLayout = dialog.findViewById(R.id.layout_camera)
        val layoutGallery: LinearLayout = dialog.findViewById(R.id.layout_gallery)
        val layoutVideo: LinearLayout = dialog.findViewById(R.id.layout_video)
        val layoutLocation: LinearLayout = dialog.findViewById(R.id.layout_location)
        val layoutFile: LinearLayout = dialog.findViewById(R.id.layout_file)
        layoutCamera.setOnClickListener {

            dialog.dismiss()
        }
        layoutLocation.setOnClickListener {

            dialog.dismiss()

        }

        layoutFile.setOnClickListener {

            dialog.dismiss()
        }
        layoutGallery.setOnClickListener {

            dialog.dismiss()
        }
        layoutVideo.setOnClickListener {

            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }*/
}