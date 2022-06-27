package com.aftab.abentseller.Fragments.Home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aftab.abentseller.Adapters.Recycler.ActiveOrderAdapter
import com.aftab.abentseller.Model.Order
import com.aftab.abentseller.Model.ProductOrder
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.FragmentActiveOrderBinding
import com.google.firebase.firestore.DocumentSnapshot


class ActiveOrderFragment : Fragment() {

    private lateinit var binding: FragmentActiveOrderBinding
    private lateinit var activeOrderAdapter: ActiveOrderAdapter
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
        binding = FragmentActiveOrderBinding.inflate(layoutInflater, container, false)
        initUI()
        getActiveOrders()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initUI() {

        sh = SharedPref(requireContext())
        usersData = sh.getUsers()!!

        binding.srlActiveOrder.setOnRefreshListener(this::getActiveOrders)

    }

    private fun getActiveOrders() {

        binding.srlActiveOrder.isRefreshing = true

        FireRef.ORDER_REF
            .whereEqualTo(Constants.SELLER_UID, usersData.uid)
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    ordersList.clear()

                    for (document in it.result) {

                        val order: Order =
                            document.toObject(Order::class.java)

                        if (order.orderStatus != "3" && order.orderStatus != "4") {
                            ordersList.add(order)
                        }
                    }

                    ordersList.sortWith { o1: Order, o2: Order ->
                        o2.orderDate.compareTo(o1.orderDate)
                    }

                    productList.clear()
                    counter = 0
                    getProducts()

                }


            }
            .addOnFailureListener {

                binding.srlActiveOrder.isRefreshing = false
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
        binding.srlActiveOrder.isRefreshing = false

        activeOrderAdapter = ActiveOrderAdapter(this.mContext, productList)
        binding.rvActiveOrder.adapter = activeOrderAdapter

    }

}