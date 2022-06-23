package com.aftab.abentseller.Activities.Main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Adapters.Pager.ProductSliderPager
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityProductDetailBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.smarteist.autoimageslider.SliderView
import java.util.*


@Suppress("deprecation")
class ProductDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productsIntent: Products
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users
    private lateinit var sellerData: Users

    private var by: String = ""

    private var productImagesList = ArrayList<String>()
    private lateinit var productSliderPager: ProductSliderPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }


    private fun initUI() {

        sh = SharedPref(this)
        usersData = sh.getUsers()!!


        loadingDialog = LoadingDialog(this, "Loading")
        Functions.setStatusBarTransparent(this)
        productsIntent = intent.getSerializableExtra(Constants.PRODUCTS) as Products
        //  count = intent.getIntExtra(Constants.ORDER, 1)

        setUI()

    }

    private fun setUI() {

        loadingDialog.show()

        setSlider()

        binding.tvProductName.text = productsIntent.title

        val price = "$" + productsIntent.price

        binding.tvPrice.text = price

        binding.tvDescription.text = productsIntent.des

        getDataFromFS(productsIntent.from)


    }

    private fun clickListeners() {

        binding.cvBack.setOnClickListener {

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
                    binding.toolbar.title = productsIntent.title
                    isVisible = true
                } else if (isVisible) {
                    binding.toolbar.title = ""
                    isVisible = false
                }
            }
        })

    }

    private fun setSlider() {

        productImagesList = productsIntent.imagesUrlList

        productSliderPager = ProductSliderPager(this)
        binding.sliderView.setSliderAdapter(productSliderPager)
        binding.sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        binding.sliderView.scrollTimeInSec = 3
        binding.sliderView.isAutoCycle = true
        binding.sliderView.startAutoCycle()

        productSliderPager.setImages(productImagesList)
    }

    private fun getDataFromFS(from: String) {

        FireRef.USERS_REF
            .whereEqualTo(Constants.UID, from)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    sellerData = documentSnapshot.toObject(Users::class.java)!!

                    var fName = sellerData.fName
                    var lName = sellerData.lName
                    fName = fName.substring(0, 1).toUpperCase(Locale.ROOT) + fName.substring(1)
                        .toLowerCase(
                            Locale.ROOT
                        )
                    lName = lName.substring(0, 1).toUpperCase(Locale.ROOT) + lName.substring(1)
                        .toLowerCase(
                            Locale.ROOT
                        )
                    by = "By@$fName$lName"

                    binding.tvBy.text = by

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

}