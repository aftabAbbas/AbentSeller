package com.aftab.abentseller.Activities.Main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Adapters.Recycler.ProductCategoryAdapter
import com.aftab.abentseller.Model.ProductCategories
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.databinding.ActivitySelectProductCategoryBinding


class SelectProductCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectProductCategoryBinding
    private var categoriesList = ArrayList<ProductCategories>()
    private lateinit var productCategoryAdapter: ProductCategoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectProductCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListeners()
        getProductCategories()
        setAdapter()
    }


    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.srlCategories.setOnRefreshListener(this::getProductCategories)

    }


    private fun getProductCategories() {

        binding.srlCategories.isRefreshing = true

        FireRef.PRODUCT_CATEGORIES_REF
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    categoriesList.clear()

                    for (document in it.result) {

                        val productCategories: ProductCategories =
                            document.toObject(ProductCategories::class.java)
                        categoriesList.add(productCategories)
                    }

                    binding.srlCategories.isRefreshing = false
                    setAdapter()

                }


            }.addOnFailureListener {

                binding.srlCategories.isRefreshing = false
                Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()

            }


    }

    private fun setAdapter() {

        productCategoryAdapter = ProductCategoryAdapter(this, categoriesList)
        binding.rvCategories.adapter = productCategoryAdapter

    }
}