package com.aftab.abentseller.Fragments.Home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aftab.abentseller.Adapters.Recycler.DeliveredOrderAdapter
import com.aftab.abentseller.Model.Order
import com.aftab.abentseller.Model.ProductOrder
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.FragmentDeliveredOrderBinding
import com.google.firebase.firestore.DocumentSnapshot


class DeliveredOrderFragment : Fragment() {


    private lateinit var binding: FragmentDeliveredOrderBinding
    private lateinit var deliveredOrderAdapter: DeliveredOrderAdapter
    private lateinit var mContext: Context

    private lateinit var sh: SharedPref
    private lateinit var usersData: Users

    private var ordersList = ArrayList<Order>()
    private var productList = ArrayList<ProductOrder>()
    private var counter = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeliveredOrderBinding.inflate(layoutInflater, container, false)
        initUI()
        getDeliveredOrders()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initUI() {

        sh = SharedPref(requireContext())
        usersData = sh.getUsers()!!

        binding.srlDeliveredOrder.setOnRefreshListener(this::getDeliveredOrders)

    }


    private fun getDeliveredOrders() {

        binding.srlDeliveredOrder.isRefreshing = true

        FireRef.ORDER_REF
            .whereEqualTo(Constants.SELLER_UID, usersData.uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    ordersList.clear()

                    for (document in it.result) {

                        val order: Order =
                            document.toObject(Order::class.java)

                        if (order.orderStatus == "3") {
                            ordersList.add(order)
                        }
                    }

                }


                getProducts()

            }
            .addOnFailureListener {

                binding.srlDeliveredOrder.isRefreshing = false
                Toast.makeText(requireContext(), "" + it.message, Toast.LENGTH_SHORT).show()

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

                        }

                        counter++
                        getProducts()

                    }


                }

        } else {

            setAdapter()

        }


    }


    private fun setAdapter() {

        if (productList.size > 0) {

            binding.llNoData.visibility = View.GONE

        } else {

            binding.llNoData.visibility = View.VISIBLE

        }

        binding.srlDeliveredOrder.isRefreshing = false

        deliveredOrderAdapter =
            DeliveredOrderAdapter(this.mContext, productList)
        binding.rvDeliveredOrder.adapter = deliveredOrderAdapter

    }

}