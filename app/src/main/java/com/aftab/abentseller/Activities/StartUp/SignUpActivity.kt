package com.aftab.abentseller.Activities.StartUp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aftab.abentseller.Activities.Main.HomeActivity
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivitySignUpBinding
import com.facebook.AccessTokenTracker
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

@SuppressLint("ClickableViewAccessibility")
@Suppress("deprecation")
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var sh: SharedPref

    private var isValid: Boolean = false
    private var isPasswordClicked: Boolean = true
    private var isCPasswordClicked: Boolean = true
    private lateinit var fName: String
    private lateinit var lName: String
    private lateinit var uid: String
    private lateinit var name: String
    private lateinit var dp: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var password: String
    private lateinit var cPassword: String
    private lateinit var planDate: String


    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var gso: GoogleSignInOptions? = null
    private var accessTokenTracker: AccessTokenTracker? = null
    private var callbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
        addPasswordPatternWatcher()
    }

    private fun initUI() {

        sh = SharedPref(this)
        loadingDialog = LoadingDialog(this, resources.getString(R.string.loading))

    }


    private fun clickListeners() {

        binding.layoutSignUp.setOnClickListener {

            onBackPressed()

        }

        binding.btnSignUp.setOnClickListener {

            sh.clearPreferences()

            isValid = validateFields()

            if (isValid) {

                checkEmailExistence()

            }

        }



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

        binding.etCPassword.setOnTouchListener { _: View, event: MotionEvent ->
            val drawableStart = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.etCPassword.right - binding.etCPassword.compoundDrawables[drawableStart].bounds.width()
                ) {
                    if (!isCPasswordClicked) {
                        binding.etCPassword.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        binding.etCPassword.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(this, R.drawable.ic_eye_disabled),
                            null
                        )
                        isCPasswordClicked = true
                    } else {
                        binding.etCPassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        binding.etCPassword.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(this, R.drawable.ic_eye_enabled),
                            null
                        )
                        isCPasswordClicked = false
                    }

                }
            }
            false
        }

    }

    private fun addPasswordPatternWatcher() {

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvPasswordValidator.visibility = View.VISIBLE
                when {
                    s.isEmpty() -> {
                        binding.tvPasswordValidator.text = resources.getString(R.string.not_enter)
                        binding.tvPasswordValidator.setTextColor(resources.getColor(R.color.red))
                    }
                    s.length < 6 -> {
                        binding.tvPasswordValidator.text = resources.getString(R.string.easy)
                        binding.tvPasswordValidator.setTextColor(resources.getColor(R.color.yellow))
                    }
                    s.length < 10 -> {
                        binding.tvPasswordValidator.text = resources.getString(R.string.medium)
                        binding.tvPasswordValidator.setTextColor(resources.getColor(R.color.green))
                    }
                    s.length < 15 -> {
                        binding.tvPasswordValidator.text = resources.getString(R.string.strong)
                        binding.tvPasswordValidator.setTextColor(resources.getColor(R.color.teal_200))
                    }
                    else -> {
                        binding.tvPasswordValidator.text =
                            resources.getString(R.string.password_max)
                        binding.tvPasswordValidator.setTextColor(resources.getColor(R.color.red))
                    }
                }
                if (s.length == 15) {
                    binding.etPassword.filters = arrayOf<InputFilter>(LengthFilter(15))
                    val shake =
                        AnimationUtils.loadAnimation(this@SignUpActivity, R.anim.shake_layout)
                    binding.etPassword.startAnimation(shake)
                    Functions.vibrate(this@SignUpActivity,100)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.etCPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvCPasswordValidator.visibility = View.VISIBLE
                binding.tvCPasswordValidator.setTextColor(resources.getColor(R.color.red))

                if (s.isEmpty()) {
                    binding.tvCPasswordValidator.setText(R.string.not_enter)
                    binding.tvCPasswordValidator.setTextColor(resources.getColor(R.color.red))
                }

                if (binding.etPassword.text
                        .toString().length == s.length && binding.etPassword.text
                        .toString().trim { it <= ' ' }.isNotEmpty()
                ) {
                    if (binding.etPassword.text.toString().contentEquals(s)) {
                        binding.tvCPasswordValidator.setText(R.string.match)
                        binding.tvCPasswordValidator.setTextColor(resources.getColor(R.color.green))
                    } else {
                        binding.tvCPasswordValidator.setText(R.string.not_match)
                        binding.tvCPasswordValidator.setTextColor(resources.getColor(R.color.red))
                    }
                } else if (s.isNotEmpty()) {
                    binding.tvCPasswordValidator.setText(R.string.not_match)
                    binding.tvCPasswordValidator.setTextColor(resources.getColor(R.color.red))
                }

                if (s.length == 15) {
                    binding.etCPassword.filters = arrayOf<InputFilter>(LengthFilter(15))
                    val shake =
                        AnimationUtils.loadAnimation(this@SignUpActivity, R.anim.shake_layout)
                    binding.etCPassword.startAnimation(shake)
                    Functions.vibrate(this@SignUpActivity,100)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    private fun validateFields(): Boolean {

        fName = binding.etFName.text.toString().trim()
        lName = binding.etLName.text.toString().trim()
        email = binding.etEmail.text.toString().trim().lowercase()
        phone = binding.etPhone.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        cPassword = binding.etCPassword.text.toString().trim()

        if (fName.isEmpty()) {

            binding.etFName.error = resources.getString(R.string.required)

        } else if (lName.isEmpty()) {

            binding.etLName.error = resources.getString(R.string.required)

        } else if (email.isEmpty()) {

            binding.etEmail.error = resources.getString(R.string.required)

        } else if (!Functions.isEmailValid(email)) {

            binding.etEmail.error = resources.getString(R.string.valid_email)

        } else if (phone.isEmpty()) {

            binding.etPhone.error = resources.getString(R.string.required)

        } else if (password.isEmpty()) {

            binding.etPassword.error = resources.getString(R.string.required)

        } else if (cPassword.isEmpty()) {

            binding.etCPassword.error = resources.getString(R.string.required)

        } else if (password != cPassword) {

            Toast.makeText(this, resources.getString(R.string.both_password), Toast.LENGTH_SHORT)
                .show()
        } else {

            isValid = true
        }

        return isValid
    }

    private fun checkEmailExistence() {

        loadingDialog.show()


        FireRef.USERS_REF.whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result.documents.size > 0) {

                    loadingDialog.dismiss()
                    Toast.makeText(
                        this,
                        resources.getString(R.string.this_email),
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    loadingDialog.dismiss()
                    goNext()

                }

            }.addOnFailureListener {

                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

            }

    }

    private fun goNext() {

        sh.putString(Constants.FIRST_NAME, fName)
        sh.putString(Constants.LAST_NAME, lName)
        sh.putString(Constants.EMAIL, email)
        sh.putString(Constants.PHONE, phone)
        sh.putString(Constants.PASSWORD, password)

        val intent = Intent(this, EmailVerificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_G_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                loadingDialog.dismiss()
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        // for facebook
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FireRef.mAuth.signInWithCredential(credential).addOnCompleteListener(
            this
        ) { task: Task<AuthResult?> ->
            if (task.isSuccessful) {
                val user: FirebaseUser = FireRef.mAuth.currentUser!!
                uid = user.uid
                name = user.displayName.toString()
                email = user.email!!
                dp = Objects.requireNonNull(user.photoUrl)
                    .toString()
                getDataFromFS(Constants.GOOGLE)
            } else {
                Functions.showSnackBar(
                    this,
                    task.exception?.message
                )
                loadingDialog.dismiss()
            }
        }.addOnFailureListener { e: Exception ->
            loadingDialog.dismiss()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDataFromFS(signInBy: String) {

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
                    if (it.exception?.message == null) {

                        //add user to fb
                        uploadUserDataToFS(signInBy)

                    } else {
                        Toast.makeText(
                            this,
                            "FS Error " + it.exception?.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

            }

    }

    private fun uploadUserDataToFS(signInBy: String) {

        planDate = System.currentTimeMillis().toString()
        val dtb = DateUtils.getCurrentDate(DateFormats.dateFormat3)

        val separated = name.split(" ".toRegex()).toTypedArray()
        fName = separated[0]
        lName = separated[1]

        val users = Users(
            uid,
            fName,
            lName,
            email,
            "",
            dp,
            "",
            Constants.MAlE,
            dtb!!,
            "",
            "",
            "",
            "",
            "",
            "",
            planDate,
            Constants.FREE,
            Constants.SELLER,
            Constants.ACTIVE,
            signInBy,
            "",
            Constants.ENGLISH,
            Constants.ONLINE
        )

        FireRef.USERS_REF.document(uid)
            .set(users)
            .addOnCompleteListener {

                saveToSp(users)

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


        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)


    }
}