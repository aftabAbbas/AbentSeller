package com.aftab.abentseller.ChatVH

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.ChatFunctions.timeCalculator

class VHTime(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var tvTime: TextView = itemView.findViewById(R.id.tv_time)
    fun setData(position: Int, arrayList: ArrayList<Messages>) {
        tvTime.text = timeCalculator(arrayList[position].dateTime)
    }

}