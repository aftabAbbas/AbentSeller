package com.aftab.abentseller.Activities.StartUp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aftab.abentseller.Activities.Main.HomeActivity
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityLoginBinding
import com.google.firebase.firestore.DocumentSnapshot

@SuppressLint("ClickableViewAccessibility")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sh: SharedPref
    private lateinit var uid: String
    private lateinit var email: String
    private lateinit var password: String
    private var isValid: Boolean = false
    private var isPasswordClicked: Boolean = true

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }


    private fun initUI() {

        sh = SharedPref(this)
        loadingDialog = LoadingDialog(this, "Loading")

    }


    private fun clickListeners() {

        binding.etPassword.setOnTouchListener { _: View, event: MotionEvent ->
            val drawableStart = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.etPassword.right - binding.etPassword.compoundDrawables[drawableStart].bounds.width()
                ) {
                    if (!isPasswordClicked) {
                        binding.etPassword.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(this, R.drawable.ic_eye_disabled),
                            null
                        )
                        isPasswordClicked = true
                    } else {
                        binding.etPassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(this, R.drawable.ic_eye_enabled),
                            null
                        )
                        isPasswordClicked = false
                    }

                }
            }
            false
        }

        binding.tvForgetPassword.setOnClickListener {

            startActivity(Intent(this, ForgetPasswordActivity::class.java))

        }

        binding.layoutSignUp.setOnClickListener {

            startActivity(Intent(this, SignUpActivity::class.java))

        }

        binding.btnLogin.setOnClickListener {


            isValid = false

            isValid = validateFields()

            if (isValid) {

                allowLogin()

            }

        }

    }

    private fun validateFields(): Boolean {

        email = binding.etEmail.text.toString().trim().lowercase()
        password = binding.etPassword.text.toString().trim()


        if (email.isEmpty()) {

            binding.etEmail.error = resources.getString(R.string.required)

        } else if (!Functions.isEmailValid(email)) {

            binding.etEmail.error = resources.getString(R.string.valid_email)

        } else if (password.isEmpty()) {

            binding.etPassword.error = resources.getString(R.string.required)

        } else {

            isValid = true

        }


        return isValid
    }

    private fun allowLogin() {

        loadingDialog.show()

        FireRef.mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    uid = FireRef.mAuth.currentUser?.uid.toString()

                    getDataFromFS()

                } else {

                    loadingDialog.dismiss()
                    Toast.makeText(this, "" + it.exception?.message, Toast.LENGTH_SHORT).show()

                }
            }

    }

    private fun getDataFromFS() {

        FireRef.USERS_REF
            .whereEqualTo(Constants.UID, uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    val users = documentSnapshot.toObject(Users::class.java)

                    if (users != null) {
                        checkUserType(users)
                    }


                } else {

                    loadingDialog.dismiss()
                    Toast.makeText(this, "FS Error " + it.exception?.message, Toast.LENGTH_SHORT)
                        .show()

                }

            }

    }

    private fun checkUserType(users: Users) {

        loadingDialog.dismiss()

        if (users.userType == Constants.SELLER) {

            saveToSp(users)


        } else {

            FireRef.mAuth.signOut()

            Toast.makeText(
                this,
                resources.getString(R.string.please_login_seller),
                Toast.LENGTH_SHORT
            ).show()

        }


    }

    private fun saveToSp(users: Users) {

        sh.putBoolean(Constants.IS_LOGGED_IN, true)
        sh.putBoolean(Constants.IS_EMAIL_SENT, false)
        sh.putBoolean(Constants.IS_ADDED_INFO, true)
        sh.putBoolean(Constants.ORDER_NOTI, true)
        sh.putBoolean(Constants.CHAT_NOTI, true)
        sh.putBoolean(Constants.REVIEW_NOTI, true)
        sh.putBoolean(Constants.DELIVERY_NOTI, true)
        sh.saveUsers(users)

        /*sh.putString(Constants.UID,uid)
        sh.putString(Constants.FIRST_NAME, fName)
        sh.putString(Constants.LAST_NAME, lName)
        sh.putString(Constants.EMAIL, email)
        sh.putString(Constants.PHONE, phone)
        sh.putString(Constants.PASSWORD, password)
        sh.putString(Constants.DTB, dtb)
        sh.putString(Constants.GENDER, password)
        sh.putString(Constants.COUNTRY, country)
        sh.putString(Constants.CITY, city)
        sh.putString(Constants.DP, dp)
        sh.putString(Constants.PLAN_TYPE, Constants.FREE)
        sh.putString(Constants.PLAN_DATE, planDate)
        sh.putString(Constants.USER_TYPE, Constants.SELLER)
        sh.putString(Constants.USER_STATUS,Constants.ACTIVE)
        sh.putString(Constants.LOGIN_TYPE, Constants.EMAIL)*/


        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)


    }

}