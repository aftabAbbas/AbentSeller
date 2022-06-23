package com.aftab.abentseller.Adapters.Recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Activities.Main.ActiveOrderDetailActivity
import com.aftab.abentseller.Activities.Main.ProductDetailActivity
import com.aftab.abentseller.Fragments.Main.EarningsFragment
import com.aftab.abentseller.Model.ProductOrder
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.DateFormats
import com.aftab.abentseller.Utils.DateUtils
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.databinding.EarningItemBinding

class EarningsAdapter(
    var context: Context,
    var productList: ArrayList<ProductOrder>,
    var earningsFragment: EarningsFragment
) :
    RecyclerView.Adapter<EarningsAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.earning_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.setData(context, productList[position], earningsFragment)

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = EarningItemBinding.bind(itemView)

        fun setData(
            context: Context,
            productOrder: ProductOrder,
            earningsFragment: EarningsFragment
        ) {

            val product = productOrder.products
            val order = productOrder.order

            val price = product.price.toDouble() * order.count.toDouble()

            Functions.setImage(context, product.imagesUrlList[0], binding.ivProduct)

            binding.tvProductName.text = product.title

            val orderDate = DateUtils.getDateTimeFromMilli(order.orderDate, DateFormats.dateFormat3)

            binding.tvOrderDate.text = orderDate

            binding.tvOrderCount.text = order.count

            earningsFragment.getBuyerData(binding.tvBuyerName, order.uid)

            binding.tvPrice.text = price.toString()

            binding.clOrder.setOnClickListener {

                context.startActivity(
                    Intent(context, ProductDetailActivity::class.java)
                        .putExtra(Constants.PRODUCTS, product)
                )

            }

        }

    }

}