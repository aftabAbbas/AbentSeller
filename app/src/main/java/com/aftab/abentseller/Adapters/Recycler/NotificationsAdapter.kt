package com.aftab.abentseller.Adapters.Recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Model.Notifications
import com.aftab.abentseller.R
import com.aftab.abentseller.databinding.NotificationItemBinding

class NotificationsAdapter(var context: Context, var notificationsList: ArrayList<Notifications>) :
    RecyclerView.Adapter<NotificationsAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.setData(notificationsList[position])

    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = NotificationItemBinding.bind(itemView)

        fun setData(notifications: Notifications) {

            binding.tvTitle.text = notifications.title
            binding.tvMessage.text = notifications.message

            if (notifications.read) {

                binding.ivIsRead.setImageResource(R.drawable.ic_read)

            } else {

                binding.ivIsRead.setImageResource(R.drawable.ic_un_read)

            }


        }

    }

}