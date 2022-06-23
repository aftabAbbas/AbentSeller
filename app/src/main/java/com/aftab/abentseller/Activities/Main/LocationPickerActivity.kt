package com.aftab.abentseller.Activities.Main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Functions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.gun0912.tedpermission.PermissionListener
import java.io.IOException
import java.util.*

class LocationPickerActivity: FragmentActivity(), OnMapReadyCallback, LocationListener {
    var toolbar: Toolbar? = null
    var locationManager: LocationManager? = null
    var context: Context? = null
    var isGPSEnable = false
    var isNetworkEnable = false
    private var midLatLng: LatLng? = null
    var address: String? = null
    var lat: String? = null
    private var lon: String? = null
    var mMap: GoogleMap? = null
    private var tvSend: TextView? = null
    var searchView: SearchView? = null
    var addressList: List<Address>? = null
    private var mapFragment: SupportMapFragment? = null
    private var permissionListener: PermissionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)
        init()
    }

    private fun init() {
        context = this@LocationPickerActivity
        viewsInit()
        clickListener()
        mainInit()
    }

    private fun mainInit() {
        val mapFragment =
            (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
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
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            this
        )
    }

    private fun viewsInit() {
        toolbar = findViewById(R.id.toolbar)
        tvSend = findViewById(R.id.tv_send)
        searchView = findViewById(R.id.search_view)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
    }

    private fun clickListener() {
        toolbar!!.setNavigationOnClickListener { onBackPressed() }
        tvSend!!.setOnClickListener {
            val returnIntent = Intent()
            if (midLatLng != null) {
                lat = midLatLng!!.latitude.toString() + ""
                lon = midLatLng!!.longitude.toString() + ""
                returnIntent.putExtra("lat", lat)
                returnIntent.putExtra("lon", lon)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
        permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                locationManager = context!!.getSystemService(LOCATION_SERVICE) as LocationManager
                isGPSEnable = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnable =
                    locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (!isGPSEnable && !isNetworkEnable) {
                    Functions.requestLocationTurnOn(this@LocationPickerActivity)
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
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location = searchView!!.query.toString()
                val geocoder = Geocoder(context)
                try {
                    addressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList!!.isNotEmpty()) {
                    val address = addressList!![0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    // this is how to set marker on every searched location.
                    // mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                } else {
                    Functions.showSnackBar(this@LocationPickerActivity, "Location not found!")
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        mapFragment!!.getMapAsync(this)
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
        mMap!!.isMyLocationEnabled = true
        mMap!!.setOnCameraIdleListener {
            midLatLng = mMap!!.cameraPosition.target
            //            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(midLatLng, 15);
//            mMap.animateCamera(location);
            if (midLatLng!!.latitude != 0.0 && midLatLng!!.longitude != 0.0) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(
                            midLatLng!!.latitude, midLatLng!!.longitude, 1
                        )
                    address = addresses[0].getAddressLine(0)
                    tvSend!!.visibility = View.VISIBLE
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
        mMap!!.animateCamera(cameraUpdate)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    companion object {
        private const val MIN_TIME: Long = 400
        private const val MIN_DISTANCE = 1000f
    }
}