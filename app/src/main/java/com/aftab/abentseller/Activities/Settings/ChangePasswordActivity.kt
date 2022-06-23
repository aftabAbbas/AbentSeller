package com.aftab.abentseller.Activities.Settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.LoadingDialog
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.HashMap


class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users
    private var context = this
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var user: FirebaseUser

    private var isValid: Boolean = false
    private var oldPwd: String = ""
    private var newPwd: String = ""
    private var confirmPwd: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        context = this@ChangePasswordActivity
        sh = SharedPref(context)
        usersData = sh.getUsers()!!
        user = FirebaseAuth.getInstance().currentUser!!
        loadingDialog = LoadingDialog(context, "Resetting")


    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        binding.btnChangePassword.setOnClickListener {

            isValid = false

            isValid = validateFields()

            if (isValid) {

                allowReAuthentication()

            }

        }

    }


    private fun validateFields(): Boolean {

        oldPwd = binding.etOldPwd.text.toString().trim()
        newPwd = binding.etNewPwd.text.toString().trim()
        confirmPwd = binding.etConfirmPwd.text.toString().trim()

        when {
            oldPwd.isEmpty() -> {

                binding.etOldPwd.error = resources.getString(R.string.required)

            }
            oldPwd != usersData.password -> {

                Toast.makeText(
                    this,
                    resources.getText(R.string.old_password_is),
                    Toast.LENGTH_SHORT
                )
                    .show()

            }
            newPwd.isEmpty() -> {

                binding.etNewPwd.error = resources.getString(R.string.required)

            }
            confirmPwd.isEmpty() -> {

                binding.etConfirmPwd.error = resources.getString(R.string.required)

            }
            newPwd != confirmPwd -> {

                Toast.makeText(this, resources.getText(R.string.both_password), Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {

                isValid = true

            }
        }


        return isValid
    }

    private fun allowReAuthentication() {

        loadingDialog.show()

        val credential = EmailAuthProvider
            .getCredential(usersData.email, oldPwd)


        user.reauthenticate(credential)
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    updatePassword()

                }

            }.addOnFailureListener {

                loadingDialog.dismiss()
                Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()

            }

    }

    private fun updatePassword() {

        user.updatePassword(confirmPwd)
            .addOnCompleteListener {

                uploadToFS()

            }.addOnFailureListener {

                loadingDialog.dismiss()
                Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()

            }

    }

    private fun uploadToFS() {

        val uid = usersData.uid

        val userInfo = HashMap<String, Any>()
        userInfo[Constants.PASSWORD] = confirmPwd

        FireRef.USERS_REF.document(uid)
            .update(userInfo)
            .addOnCompleteListener {

                saveToSp()

            }.addOnFailureListener {

                loadingDialog.dismiss()
                Toast.makeText(this, "FS Error: " + it.message, Toast.LENGTH_SHORT).show()

            }


    }

    private fun saveToSp() {

        val users: Users = usersData

        users.password = confirmPwd
        sh.saveUsers(users)

        loadingDialog.dismiss()

        Toast.makeText(this, resources.getText(R.string.password_change), Toast.LENGTH_SHORT).show()

    }
}