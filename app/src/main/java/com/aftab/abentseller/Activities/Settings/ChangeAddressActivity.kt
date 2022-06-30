package com.aftab.abentseller.Activities.Settings

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityChangeAddressBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import java.io.IOException
import java.util.*


class ChangeAddressActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var binding: ActivityChangeAddressBinding
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users
    private var context = this
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    var isGPSEnable = false
    var isNetworkEnable = false
    private lateinit var midLatLng: LatLng
    private lateinit var address: String
    private lateinit var lat: String
    private lateinit var lng: String
    private var addressLat: Double = 0.0
    private var addressLng: Double = 0.0
    private lateinit var addressList: List<Address>
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var permissionListener: PermissionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        context = this@ChangeAddressActivity
        loadingDialog = LoadingDialog(context, "Uploading")
        sh = SharedPref(this)
        usersData = sh.getUsers()!!
        Functions.setStatusBarTransparent(this)

        mainInit()
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

        binding.ivBack.setOnClickListener {

            onBackPressed()

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

            val location = binding.etSearch.text.toString().trim()
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

        binding.btnSave.setOnClickListener {

            lat = midLatLng.latitude.toString() + ""
            lng = midLatLng.longitude.toString() + ""

            uploadToFS()

        }
    }

    private fun uploadToFS() {

        loadingDialog.show()

        val uid = usersData.uid

        val userInfo = HashMap<String, Any>()
        userInfo[Constants.ADDRESS_LAT] = lat
        userInfo[Constants.ADDRESS_LNG] = lng

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

        users.addressLat = lat
        users.addressLng = lng
        sh.saveUsers(users)

        loadingDialog.dismiss()

        mMap.clear()
        val marker =
            MarkerOptions().position(LatLng(lat.toDouble(), lng.toDouble())).title("Your address")

        mMap.addMarker(marker)
        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(LatLng(lat.toDouble(), lng.toDouble()), 15f)
        mMap.animateCamera(cameraUpdate)
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

        val locationButton = (mapFragment.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp =  locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 100)

        if (usersData.addressLat != "" && usersData.addressLng != "") {

            addressLat = usersData.addressLat.toDouble()
            addressLng = usersData.addressLng.toDouble()

            mMap.clear()

            val marker =
                MarkerOptions().position(LatLng(addressLat, addressLng)).title("Your address")

            googleMap.addMarker(marker)
            val cameraUpdate =
                CameraUpdateFactory.newLatLngZoom(LatLng(addressLat, addressLng), 15f)
            mMap.animateCamera(cameraUpdate)


        } else {

            mMap.isMyLocationEnabled = true

        }
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

        if (usersData.addressLat == "" && usersData.addressLng == "") {

            val latLng = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            mMap.animateCamera(cameraUpdate)

        }

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