package com.aftab.abentseller.Fragments.Main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aftab.abentseller.Adapters.Recycler.EarningsAdapter
import com.aftab.abentseller.Model.Order
import com.aftab.abentseller.Model.ProductOrder
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.FragmentEarningsBinding
import com.google.firebase.firestore.DocumentSnapshot

@SuppressLint("SetTextI18n")
class EarningsFragment : Fragment() {


    private lateinit var binding: FragmentEarningsBinding
    private lateinit var earningsAdapter: EarningsAdapter
    private lateinit var mContext: Context

    private lateinit var sh: SharedPref
    private lateinit var usersData: Users

    private var ordersList = ArrayList<Order>()
    private var productList = ArrayList<ProductOrder>()
    private var counter = 0
    private var totalEarning = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEarningsBinding.inflate(layoutInflater, container, false)
        initUI()
        getEarningData()



        return binding.root
    }

    private fun initUI() {

        sh = SharedPref(requireContext())
        usersData = sh.getUsers()!!

        binding.srlEarning.setOnRefreshListener(this::getEarningData)


    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    private fun getEarningData() {

        binding.srlEarning.isRefreshing = true


        FireRef.ORDER_REF
            .whereEqualTo(Constants.SELLER_UID, usersData.uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    totalEarning = 0.0
                    ordersList.clear()
                    productList.clear()

                    for (document in it.result) {

                        val order: Order =
                            document.toObject(Order::class.java)

                        if (order.orderStatus != "4") {
                            ordersList.add(order)
                        }
                    }

                    ordersList.sortWith { o1: Order, o2: Order ->
                        o2.orderDate.compareTo(o1.orderDate)
                    }

                    counter = 0
                    getProducts()

                } else {

                    binding.srlEarning.isRefreshing = false
                    Toast.makeText(requireContext(), "" + it.exception?.message, Toast.LENGTH_SHORT)
                        .show()

                }


            }
    }

    private fun getProducts() {

        if (counter < ordersList.size) {


            FireRef.PRODUCTS_REF
                .whereEqualTo(Constants.ID, ordersList[counter].productId)
                .get()
                .addOnCompleteListener {

                    if (it.isSuccessful) {

                        val documentSnapshot: DocumentSnapshot = it.result.documents[0]


                        val products = documentSnapshot.toObject(Products::class.java)

                        if (products != null) {

                            val productOrder = ProductOrder()
                            productOrder.order = ordersList[counter]
                            productOrder.products = products
                            productList.add(productOrder)

                            val price =
                                products.price.toDouble() * ordersList[counter].count.toDouble()

                            totalEarning += price

                        }

                        counter++
                        getProducts()

                    } else {

                        binding.srlEarning.isRefreshing = false
                        Toast.makeText(
                            requireContext(),
                            "" + it.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                }

        } else {

            setAdapter()

        }


    }


    private fun setAdapter() {

        binding.srlEarning.isRefreshing = false


        binding.tvTotalEarning.text = "$ $totalEarning"

        earningsAdapter = EarningsAdapter(mContext, productList, this@EarningsFragment)
        binding.rvEarning.adapter = earningsAdapter

    }

    fun getBuyerData(tvUserName: TextView, from: String) {

        FireRef.USERS_REF
            .whereEqualTo(Constants.UID, from)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful && it.result != null && it.result
                        .documents.size > 0
                ) {

                    val documentSnapshot: DocumentSnapshot = it.result.documents[0]

                    val users = documentSnapshot.toObject(Users::class.java)

                    val name = users?.fname + " " + users?.lname

                    tvUserName.text = name

                } else {

                    Toast.makeText(
                        mContext,
                        "FS Error " + it.exception?.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }

            }.addOnFailureListener {


                Toast.makeText(mContext, "FS Error " + it.message, Toast.LENGTH_SHORT)
                    .show()

            }


    }
}