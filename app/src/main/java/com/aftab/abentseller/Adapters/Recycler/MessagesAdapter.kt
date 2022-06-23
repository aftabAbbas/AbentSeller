package com.aftab.abentseller.Adapters.Recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.ChatVH.*
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.SharedPref
import java.util.*

class MessagesAdapter(
    var context: Context,
    var arrayList: ArrayList<Messages>,
    var userId: String,
    var receiverId: String,
   var productId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var sh: SharedPref = SharedPref(context)
    override fun getItemViewType(position: Int): Int {
        val msgFrom = arrayList[position].from
        return if (msgFrom != null) {

            // Sender all types of messages layout will be inflated here
            if (msgFrom == userId) {
                if (arrayList[position].relatedTo!!.isEmpty()) {
                    when (Objects.requireNonNull(arrayList[position].type)) {
                        Constants.PNG, Constants.JPG -> Constants.LAYOUT_SEND_IMG
                        Constants.LOCATION -> Constants.LAYOUT_SEND_LOCATION
                        Constants.DOCX, Constants.PDF, Constants.ZIP, Constants.XML, Constants.JAVA, Constants.APK -> Constants.LAYOUT_SEND_FILE
                        Constants.MP4 -> Constants.LAYOUT_SENDER_VIDEO
                        else -> Constants.LAYOUT_SEND_MSG
                    }
                } else {
                    Constants.LAYOUT_SEND_RELATED_TO
                }
            } else {

                // Receiver all types of messages layout will be inflated here
                if (arrayList[position].relatedTo!!.isEmpty()) {
                    when (Objects.requireNonNull(arrayList[position].type)) {
                        Constants.PNG, Constants.JPG -> Constants.LAYOUT_RECEIVE_IMG
                        Constants.LOCATION -> Constants.LAYOUT_RECEIVE_LOCATION
                        Constants.DOCX, Constants.PDF, Constants.ZIP, Constants.XML, Constants.JAVA, Constants.APK -> Constants.LAYOUT_RECEIVE_FILE
                        Constants.MP4 -> Constants.LAYOUT_RECEIVE_VIDEO
                        else -> Constants.LAYOUT_RECEIVE_MSG
                    }
                } else {
                    Constants.LAYOUT_RECEIVE_RELATED_TO
                }
            }
        } else {
            Constants.LAYOUT_SEND_MSG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val view: View
        val viewHolder: RecyclerView.ViewHolder
        when (viewType) {
            Constants.LAYOUT_SEND_MSG -> {
                view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_send_msg_layout, parent, false)
                viewHolder = VHMsgSender(view)
            }
            Constants.LAYOUT_RECEIVE_MSG -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.item_receive_msg_layout, parent, false)
                viewHolder = VHMsgReceiver(view)
            }
            Constants.LAYOUT_SEND_IMG -> {
                view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_send_img_layout, parent, false)
                viewHolder = VHImgSender(view)
            }
            Constants.LAYOUT_RECEIVE_IMG -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.item_receive_img_layout, parent, false)
                viewHolder = VHImgReceiver(view)
            }
            Constants.LAYOUT_SEND_LOCATION -> {
                view =
                    LayoutInflater.from(context).inflate(R.layout.item_send_location, parent, false)
                viewHolder = VHLocSender(view)
            }
            Constants.LAYOUT_RECEIVE_LOCATION -> {
                view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_receive_location, parent, false)
                viewHolder = VHLocReceiver(view)
            }
            Constants.LAYOUT_SEND_FILE -> {
                view = LayoutInflater.from(context).inflate(R.layout.item_send_file, parent, false)
                viewHolder = VHFileSender(view)
            }
            Constants.LAYOUT_RECEIVE_FILE -> {
                view = LayoutInflater.from(context).inflate(R.layout.item_rec_file, parent, false)
                viewHolder = VHFileReceiver(view)
            }
            Constants.LAYOUT_SENDER_VIDEO -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.item_send_video_layout, parent, false)
                viewHolder = VHVideoSender(view)
            }
            Constants.LAYOUT_RECEIVE_VIDEO -> {
                view = LayoutInflater.from(context)
                        .inflate(R.layout.item_rec_video_layout, parent, false)
                viewHolder = VHVideoReceiver(view)
            }
            Constants.LAYOUT_SEND_RELATED_TO -> {
                view = LayoutInflater.from(context)
                        .inflate(R.layout.item_send_related_to, parent, false)
                viewHolder = VHSendRelatedTo(view)
            }
            Constants.LAYOUT_RECEIVE_RELATED_TO -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.item_receive_related_to, parent, false)
                viewHolder = VHReceiveRelatedTo(view)
            }
            else -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_time, parent, false)
                viewHolder = VHTime(view)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Constants.LAYOUT_SEND_MSG -> {
                (holder as VHMsgSender).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_RECEIVE_MSG -> {
                (holder as VHMsgReceiver).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_SEND_IMG -> {
                (holder as VHImgSender).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_RECEIVE_IMG -> {
                (holder as VHImgReceiver).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_SEND_LOCATION -> {
                (holder as VHLocSender).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_RECEIVE_LOCATION -> {
                (holder as VHLocReceiver).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_SEND_FILE -> {
                (holder as VHFileSender).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_RECEIVE_FILE -> {
                (holder as VHFileReceiver).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_SENDER_VIDEO -> {
                (holder as VHVideoSender).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_RECEIVE_VIDEO -> {
                (holder as VHVideoReceiver).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_SEND_RELATED_TO -> {
                (holder as VHSendRelatedTo).setData(context, position, arrayList, receiverId, productId)
            }
            Constants.LAYOUT_RECEIVE_RELATED_TO -> {
                (holder as VHReceiveRelatedTo).setData(context, position, arrayList, receiverId, productId)
            }
            else -> {
                (holder as VHTime).setData(position, arrayList)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}