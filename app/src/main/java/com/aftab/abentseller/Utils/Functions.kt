package com.aftab.abentseller.Utils

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.os.Vibrator
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.aftab.abentseller.Model.Messages
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@Suppress("deprecation")
object Functions {

    fun hideSystemUI(context: Context) {
        val activity = context as Activity
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

    }

    fun composeEmail(context: Context, address: String, subject: String?) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$address")
        }
        context.startActivity(Intent.createChooser(emailIntent, ""))
    }

    fun pickVideoFromGallery(context: Context) {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        (context as Activity).startActivityForResult(
            Intent.createChooser(intent, "Select Video"),
            Constants.REQUEST_TAKE_GALLERY_VIDEO
        )
    }

    fun pickImageFromGallery(context: Context) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        (context as Activity).startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            Constants.PICK_FILE_REQUEST_CODE
        )
    }

    fun pickFile(context: Context) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        (context as Activity).startActivityForResult(intent, Constants.PICK_FILE_REQUEST_CODE)
    }

    fun fullScreenWithNav(context: Context) {
        val activity = context as Activity
        val window = activity.window
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }

    fun changeNavigationBarColor(context: Context, color: Int) {
        val activity = context as Activity
        activity.window.navigationBarColor = activity.resources.getColor(color)
    }

    fun checkStoragePermission(context: Context?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            val result1 =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    fun openFile(context: Context?, file: File?, position: Int, arrayList: ArrayList<Messages>) {
        val mimeType: String? =
            ChatFunctions.getMimeType(context!!, Uri.fromFile(file))
        when (mimeType) {
            Constants.PDF -> openPDF(
                context,
                arrayList[position].url
            )
            Constants.DOCX, Constants.XML, Constants.JAVA, Constants.ZIP -> try {
                openFile(context, file!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Constants.APK -> openAPK(context, file!!)
        }
    }

    fun openPDF(context: Context, url: String?) {
        val intent = Intent()
        intent.setDataAndType(Uri.parse(url), "application/pdf")
        context.startActivity(intent)
    }

    @Throws(IOException::class)
    fun openFile(context: Context, aFile: File) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        try {
            val myIntent = Intent(Intent.ACTION_VIEW)
            val file = File(aFile.absolutePath)
            val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
            val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            myIntent.setDataAndType(Uri.fromFile(file), mimetype)
            context.startActivity(myIntent)
        } catch (e: java.lang.Exception) {
            val data = e.message
            Toast.makeText(context, "No app found to open file: " + e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun openAPK(context: Context, filePath: File) {
        try {
//            String PATH = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath();
            val file = File(filePath.toString())
            val intent = Intent(Intent.ACTION_VIEW)
            if (Build.VERSION.SDK_INT >= 24) {
                val downloaded_apk = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    file
                )
                intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive")
                val resInfoList = context.packageManager.queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                for (resolveInfo in resInfoList) {
                    context.grantUriPermission(
                        context.applicationContext.packageName + ".provider",
                        downloaded_apk,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(intent)
            } else {
                intent.action = Intent.ACTION_VIEW
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun hideKeyboard(context: Context) {
        val activity = context as Activity
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getAddress(lat: Double, lng: Double, context: Context): String {

        var address = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses =
            geocoder.getFromLocation(lat, lng, 1)

        if (addresses.size > 0) {

            address = addresses[0].getAddressLine(0)
        }

        return address
    }

    fun setImage(context: Context, url: String, iv: ImageView) {

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(com.aftab.abentseller.R.drawable.progress_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform()


        if (url == "") {
            Glide.with(context).load(url)
                .apply(options)
                .placeholder(com.aftab.abentseller.R.drawable.place_holder)
                .into(iv)

        } else {
            Glide.with(context).load(url)
                .apply(options)
                .into(iv)

        }

    }


    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    fun vibrate(context: Context, milli: Long) {
        val vibrate = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrate.vibrate(milli)
    }

    fun setStatusBarTransparent(context: Context) {
        val window = (context as Activity).window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val decorView = window.decorView
        window.statusBarColor = Color.TRANSPARENT

        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }


    fun setLinearLayoutGravity(gravity1: Int, linearLayout: LinearLayout?) {

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1.0f
            gravity = gravity1
        }
        linearLayout?.layoutParams = params
    }

    fun setDialogGravity(dialog: Dialog) {
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.y = 190
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
    }

    fun setScaleOutAnimation(view: View) {
        val anim = ScaleAnimation(
            1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim.duration = 800
        view.startAnimation(anim)
    }

    fun setScaleInAnimation(view: View) {
        val anim = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim.duration = 800
        view.startAnimation(anim)
    }


    fun formatMilliSecond(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }

    fun isEmailValid(email: String): Boolean {
        var isValid = false
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val inputStr: CharSequence = email
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(inputStr)
        if (matcher.matches()) {
            isValid = true
        }
        return isValid
    }


    fun getCurrentDate(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH)
        return df.format(c)
    }

    private fun getMilliSeconds(time: String?): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH)
        var date: Date? = null
        try {
            date = sdf.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(date != null)
        val millis = date!!.time
        return millis.toString()
    }

    private fun shuffleText(input: String): String {
        val rand = Random()
        val length = 20
        var randomString = ""
        val text = CharArray(length)
        for (i in 0 until length) {
            text[i] = input[rand.nextInt(input.length)]
        }
        for (i in text.indices) {
            randomString += text[i]
        }
        return randomString
    }

    fun requestLocationTurnOn(context: Context) {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnFailureListener((context as Activity)) { e: Exception ->
            if (e is ResolvableApiException) {

                try {
                    e.startResolutionForResult(context, 51)
                } catch (e1: IntentSender.SendIntentException) {
                    e1.printStackTrace()
                }

            }
        }
    }

    fun getRandomRequestCode(): Int {
        val date: String = getCurrentDate()
        val milli: String = getMilliSeconds(date)
        val shuffledString: String = shuffleText(milli)
        val chars = shuffledString.toCharArray()
        var str = ""
        for (i in 0..3) {
            str = str + chars[i] + ""
        }
        return str.toInt()
    }

    fun hideKeyBoard(context: Context) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun showSnackBar(context: Context, text: String?) {
        val activity = context as Activity
        Snackbar.make(activity.findViewById(R.id.content), text!!, Snackbar.LENGTH_LONG)
            .setAction(context.getString(R.string.ok)) { view: View? -> }
            .setActionTextColor(context.getResources().getColor(R.color.holo_red_light))
            .show()
    }

    @JvmStatic
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else { // non-primary volumes e.g sd card
                    var filePath = "non"
                    //getExternalMediaDirs() added in API 21
                    val extenal = context.externalMediaDirs
                    for (f in extenal) {
                        filePath = f.absolutePath
                        if (filePath.contains(type)) {
                            val endIndex = filePath.indexOf("Android")
                            filePath = filePath.substring(0, endIndex) + split[1]
                        }
                    }
                    filePath
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: java.lang.Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


}