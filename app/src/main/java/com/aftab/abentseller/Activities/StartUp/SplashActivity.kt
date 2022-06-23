package com.aftab.abentseller.Activities.StartUp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Activities.Main.AddNewProductActivity
import com.aftab.abentseller.Activities.Main.HomeActivity
import com.aftab.abentseller.BuildConfig
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySplashBinding
    private lateinit var sh: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(binding.root)
        Functions.hideSystemUI(this)
        initUI()

        Handler(Looper.getMainLooper()).postDelayed({

            finish()
            
            if (sh.getBoolean(Constants.IS_EMAIL_SENT)) {

                if (sh.getBoolean(Constants.IS_ADDED_INFO)){

                    startActivity(Intent(this, CreateAccountActivity::class.java))

                }else{

                    startActivity(Intent(this, EmailVerificationActivity::class.java))

                }

            } else {


                if (!sh.getBoolean(Constants.IS_LOGGED_IN)) {
                    
                    startActivity(Intent(this, LoginActivity::class.java))

                } else {

                    startActivity(Intent(this, HomeActivity::class.java))

                }

            }


        }, Constants.SPLASH_DURATION)


    }

    private fun initUI() {

        sh = SharedPref(this)
        binding.tvVersion.text= BuildConfig.VERSION_NAME


    }
}