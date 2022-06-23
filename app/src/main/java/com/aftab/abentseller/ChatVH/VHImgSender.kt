package com.aftab.abentseller.ChatVH

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Pair
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Activities.Main.SeeImageActivity
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

class VHImgSender(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var ivImgSender: ImageView = itemView.findViewById(R.id.iv_img_sender)
    private var tvTimeSender: TextView = itemView.findViewById(R.id.tv_time)
    var progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    var cardView: CardView = itemView.findViewById(R.id.cardView)
    var sp: SharedPref? = null
    var usersData: Users? = null
    fun setData(
        context: Context?,
        position: Int,
        arrayList: ArrayList<Messages>,
        receiverId: String?,
        productId: String
    ) {
        sp = SharedPref(context!!)
        usersData = sp!!.getUsers()
        if (arrayList[position].msg != null) {
            progressBar.visibility = View.VISIBLE
            Glide.with(context).load(arrayList[position].url).placeholder(R.drawable.place_holder_img)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                }).into(ivImgSender)
            tvTimeSender.text = getTime(arrayList[position].dateTime)
        }
        cardView.setOnClickListener {

            val intent = Intent(context, SeeImageActivity::class.java)
            val pairs: Array<Pair<View, String>?> = arrayOfNulls(1)
            pairs[0] = Pair<View, String>(cardView, "image")
            val options = ActivityOptions.makeSceneTransitionAnimation(context as Activity, *pairs)
            intent.putExtra(Constants.SEND_IMAGE, arrayList[position].url)
            context.startActivity(intent, options.toBundle())
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
                    .child(arrayList[position].msgKey!!)
                    .removeValue()
                arrayList.removeAt(position)
            }
            dialog.show()
            false
        }
    }

}