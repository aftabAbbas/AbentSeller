package com.aftab.abentseller.Activities.StartUp

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.databinding.ActivityAllowLocationBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

@Suppress("deprecation")
class AllowLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllowLocationBinding
    private lateinit var permissionListener: PermissionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllowLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListeners()
    }

    private fun checkPermissions() {

        permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {

                startActivityForResult(
                    Intent(
                        this@AllowLocationActivity,
                        ChooseLocationActivity::class.java
                    ), 10
                )

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    this@AllowLocationActivity,
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .check()
    }

    private fun clickListeners() {

        binding.btnAllow.setOnClickListener {

            checkPermissions()

        }

        binding.btnAllowWhile.setOnClickListener {

            checkPermissions()

        }

        binding.btnDoNotAllow.setOnClickListener {

            onBackPressed()

        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            if (requestCode == 10) {


                val from = data?.getStringExtra(Constants.FROM).toString()

                if (from == Constants.LOCATION) {

                    val lat = data?.getStringExtra(Constants.LAT).toString()
                    val lng = data?.getStringExtra(Constants.LNG).toString()

                    val returnIntent = Intent()
                    returnIntent.putExtra(Constants.LAT, lat)
                    returnIntent.putExtra(Constants.FROM, Constants.LOCATION)
                    returnIntent.putExtra(Constants.LNG, lng)
                    setResult(RESULT_OK, returnIntent)
                    finish()

                }


            }

        }
    }
}