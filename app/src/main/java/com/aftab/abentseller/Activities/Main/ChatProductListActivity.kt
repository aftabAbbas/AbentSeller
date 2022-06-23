package com.aftab.abentseller.Activities.Main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Adapters.Recycler.ProductsAdapter
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.RecentChat
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.LoadingDialog
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivityChatProductListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot

class ChatProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatProductListBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var recentChat: RecentChat
    private lateinit var productsAdapter: ProductsAdapter

    private var productIdsList = ArrayList<String>()
    private var productsList = ArrayList<Products>()

    private lateinit var sh: SharedPref
    private lateinit var usersData: Users
    var productCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
        getLastProducts()

    }


    private fun initUI() {

        sh = SharedPref(this)
        usersData = sh.getUsers()!!
        recentChat = intent.getSerializableExtra(Constants.PRODUCTS) as RecentChat
        loadingDialog = LoadingDialog(this, "Loading")

    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun getLastProducts() {

        loadingDialog.show()

        FireRef.CHATS
            .child(usersData.uid)
            .child(recentChat.userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    productIdsList.clear()

                    for (dataSnapshot in snapshot.children) {

                        val userId = dataSnapshot.key.toString()

                        productIdsList.add(userId)

                    }

                    productCount = 0
                    productsList.clear()
                    getProducts()

                }

                override fun onCancelled(error: DatabaseError) {

                    loadingDialog.dismiss()

                }
            })

    }

    private fun getProducts() {

        if (productCount < productIdsList.size) {

            FireRef.PRODUCTS_REF.whereEqualTo(Constants.ID, productIdsList[productCount])
                .get()
                .addOnCompleteListener {

                    if (it.isSuccessful) {

                        val documentSnapshot: DocumentSnapshot = it.result.documents[0]


                        val products = documentSnapshot.toObject(Products::class.java)

                        if (products != null) {
                            productsList.add(products)
                        }

                        productCount++
                        getProducts()

                    }


                }

        } else {
            loadingDialog.dismiss()

            productsAdapter = ProductsAdapter(this, productsList,recentChat)
            binding.rvProductList.adapter = productsAdapter

        }

    }

}