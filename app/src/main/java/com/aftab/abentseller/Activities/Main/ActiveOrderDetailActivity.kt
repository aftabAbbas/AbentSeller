package com.aftab.abentseller.Activities.Main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Adapters.Pager.ProductSliderPager
import com.aftab.abentseller.Model.Order
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.RecentChat
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityActiveOrderDetailBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.firebase.firestore.DocumentSnapshot
import com.smarteist.autoimageslider.SliderView
import java.util.*


@Suppress("deprecation")
class ActiveOrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveOrderDetailBinding
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var customerData: Users
    private lateinit var deliveryBoyData: Users
    private lateinit var recentChat: RecentChat
    private lateinit var order: Order
    private lateinit var products: Products
    private lateinit var productSliderPager: ProductSliderPager
    private var productImagesList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        setSlider()
        clickListeners()
    }


    @SuppressLint("SetTextI18n")
    private fun initUI() {

        loadingDialog = LoadingDialog(this, "Loading")
        Functions.setStatusBarTransparent(this)

        order = intent.getSerializableExtra(Constants.ORDER) as Order
        products = intent.getSerializableExtra(Constants.PRODUCTS) as Products


        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        binding.collapsingToolbarLayout.isTitleEnabled = false


        binding.tvProductName.text = products.title
        binding.tvDescription.text = products.des

        val price = "$" + products.price

        binding.tvPrice.text = price
        binding.tvPrice1.text = price

        binding.tvItemsCount.text = "(${order.count} items)"

        val finalPrice = products.price.toFloat() * order.count.toFloat()

        val finalPr: Float = finalPrice + 1F
        binding.tvFinalPrice.text = "$$finalPr"

        binding.tvColor.text = order.color
        binding.tvSize.text = order.size

        val orderDate = DateUtils.getDateTimeFromMilli(order.orderDate, DateFormats.dateFormat3)
        binding.tvOrderDate.text = orderDate

        binding.tvOrderCount.text = order.count

        val name = "${order.fName} ${order.lName}"

        binding.tvPersonName.text = name


        val parcelCode = order.id.substring(1, 7).toUpperCase(Locale.ROOT)

        binding.tvParcelCode.text = parcelCode

        binding.tvEmail.text = order.email

        getCustomerData()

        if (order.deliveryBoyUid == "") {

            Functions.setImage(this, "", binding.ivRiderDp)
            binding.tvRiderName.text = resources.getString(R.string.pending)

        }

    }

    private fun getCustomerData() {

        loadingDialog.show()

        FireRef.USERS_REF
            .whereEqualTo(Constants.UID, order.uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    customerData = documentSnapshot.toObject(Users::class.java)!!

                    val dp = customerData.dp

                    Functions.setImage(this, dp, binding.civUser)

                    if (order.deliveryBoyUid != "") {

                        getDeliveryBoyData()

                    } else {

                        loadingDialog.dismiss()

                    }

                } else {

                    loadingDialog.dismiss()

                    Toast.makeText(this, "FS Error " + it.exception?.message, Toast.LENGTH_SHORT)
                        .show()

                }

            }.addOnFailureListener {

                loadingDialog.dismiss()

                Toast.makeText(this, "FS Error " + it.message, Toast.LENGTH_SHORT)
                    .show()

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

                    val dp = deliveryBoyData.dp

                    val name = deliveryBoyData.fname + " " + deliveryBoyData.lname

                    binding.tvRiderName.text = name

                    Functions.setImage(this, dp, binding.ivRiderDp)

                    loadingDialog.dismiss()

                } else {

                    loadingDialog.dismiss()

                    Toast.makeText(this, "FS Error " + it.exception?.message, Toast.LENGTH_SHORT)
                        .show()

                }

            }.addOnFailureListener {

                loadingDialog.dismiss()

                Toast.makeText(this, "FS Error " + it.message, Toast.LENGTH_SHORT)
                    .show()

            }

    }

    private fun clickListeners() {

        binding.cvBack.setOnClickListener {

            onBackPressed()

        }

        binding.toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        binding.appbarLayout.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isVisible = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.toolbar.title = products.title
                    isVisible = true
                } else if (isVisible) {
                    binding.toolbar.title = ""
                    isVisible = false
                }
            }
        })

        binding.cvCustomerChat.setOnClickListener {

            recentChat = RecentChat()
            recentChat.userId = customerData.uid
            recentChat.users = customerData

            startActivity(
                Intent(this, UserChatActivity::class.java)
                    .putExtra(Constants.PRODUCTS, products)
                    .putExtra(Constants.KEY_REF_CHATS, recentChat)
            )

        }

        binding.cvDeliveryBoyChat.setOnClickListener {

            if (order.deliveryBoyUid != "") {
                recentChat = RecentChat()
                recentChat.userId = deliveryBoyData.uid
                recentChat.users = deliveryBoyData

                startActivity(
                    Intent(this, UserChatActivity::class.java)
                        .putExtra(Constants.PRODUCTS, products)
                        .putExtra(Constants.KEY_REF_CHATS, recentChat)
                )

            } else {

                Toast.makeText(
                    this,
                    resources.getString(R.string.delivery_boy_is),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

        binding.cvCallRider.setOnClickListener {

            if (order.deliveryBoyUid != "") {

                //call

                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:" + Uri.encode(deliveryBoyData.phone.trim()))
                callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(callIntent)

            } else {

                Toast.makeText(
                    this,
                    resources.getString(R.string.delivery_boy_is),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

    private fun setSlider() {

        productImagesList = products.imagesUrlList

        productSliderPager = ProductSliderPager(this)
        binding.orderSliderView.setSliderAdapter(productSliderPager)
        binding.orderSliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        binding.orderSliderView.scrollTimeInSec = 3
        binding.orderSliderView.isAutoCycle = true
        binding.orderSliderView.startAutoCycle()

        productSliderPager.setImages(productImagesList)
    }
}