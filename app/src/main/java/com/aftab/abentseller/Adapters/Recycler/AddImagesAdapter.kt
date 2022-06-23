package com.aftab.abentseller.Adapters.Recycler

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Listeners.AddImagesListener
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.databinding.ImagesItemBinding

class AddImagesAdapter(
    var context: Context,
    private var imagesList: ArrayList<Uri>,
    private var addImagesListener: AddImagesListener
) :
    RecyclerView.Adapter<AddImagesAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.images_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.setData(context, addImagesListener, imagesList[position])

    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ImagesItemBinding.bind(itemView)

        fun setData(context: Context, addImagesListener: AddImagesListener, uri: Uri) {

            var urio: Uri = Functions.getPath(context, uri)!!.toUri()

            binding.ivProduct.setImageURI(urio)

            binding.ivRemove.setOnClickListener {


                addImagesListener.removeImage(uri)

            }
        }


    }

}