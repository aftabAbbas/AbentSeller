package com.aftab.abentseller.ChatVH

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.DateUtils.getTime
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.MySpannable
import com.aftab.abentseller.Utils.SharedPref

@Suppress("deprecation")
class VHMsgReceiver(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var tvRecMsg: TextView = itemView.findViewById(R.id.tv_msg_rec)
    private var tvRecMsgTime: TextView = itemView.findViewById(R.id.tv_rec_time)
    var linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
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
            tvRecMsg.text = arrayList[position].msg
            //makeTextViewResizable(tvRecMsg, 10, "... Read More", true);
        }
        if (arrayList[position].dateTime != null) tvRecMsgTime.text = getTime(
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

    companion object {
        fun makeTextViewResizable(
            tv: TextView,
            maxLine: Int,
            expandText: String,
            viewMore: Boolean
        ) {
            if (tv.tag == null) {
                tv.tag = tv.text
            }
            val vto = tv.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val obs = tv.viewTreeObserver
                    obs.removeGlobalOnLayoutListener(this)
                    if (maxLine == 0) {
                        val lineEndIndex = tv.layout.getLineEnd(0)
                        val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                            .toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(tv.text.toString()), tv, expandText, viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                        val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                        val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                            .toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(tv.text.toString()), tv, expandText, viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    } else {
                        val lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                        val text =
                            tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                        tv.text = text
                        tv.movementMethod = LinkMovementMethod.getInstance()
                        tv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(tv.text.toString()), tv, expandText, viewMore
                            ), TextView.BufferType.SPANNABLE
                        )
                    }
                }
            })
        }

        private fun addClickablePartTextViewResizable(
            strSpanned: Spanned, tv: TextView,
            spannableText: String, viewMore: Boolean
        ): SpannableStringBuilder {
            val str = strSpanned.toString()
            val ssb = SpannableStringBuilder(strSpanned)
            if (str.contains(spannableText)) {
                ssb.setSpan(object : MySpannable(false) {
                    override fun onClick(widget: View) {
                        if (viewMore) {
                            tv.layoutParams = tv.layoutParams
                            tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                            tv.invalidate()
                            makeTextViewResizable(tv, -1, "See Less", false)
                        } else {
                            tv.layoutParams = tv.layoutParams
                            tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                            tv.invalidate()
                            makeTextViewResizable(tv, 3, "... Read More", true)
                        }
                    }
                }, str.indexOf(spannableText), str.indexOf(spannableText) + spannableText.length, 0)
            }
            return ssb
        }
    }

}