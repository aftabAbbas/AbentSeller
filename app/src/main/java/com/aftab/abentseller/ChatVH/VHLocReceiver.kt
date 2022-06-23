package com.aftab.abentseller.ChatVH

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.SharedPref
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class VHLocReceiver(itemView: View) : RecyclerView.ViewHolder(itemView), OnMapReadyCallback {
    var context: Context? = null
    var cardView: CardView = itemView.findViewById(R.id.cv_receive_location)
    private var mapView: MapView? = itemView.findViewById(R.id.mv_rec_loc)
    private var tvLocation: TextView = itemView.findViewById(R.id.tv_rec_location)
    private var googleMap: GoogleMap? = null
    var location: String? = null
    var sp: SharedPref? = null
    var usersData: Users? = null
    fun setData(
        context: Context?,
        position: Int,
        arrayList: ArrayList<Messages>,
        receiverId: String?,
        productId: String
    ) {
        this.context = context
        sp = SharedPref(context!!)
        usersData = sp!!.getUsers()
        location = arrayList[position].url
        tvLocation.text = arrayList[position].msg
        cardView.setOnLongClickListener {
            val dialog = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
            dialog.setMessage(context.resources.getString(R.string.are_you_sure_you))
            dialog.setNegativeButton(context.resources.getString(R.string.no), null)
            dialog.setPositiveButton(context.resources.getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                FireRef.CHATS
                    .child(usersData!!.uid)
                    .child(receiverId!!)
                    .child(productId)
                    .child(arrayList[position].msgKey!!)
                    .removeValue()
                arrayList.removeAt(position)
            }
            dialog.show()
            false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.setAllGesturesEnabled(false)
        googleMap.uiSettings.isScrollGesturesEnabled = false
        val lng = location!!.split(",".toRegex()).toTypedArray()[location!!.split(",".toRegex())
            .toTypedArray().size - 1]
        val lat = location!!.substring(0, location!!.length - lng.length).replace(",", "")
        googleMap.setOnMapClickListener {
            val uri = "http://maps.google.com/maps?q=loc:$lat,$lng"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setPackage("com.google.android.apps.maps")
            context!!.startActivity(intent)
        }
        mapView!!.setOnClickListener(null)
        val latLng = LatLng(lat.toDouble(), lng.toDouble())
        this.googleMap!!.addMarker(MarkerOptions().position(latLng))
        this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        this.googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7.0f))
    }

    init {
        if (mapView != null) {
            mapView!!.onCreate(null)
            mapView!!.onResume()
            mapView!!.getMapAsync(this)
        }
    }
}