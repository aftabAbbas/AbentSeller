package com.aftab.abentseller.Adapters.Recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Activities.Main.AddNewProductActivity
import com.aftab.abentseller.Model.ProductCategories
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.databinding.ProductCategoryItemBinding
import com.bumptech.glide.Glide

class ProductCategoryAdapter(var context: Context, private var productList: ArrayList<ProductCategories>) :
    RecyclerView.Adapter<ProductCategoryAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.product_category_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.setData(context,productList[position])

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding=ProductCategoryItemBinding.bind(itemView)

        fun setData(context: Context, productCategories: ProductCategories){

            Glide.with(context).load(productCategories.url)
                .into(binding.ivProduct)
            binding.tvProductName.text=productCategories.name


            binding.clItem.setOnClickListener {

                context.startActivity(Intent(context, AddNewProductActivity::class.java)
                    .putExtra(Constants.PRODUCT_CATEGORIES,productCategories))

            }

        }


    }

}