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
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

@Suppress("deprecation")
@SuppressLint("ClickableViewAccessibility")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sh: SharedPref
    private lateinit var uid: String
    private lateinit var name: String
    private lateinit var dp: String
    private lateinit var email: String
    private lateinit var password: String
    private var isValid: Boolean = false
    private var isPasswordClicked: Boolean = true

    private lateinit var loadingDialog: LoadingDialog

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var gso: GoogleSignInOptions? = null
    private var accessTokenTracker: AccessTokenTracker? = null
    private var callbackManager: CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
        faceBookClickListener()
    }


    private fun initUI() {

        sh = SharedPref(this)
        loadingDialog = LoadingDialog(this, "Loading")


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso!!)

        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()

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

        binding.btnGoogleSignIn.setOnClickListener {

            loginWithGoogle()


        }

    }

    private fun loginWithGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, Constants.RC_G_SIGN_IN)
        loadingDialog.show()
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

                    getDataFromFS(Constants.EMAIL)

                } else {

                    loadingDialog.dismiss()
                    Toast.makeText(this, "" + it.exception?.message, Toast.LENGTH_SHORT).show()

                }
            }

    }

    private fun getDataFromFS(loginType: String) {

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

                        Toast.makeText(
                            this,
                            resources.getString(R.string.no_user_found),
                            Toast.LENGTH_SHORT
                        )
                            .show()

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

    private fun faceBookClickListener() {
        binding.loginButton?.setReadPermissions("email", "public_profile")
        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    loadingDialog.show()
                    handlerFacebookToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Toast.makeText(this@LoginActivity, "Cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LoginActivity, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            })
        accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken,
                currentAccessToken: AccessToken
            ) {
                if (currentAccessToken == null) {
                    FireRef.mAuth.signOut()
                }
            }
        }
        LoginManager.getInstance().retrieveLoginStatus(this, object : LoginStatusCallback {
            override fun onCompleted(accessToken: AccessToken) {
                // User was previously logged in, can log them in directly here.
                // If this callback is called, a popup notification appears that says
                // "Logged in as <User Name>"
            }

            override fun onFailure() {
                // No access token could be retrieved for the user
            }

            override fun onError(exception: java.lang.Exception) {
                // An error occurred
            }
        })
    }

    private fun handlerFacebookToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        FireRef.mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user: FirebaseUser = FireRef.mAuth.currentUser!!
                    uid = user.uid
                    name = user.displayName!!
                    email = user.email!!
                    dp =
                        Objects.requireNonNull(user.photoUrl)
                            .toString()
                    getDataFromFS(Constants.FACEBOOK)
                } else {
                    loadingDialog.dismiss()
                }
            }
            .addOnFailureListener { e: java.lang.Exception ->
                Toast.makeText(this, "Failure " + e.message, Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
    }

}