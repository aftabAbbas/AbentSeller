package com.aftab.abentseller.ChatVH

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.Utils.SharedPref

class VHReceiveRelatedTo(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var tvMsg: TextView = itemView.findViewById(R.id.tv_msg)
    private var tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
    private var ivImg: ImageView = itemView.findViewById(R.id.iv_img)
    private var linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
    var sh: SharedPref? = null
    var usersData: Users? = null
    fun setData(
        context: Context?,
        position: Int,
        arrayList: ArrayList<Messages>,
        receiverId: String?,
        productId: String
    ) {
        sh = SharedPref(context!!)
        usersData = sh!!.getUsers()
        tvMsg.text = arrayList[position].msg
        getData(context, position, arrayList)

        /*  linearLayout.setOnClickListener(v -> context.startActivity(new Intent(context, MarketPlaceActivity.class)
                .putExtra(Const.SEND_POSITION, arrayList.get(position).getPostPosition())));
*/
        linearLayout.setOnLongClickListener {
            val dialog = AlertDialog.Builder(
                context, R.style.AppCompatAlertDialogStyle
            )
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

    private fun getData(context: Context, position: Int, arrayList: ArrayList<Messages>) {
        FireRef.PRODUCTS_REF.document(arrayList[position].relatedTo!!).get().addOnCompleteListener {

            if (it.isSuccessful) {

                val products = it.result.toObject(Products::class.java)

                if (products != null) {
                    tvDesc.text = products.des
                    Functions.setImage(context, products.imagesUrlList[0], ivImg)
                }
            }

        }
    }

}