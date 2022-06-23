package com.aftab.abentseller.Activities.Main

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Model.Order
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Notifications.Helper.NotiHelper
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityScanQrcodeBinding
import com.bumptech.glide.Glide
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.firestore.DocumentSnapshot
import java.io.IOException
import java.util.*


@Suppress("deprecation")
class ScanQRCodeActivity : AppCompatActivity() {

    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val requestCameraPermission = 201
    var intentData = ""

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var binding: ActivityScanQrcodeBinding
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users
    private lateinit var order: Order
    private lateinit var product: Products
    private lateinit var deliveryBoyData: Users
    private lateinit var customerData: Users

    private var toneGen1: ToneGenerator? = null

    private lateinit var ivProduct: ImageView
    private lateinit var tvProductName: TextView
    private lateinit var tvParcelCode: TextView
    private lateinit var tvSize: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvOrderDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        loadingDialog = LoadingDialog(this, "Loading")

        ivProduct = findViewById(R.id.iv_product)
        tvProductName = findViewById(R.id.tv_product_name)
        tvParcelCode = findViewById(R.id.tv_parcel_code)
        tvSize = findViewById(R.id.tv_size)
        tvPrice = findViewById(R.id.tv_price)
        tvOrderDate = findViewById(R.id.tv_order_date)

        sh = SharedPref(this)
        usersData = sh.getUsers()!!

        toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        setViewsVisibility(0)

    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        binding.btnAssign.setOnClickListener {

            if (order.assigned == "0") {

                assignToDeliveryBoy()

            } else {

                Toast.makeText(
                    this,
                    resources.getString(R.string.already_assigned),
                    Toast.LENGTH_SHORT
                ).show()

            }
        }


    }

    private fun assignToDeliveryBoy() {

        loadingDialog.show()

        val orderInfo = HashMap<String, Any>()
        orderInfo[Constants.ASSIGNED] = "1"
        orderInfo[Constants.ORDER_STATUS] = "2"

        FireRef.ORDER_REF
            .document(order.id)
            .update(orderInfo)
            .addOnCompleteListener {


                if (it.isSuccessful) {


                    getCustomerData()


                } else {

                    loadingDialog.dismiss()

                    Toast.makeText(
                        this,
                        "" + it.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }


    }

    private fun getCustomerData() {


        FireRef.USERS_REF
            .whereEqualTo(Constants.UID, order.uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    customerData = documentSnapshot.toObject(Users::class.java)!!

                    val msg =
                        resources.getString(R.string.your_parcel_has) + " " + (deliveryBoyData.fName + " " + deliveryBoyData.fName)

                    NotiHelper.sendNotification(
                        this,
                        customerData.fcm,
                        resources.getString(R.string.order_picked),
                        msg,
                        Constants.REMOTE_MSG_ORDER,
                        customerData.uid,
                        usersData.uid
                    )

                    Toast.makeText(
                        this,
                        "" + resources.getString(R.string.assigned_successfully),
                        Toast.LENGTH_SHORT
                    ).show()

                    onBackPressed()

                } else {

                    setViewsVisibility(0)

                    loadingDialog.dismiss()
                    Toast.makeText(this, "FS Error " + it.exception?.message, Toast.LENGTH_SHORT)
                        .show()

                }

            }


    }


    private fun initialiseDetectorsAndSources() {
        Toast.makeText(applicationContext, "Barcode scanner started", Toast.LENGTH_SHORT).show()

        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()


        cameraSource = CameraSource.Builder(this, barcodeDetector!!)
            .setRequestedPreviewSize(300, 300)
            .setAutoFocusEnabled(true)
            .build()


        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (checkSelfPermission(
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource?.start(binding.surfaceView.holder)
                    } else {
                        requestPermissions(
                            arrayOf(Manifest.permission.CAMERA),
                            requestCameraPermission
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })

        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    binding.textView8.post {

                        val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP)

                        Functions.vibrate(this@ScanQRCodeActivity, 500)

                        intentData = barcodes.valueAt(0)!!.displayValue

                        cameraSource!!.release()
                        barcodeDetector!!.release()

                        getOrderDetail()

                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Toast.makeText(applicationContext, "onRequestPermissionsResult", Toast.LENGTH_SHORT).show()

        if (requestCode == requestCameraPermission) {

            initialiseDetectorsAndSources()

        }

    }


    private fun getOrderDetail() {

        loadingDialog.show()


        FireRef.ORDER_REF
            .whereEqualTo(Constants.ID, intentData)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {


                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    order = documentSnapshot.toObject(Order::class.java)!!

                    getProduct()

                } else {

                    setViewsVisibility(0)

                    loadingDialog.dismiss()
                    Toast.makeText(
                        this,
                        resources.getString(R.string.no_data_found),
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }

    }

    private fun getProduct() {

        FireRef.PRODUCTS_REF
            .whereEqualTo(Constants.ID, order.productId)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]


                    product = documentSnapshot.toObject(Products::class.java)!!


                    getDeliveryBoyData()

                } else {

                    setViewsVisibility(0)

                    loadingDialog.dismiss()
                    Toast.makeText(this, "" + it.exception?.message, Toast.LENGTH_SHORT).show()

                }


            }

    }

    private fun getDeliveryBoyData() {

        FireRef.USERS_REF
            .whereEqualTo(Constants.UID, order.deliveryBoyUid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    deliveryBoyData = documentSnapshot.toObject(Users::class.java)!!

                    setUI()

                } else {

                    setViewsVisibility(0)

                    loadingDialog.dismiss()
                    Toast.makeText(this, "FS Error " + it.exception?.message, Toast.LENGTH_SHORT)
                        .show()

                }

            }

    }

    private fun setUI() {

        loadingDialog.dismiss()

        setViewsVisibility(1)

        val name = deliveryBoyData.fName + " " + deliveryBoyData.lName
        binding.tvRiderName.text = name

        Glide.with(this)
            .load(deliveryBoyData.dp)
            .placeholder(R.drawable.place_holder)
            .into(binding.ivRider)

        val parcelCode = order.id.substring(1, 7).toUpperCase(Locale.ROOT)
        val price = "$${product.price.toDouble() * order.count.toDouble()}"


        Glide.with(this)
            .load(product.imagesUrlList[0])
            .placeholder(R.drawable.place_holder_img)
            .into(ivProduct)

        tvProductName.text = product.title
        tvSize.text = order.size
        tvParcelCode.text = parcelCode
        tvPrice.text = price


        if (order.assigned == "0") {

            binding.btnAssign.setText(R.string.assign)

        } else {

            binding.btnAssign.setText(R.string.already_assigned)

        }


    }

    private fun setViewsVisibility(vis: Int) {

        if (vis == 1) {

            binding.tvResults.visibility = View.VISIBLE
            binding.clOrder.visibility = View.VISIBLE
            binding.llRider.visibility = View.VISIBLE
            binding.btnAssign.visibility = View.VISIBLE

        } else {

            binding.tvResults.visibility = View.GONE
            binding.clOrder.visibility = View.GONE
            binding.llRider.visibility = View.GONE
            binding.btnAssign.visibility = View.GONE

        }

    }


    override fun onPause() {
        super.onPause()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }

}