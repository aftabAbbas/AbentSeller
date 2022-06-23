package com.aftab.abentseller.ChatVH

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.util.Pair
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Activities.Main.PlayVideoActivity
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.DateUtils.getTime
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.SharedPref
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class VHVideoSender(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var imageView: ImageView = itemView.findViewById(R.id.imageView)
    private var tvTime: TextView = itemView.findViewById(R.id.tv_time)
    private var cardView: CardView = itemView.findViewById(R.id.cardView)
    private var progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    var sp: SharedPref? = null
    var usersData: Users? = null

    @SuppressLint("CheckResult")
    fun setData(
        context: Context,
        position: Int,
        arrayList: ArrayList<Messages>,
        receiverId: String?,
        productId: String
    ) {
        sp = SharedPref(context)
        usersData = sp!!.getUsers()
        tvTime.text = getTime(arrayList[position].dateTime)
        progressBar.visibility = View.VISIBLE
        Glide.with(context).asBitmap().load(arrayList[position].url)
            .placeholder(R.drawable.place_holder_img).listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any,
                    target: Target<Bitmap?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            }).into(imageView)
        cardView.setOnClickListener {
            if (arrayList[position].url != null) {
                val intent = Intent(context, PlayVideoActivity::class.java)
                val pairs: Array<Pair<View, String>?> = arrayOfNulls(1)
                pairs[0] = Pair<View, String>(cardView, "image")
                val options =
                    ActivityOptions.makeSceneTransitionAnimation(context as Activity, *pairs)
                intent.putExtra(Constants.SEND_VIDEO_URL, arrayList[position].url)
                context.startActivity(intent, options.toBundle())
            }
        }
        cardView.setOnLongClickListener {
            val dialog = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
            dialog.setMessage(context.resources.getString(R.string.are_you_sure_you))
            dialog.setNegativeButton(context.resources.getString(R.string.no), null)
            dialog.setPositiveButton(context.resources.getString(R.string.yes)) { _: DialogInterface?, _: Int ->
                FireRef.CHATS
                    .child(usersData!!.uid)
                    .child(receiverId!!)
                    .child(productId)
                    .child(arrayList[position].msgKey!!).removeValue()
                arrayList.removeAt(position)
            }
            dialog.show()
            false
        }
    }

}