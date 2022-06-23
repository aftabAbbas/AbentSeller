package com.aftab.abentseller.Adapters.Recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Activities.Main.ActiveOrderDetailActivity
import com.aftab.abentseller.Model.ProductOrder
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.DateFormats
import com.aftab.abentseller.Utils.DateUtils
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.databinding.ActiveOrderItemBinding
import java.util.*

@Suppress("deprecation")
class ActiveOrderAdapter(var context: Context, private var productList: ArrayList<ProductOrder>) :
    RecyclerView.Adapter<ActiveOrderAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.active_order_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.setData(context, productList[position])

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ActiveOrderItemBinding.bind(itemView)

        fun setData(context: Context, productOrder: ProductOrder) {

            val product = productOrder.products
            val order = productOrder.order

            val orderDate = DateUtils.getDateTimeFromMilli(order.orderDate, DateFormats.dateFormat1)

            binding.tvProductName.text = product.title

            binding.tvOrderDate.text = orderDate

            val parcelCode = order.id.substring(1, 7).toUpperCase(Locale.ROOT)

            binding.tvParcelCode.text = parcelCode

            binding.tvSize.text = order.size

            val price = "$${product.price.toDouble() * order.count.toDouble()}"

            binding.tvPrice.text = price

            Functions.setImage(context, product.imagesUrlList[0], binding.ivProduct)

            binding.cvItem.setOnClickListener {

                context.startActivity(
                    Intent(context, ActiveOrderDetailActivity::class.java)
                        .putExtra(Constants.ORDER, order)
                        .putExtra(Constants.PRODUCTS, product)
                )

            }
        }

    }

}