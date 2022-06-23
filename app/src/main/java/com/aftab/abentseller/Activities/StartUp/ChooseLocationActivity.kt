package com.aftab.abentseller.Activities.StartUp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivityChooseLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.gun0912.tedpermission.PermissionListener
import java.io.IOException
import java.util.*

class ChooseLocationActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var sh: SharedPref
    private var context = this
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    var isGPSEnable = false
    var isNetworkEnable = false
    private lateinit var midLatLng: LatLng
    private lateinit var address: String
    private lateinit var lat: String
    private lateinit var lng: String
    private lateinit var addressList: List<Address>
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var permissionListener: PermissionListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {
        context = this@ChooseLocationActivity
        sh = SharedPref(this)
        mainInit()

        Functions.setStatusBarTransparent(this)

    }

    private fun mainInit() {
        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
        mapFragment.getMapAsync(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME, MIN_DISTANCE, this
        )
    }

    private fun clickListeners() {

        binding.btnSave.setOnClickListener {

            back()

        }
        permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
                isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnable =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (!isGPSEnable && !isNetworkEnable) {
                    Functions.requestLocationTurnOn(context)
                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    context,
                    "Please allow permission to use location feature. Unless you won't use this feature.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.cvSearch.setOnClickListener {

            val location = binding.etLocation.text.toString().trim()
            val geocoder = Geocoder(context)

            try {
                addressList = geocoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (addressList.isNotEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            } else {
                Functions.showSnackBar(context, "Location not found!")
            }

        }

        mapFragment.getMapAsync(this)

        binding.ivBack.setOnClickListener {

            back()

        }

    }

    override fun onBackPressed() {

        back()

    }

    private fun back() {

        lat = midLatLng.latitude.toString() + ""
        lng = midLatLng.longitude.toString() + ""

        /* sh.putString(Constants.LAT, lat)
         sh.putString(Constants.LNG, lng)

         val intent = Intent(this, CreateAccountActivity::class.java)
         intent.putExtra(Constants.FROM, Constants.LOCATION)
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
         startActivity(intent)*/


        val returnIntent = Intent()
        returnIntent.putExtra(Constants.LAT, lat)
        returnIntent.putExtra(Constants.FROM, Constants.LOCATION)
        returnIntent.putExtra(Constants.LNG, lng)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mMap.isMyLocationEnabled = true
        mMap.setOnCameraIdleListener {
            midLatLng = mMap.cameraPosition.target
            if (midLatLng.latitude != 0.0 && midLatLng.longitude != 0.0) {

                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        midLatLng.latitude, midLatLng.longitude, 1
                    )
                    address = addresses[0].getAddressLine(0)
                    //binding.tvSend.visibility = View.VISIBLE

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        mMap.animateCamera(cameraUpdate)
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    companion object {
        private const val MIN_TIME: Long = 400
        private const val MIN_DISTANCE = 1000f
    }
}