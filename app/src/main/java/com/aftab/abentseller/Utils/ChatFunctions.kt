package com.aftab.abentseller.Utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import java.io.File
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object ChatFunctions {
    fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String?
        extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

    fun timeCalculator(time: String?): String? {
        val _endDate: String? = DateUtils.getCurrentDate()
        @SuppressLint("SimpleDateFormat") val myFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z")
        var startDate: Date? = null
        try {
            startDate = myFormat.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        var endDate: Date? = null
        try {
            endDate = myFormat.parse(_endDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(endDate != null)
        assert(startDate != null)
        val difference = endDate!!.time - startDate!!.time
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
        if (days == 1) {
            return "Yesterday"
        } else if (days == 0) {
            return "Today"
        } else if (days > 1 && days < 365) {
            return DateUtils.changeDateFormat(time)
        }
        return DateUtils.changeDateFormat2(time)
    }

    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    fun getStringSizeLengthFile(size: String): String? {
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        return when {
            size < sizeMb.toString() -> df.format((size.toDouble() / sizeKb)) + " Kb"
            size < sizeGb.toString() -> df.format(
                (size.toDouble() / sizeMb)
            ) + " Mb"
            size < sizeTerra.toString() -> df.format((size.toDouble() / sizeGb)) + " Gb"
            else -> ""
        }
    }

    fun checkTimeDifference(time1: String?, time2: String?, format1: String?): Long {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat(format1)
        var d1: Date? = null
        var d2: Date? = null
        try {
            d1 = format.parse(time1)
            d2 = format.parse(time2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(d2 != null)
        assert(d1 != null)
        val diff = d2!!.time - d1!!.time
        val diffSeconds = diff / 1000
        val diffMinutes = diff / (60 * 1000)
        val diffHours = diff / (60 * 60 * 1000)
        return TimeUnit.MILLISECONDS.toSeconds(diff)
    }

    fun getPath(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (UriUtils.isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    Toast.makeText(
                        context,
                        "Could not get file path. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (UriUtils.isDownloadsDocument(uri)) {
                val fileName: String =
                    getFilePath(context, uri)!!
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                }
                var id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:".toRegex(), "")
                    val file = File(id)
                    if (file.exists()) return id
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                return UriUtils.getDataColumn(context, contentUri, null, null)
            } else if (UriUtils.isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                val contentUri: Uri
                contentUri = if ("image" == type) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Files.getContentUri("external")
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return UriUtils.getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return UriUtils.getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }

        return null
    }

    fun getFilePath(context: Context, uri: Uri?): String? {
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        context.contentResolver.query(
            uri!!, projection, null, null,
            null
        ).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val index =
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        }
        return null
    }

    fun getFileSize(context: Context, fileUri: Uri?): Long {
        @SuppressLint("Recycle") val returnCursor = context.contentResolver.query(
            fileUri!!, null, null, null, null
        )
        val sizeIndex = returnCursor!!.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        return returnCursor.getLong(sizeIndex)
    }

    fun getFileSize2(fileUri: Uri): Long {
        val f = File(fileUri.path)
        return f.length()
    }


}