package com.aftab.abentseller.Adapters.Pager

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.aftab.abentseller.Model.OrderBanners
import com.aftab.abentseller.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.smarteist.autoimageslider.SliderViewAdapter

class ProductSliderPager : SliderViewAdapter<ProductSliderPager.SliderAdapterVH> {
    private var context: Context? = null
    private var mSliderItems: List<String> = ArrayList()

    constructor() {}
    constructor(context: Context?) {
        this.context = context
    }

    constructor(context: Activity?, mSliderItems: ArrayList<String>) {
        this.context = context
        this.mSliderItems = mSliderItems
    }

    fun setImages(sliderItems: ArrayList<String>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: String = mSliderItems[position]


        Glide.with(context!!).load(sliderItem).addListener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                viewHolder.pbLoading.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                viewHolder.pbLoading.visibility = View.GONE
                return false
            }
        }).into(viewHolder.imageViewBackground)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    inner class SliderAdapterVH(itemView: View) :
        ViewHolder(itemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.imageView_slider)
        var pbLoading: ProgressBar = itemView.findViewById(R.id.pb_loading)

    }
}
