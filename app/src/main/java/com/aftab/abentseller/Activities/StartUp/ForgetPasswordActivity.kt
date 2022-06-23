package com.aftab.abentseller.Activities.StartUp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.LoadingDialog
import com.aftab.abentseller.databinding.ActivityForgetPasswordBinding

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private var email = ""
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        loadingDialog = LoadingDialog(this, "Loading")

    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        binding.btnSend.setOnClickListener {

            email = binding.etEmail.text.toString().trim()

            if (email == "") {

                binding.etEmail.error = resources.getString(R.string.required)

            } else {

                sendEmail()

            }

        }

    }

    private fun sendEmail() {

        loadingDialog.show()

        FireRef.mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    loadingDialog.dismiss()
                    Toast.makeText(
                        this,
                        resources.getString(R.string.pass_reset),
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()

                } else {

                    loadingDialog.dismiss()
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }

    }
}