package com.aftab.abentseller.Adapters.Recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Activities.Main.UserChatActivity
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.RecentChat
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.databinding.ProductItemBinding

@Suppress("deprecation")
class ProductsAdapter(
    var context: Context,
    private var productsList: ArrayList<Products>,
    private var recentChat: RecentChat
) :
    RecyclerView.Adapter<ProductsAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.setData(context, productsList[position], recentChat)


    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ProductItemBinding.bind(itemView)

        fun setData(context: Context, products: Products, recentChat: RecentChat) {

            binding.tvProductName.text = products.title

            val imagesList = products.imagesUrlList

            Functions.setImage(context, imagesList[0], binding.ivProduct)

            val price = "$${products.price}"

            binding.tvPrice.text = price


            binding.clProduct
                .setOnClickListener {
                    context.startActivity(
                        Intent(context, UserChatActivity::class.java)
                            .putExtra(Constants.PRODUCTS, products)
                            .putExtra(Constants.KEY_REF_CHATS, recentChat)
                    )
                }
        }

    }

}