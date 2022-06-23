package com.aftab.abentseller.Activities.Main

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Adapters.Pager.ProductSliderPager
import com.aftab.abentseller.Model.Order
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityCanceledOrderBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.smarteist.autoimageslider.SliderView

class CanceledOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCanceledOrderBinding
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var orderUserData: Users
    private lateinit var order: Order
    private lateinit var products: Products
    private lateinit var productSliderPager: ProductSliderPager
    private var productImagesList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanceledOrderBinding.inflate(layoutInflater)
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

        binding.tvEmail.text = order.email

        getUserData()

    }

    private fun getUserData() {

        loadingDialog.show()

        FireRef.USERS_REF
            .whereEqualTo(Constants.UID, order.uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    orderUserData = documentSnapshot.toObject(Users::class.java)!!

                    val dp = orderUserData.dp

                    Functions.setImage(this, dp, binding.civUser)

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

        binding.appbarLayout.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
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