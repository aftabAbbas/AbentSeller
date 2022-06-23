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
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivitySignUpBinding

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
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var password: String
    private lateinit var cPassword: String

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
}