package com.aftab.abentseller.ChatVH

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.DateUtils.getTime
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.SharedPref

class VHMsgSender(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var tvSendMsg: TextView = itemView.findViewById(R.id.tv_msg_send)
    private var tvSendMsgTime: TextView = itemView.findViewById(R.id.tv_send_time)
    private var linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
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
        if (arrayList[position].msg != null) tvSendMsg.text = arrayList[position].msg
        if (arrayList[position].dateTime != null) tvSendMsgTime.text = getTime(
            arrayList[position].dateTime
        )
        linearLayout.setOnLongClickListener {
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