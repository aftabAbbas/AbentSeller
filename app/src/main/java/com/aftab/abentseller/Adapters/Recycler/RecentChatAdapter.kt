package com.aftab.abentseller.Adapters.Recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Activities.Main.ChatProductListActivity
import com.aftab.abentseller.Model.RecentChat
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.databinding.RecentChatItemBinding
import com.bumptech.glide.Glide
import java.util.*

class RecentChatAdapter(var context: Context, private var recentChatList: ArrayList<RecentChat>) :
    RecyclerView.Adapter<RecentChatAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.recent_chat_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.itemView.setOnClickListener {

            context.startActivity(
                Intent(context, ChatProductListActivity::class.java)
                    .putExtra(Constants.PRODUCTS, recentChatList[position])
            )

        }

        holder.setData(context, recentChatList[position])


    }

    override fun getItemCount(): Int {
        return recentChatList.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = RecentChatItemBinding.bind(itemView)

        fun setData(context: Context, recentChat: RecentChat) {

            val users = recentChat.users

            var fName = users.fName
            var lName = users.lName
            fName =
                fName.substring(0, 1).uppercase(Locale.ROOT) + fName.substring(1)
                    .lowercase(
                        Locale.ROOT
                    )
            lName =
                lName.substring(0, 1).uppercase(Locale.ROOT) + lName.substring(1)
                    .lowercase(
                        Locale.ROOT
                    )
            val name = "$fName $lName"

            binding.tvUserName.text = name

            Glide.with(context)
                .load(users.dp)
                .placeholder(R.drawable.place_holder)
                .into(binding.ivUser)

            binding.tvMessage.text = context.resources.getString(R.string.just_start)
        }

    }

}