package com.aftab.abentseller.ChatVH

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aftab.abentseller.Model.Messages
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.DateUtils.getTime
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.Functions.checkStoragePermission
import com.aftab.abentseller.Utils.Functions.openFile
import com.aftab.abentseller.Utils.SharedPref
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import java.io.*
import java.net.URL
import java.util.*

@Suppress("deprecation")
class VHFileSender(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var imageView: ImageView = itemView.findViewById(R.id.iv_my_doc)
    private var ivDownload: ImageView = itemView.findViewById(R.id.iv_download)
    private var tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
    private var tvTime: TextView = itemView.findViewById(R.id.tv_time)
    private var tvFileSize: TextView = itemView.findViewById(R.id.tv_file_size)
    private var tvType: TextView = itemView.findViewById(R.id.tv_type)
    private var cardView: CardView = itemView.findViewById(R.id.cardView)
    private var sp: SharedPref? = null
    private var ringProgressBar: RingProgressBar = itemView.findViewById(R.id.myProgressbar)
    private var clProgress: ConstraintLayout = itemView.findViewById(R.id.cl_progress)
    private var progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    private var mTask: DownloadFileFromURL? = null
    private lateinit var output: OutputStream
    private var usersData: Users? = null

    fun setData(
        context: Context?,
        position: Int,
        arrayList: ArrayList<Messages>,
        receiverId: String?,
        productId: String
    ) {
        sp = SharedPref(context!!)
        usersData = sp!!.getUsers()
        when (arrayList[position].type) {
            Constants.DOCX -> imageView.setBackgroundResource(R.drawable.ic_docx)
            Constants.PDF -> imageView.setBackgroundResource(R.drawable.ic_pdf)
            Constants.ZIP -> imageView.setBackgroundResource(R.drawable.zip)
            Constants.XML -> imageView.setBackgroundResource(R.drawable.xml)
            Constants.JAVA -> imageView.setBackgroundResource(R.drawable.java)
            else -> imageView.setBackgroundResource(R.drawable.apk_black)
        }
        tvFileName.text = arrayList[position].msg
        tvTime.text = getTime(arrayList[position].dateTime)
        tvFileSize.text = arrayList[position].fileSize!!.toUpperCase(Locale.ROOT)
        tvType.text = arrayList[position].type!!.toUpperCase(Locale.ROOT)
        cardView.setOnClickListener {
            val isPermissionAccepted = checkStoragePermission(context)
            val file = File(Constants.FILEPATH + arrayList[position].msg)
            if (file.exists()) {
                openFile(context, file, position, arrayList)
            } else {
                if (arrayList[position].filePath != null) {

                    val f = arrayList[position].filePath?.let { it1 -> File(it1) }
                    if (f!!.exists()) {
                        openFile(context, file, position, arrayList)
                    } else {
                        clProgress.visibility = View.VISIBLE
                        ringProgressBar.visibility = View.GONE
                        ivDownload.visibility = View.GONE
                        if (isPermissionAccepted) {
                            mTask = DownloadFileFromURL(
                                context,
                                arrayList[position].msg!!
                            ).execute(arrayList[position].url) as DownloadFileFromURL?

                        } else {
                            // Functions.requestPermission(context);
                        }
                    }
                } else {
                    clProgress.visibility = View.VISIBLE
                    ringProgressBar.visibility = View.GONE
                    ivDownload.visibility = View.GONE
                    if (isPermissionAccepted) {
                        mTask = DownloadFileFromURL(
                            context,
                            arrayList[position].msg!!
                        )
                            .execute(arrayList[position].url) as DownloadFileFromURL?
                    } else {
                        // Functions.requestPermission(context);
                    }
                }
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
        ivDownload.setOnClickListener {
            val isPermissionAccepted = checkStoragePermission(context)
            if (isPermissionAccepted) {
                clProgress.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                ivDownload.visibility = View.GONE
                ringProgressBar.visibility = View.GONE
                mTask = DownloadFileFromURL(
                    context,
                    arrayList[position].msg!!
                )
                    .execute(arrayList[position].url) as DownloadFileFromURL?
            } else {
                //Functions.requestPermission(context);
            }
        }
        clProgress.setOnClickListener {
            if (mTask != null) {
                mTask!!.cancel(true)
            }
            clProgress.visibility = View.GONE
            ivDownload.visibility = View.VISIBLE
            try {
                output.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val f = File(Constants.FILEPATH + arrayList[position].msg)
            if (f.exists()) {
                f.delete()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadFileFromURL(private var context: Context, private var msg: String) :
        AsyncTask<String?, String?, String?>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg f_url: String?): String? {
            var count: Int
            try {
                val url = URL(f_url[0])
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val sampleDir =
                    File(Environment.getExternalStorageDirectory(), "/" + Constants.DIRECTORY_NAME)
                if (!sampleDir.exists()) {
                    sampleDir.mkdirs()
                }
                val input: InputStream = BufferedInputStream(url.openStream(), 8192)
                output = FileOutputStream(
                    Environment.getExternalStoragePublicDirectory(Constants.DIRECTORY_NAME)
                        .absolutePath + "/" + msg + "/"
                )
                val data = ByteArray(1024)
                var total: Long = 0
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    publishProgress("" + (total * 100 / lengthOfFile).toInt())
                    output.write(data, 0, count)
                }
                output.flush()
                output.close()
                input.close()
            } catch (ignored: Exception) {
            }
            return null
        }

        @Deprecated("Deprecated in Java")
        override fun onProgressUpdate(vararg progress: String?) {
            Handler(Looper.getMainLooper()).post {
                if (progress[0]!!.toInt() == 0) {
                    ringProgressBar.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
                ringProgressBar.progress = progress[0]!!.toInt()
                if (progress[0]!!.toInt() == 100) {
                    Toast.makeText(context, context.resources.getString(R.string.download_completed), Toast.LENGTH_SHORT).show()
                    clProgress.visibility = View.GONE
                }
            }
        }
    }

}