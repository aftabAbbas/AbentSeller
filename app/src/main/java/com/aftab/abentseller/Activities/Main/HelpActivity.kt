package com.aftab.abentseller.Activities.Main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aftab.abentseller.Model.Notifications
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Notifications.Helper.NotiHelper
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.LoadingDialog
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivityHelpBinding
import com.aftab.abentuser.Model.Admin
import com.aftab.abentuser.Model.Help

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding
    private lateinit var sh: SharedPref
    private lateinit var users: Users
    private lateinit var adminData: Users
    private lateinit var loadingDialog: LoadingDialog
    private var adminFCMList = ArrayList<Users>()

    private var name = ""
    private var email = ""
    private var des = ""
    private var title = ""
    private  var message = ""
    private var notiCount = 0
    private var isValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        sh = SharedPref(this)
        loadingDialog = LoadingDialog(this, "Loading")

        users = sh.getUsers()!!

        name = users.fname + " " + users.lname
        email = users.email

        binding.etName.setText(name)
        binding.etEmail.setText(email)

        getAdminData()

    }

    private fun getAdminData() {

        loadingDialog.show()

        FireRef.ADMIN_REF
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    adminFCMList.clear()

                    for (document in it.result) {

                        val admin: Admin =
                            document.toObject(Admin::class.java)

                        adminData = Users()
                        adminData.fcm = admin.fcm
                        adminFCMList.add(adminData)

                    }

                    loadingDialog.dismiss()

                }

            }

    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        binding.btnSend.setOnClickListener {

            isValid = false

            isValid = validateFields()

            if (isValid) {

                uploadToFS()

            }

        }


    }

    private fun uploadToFS() {

        loadingDialog.show()

        val id = FireRef.myRef.push().key
        val uid = users.uid

        val help = Help(id!!, System.currentTimeMillis().toString(), uid, name, email, des)

        FireRef.HELP_REF.document(id)
            .set(help)
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    if (adminFCMList.size>0) {

                        sendNotificationToAdmins()

                    }else{

                        loadingDialog.dismiss()
                        Toast.makeText(this, resources.getString(R.string.help_request), Toast.LENGTH_SHORT).show()

                    }


                } else {

                    loadingDialog.dismiss()
                    Toast.makeText(this, "Error: " + it.exception?.message, Toast.LENGTH_SHORT)
                        .show()

                }

            }

    }

    private fun sendNotificationToAdmins() {

        val gender = users.gender.trim()


        message = if (gender== Constants.MAlE){

            name + resources.getString(R.string.needs_your1)

        }else{

            name + resources.getString(R.string.needs_your2)

        }

        title = "Help!"


        NotiHelper.sendNotificationToMulti(
            this,
            adminFCMList,
            title,
            message,
            Constants.ORDER_NOTI,
            "",
            users.uid
        )

        notiCount = 0
        saveNotificationToDB(title, message)

    }

    private fun saveNotificationToDB(title: String, message: String) {

        if (notiCount < adminFCMList.size) {

            val id = FireRef.myRef.push().key!!

            val notifications = Notifications(
                id,
                title,
                message,
                System.currentTimeMillis().toString(),
                adminFCMList[notiCount].uid,
                users.uid,
                false
            )

            FireRef.NOTIFICATIONS_REF
                .document(id)
                .set(notifications)
                .addOnCompleteListener {

                    notiCount++
                    saveNotificationToDB(title, message)

                }
                .addOnFailureListener {

                    notiCount++
                    saveNotificationToDB(title, message)

                }

        } else {


            loadingDialog.dismiss()
            Toast.makeText(this, resources.getString(R.string.help_request), Toast.LENGTH_SHORT).show()

        }

    }


    private fun validateFields(): Boolean {

        name = binding.etName.text.toString()
        email = binding.etEmail.text.toString()
        des = binding.etProblem.text.toString()

        if (name.isEmpty()) {

            binding.etName.error = resources.getString(R.string.required)

        } else if (email.isEmpty()) {

            binding.etEmail.error = resources.getString(R.string.required)

        } else if (des.isEmpty()) {

            binding.etProblem.error = resources.getString(R.string.required)

        } else {

            isValid = true

        }

        return isValid
    }
}