package com.aftab.abentseller.Utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {


    fun getDateTimeFromMilli(time: String, dateFormat: String): String? {
        val milliSeconds = time.toLong()
        val formatter = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getCurrentDate(): String? {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z", Locale.US)
        df.timeZone = TimeZone.getDefault()
        return df.format(c)
    }

    fun getCurrentDate(format:String): String? {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat(format, Locale.US)
        df.timeZone = TimeZone.getDefault()
        return df.format(c)
    }

    fun getTime(date: String?): String? {
        val originalFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z", Locale.ENGLISH)
        @SuppressLint("SimpleDateFormat") val targetFormat: DateFormat = SimpleDateFormat("hh:mm a")
        var date1: Date? = null
        try {
            date1 = originalFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(date1 != null)
        return targetFormat.format(date1)
    }

    fun changeDateFormat(date: String?): String? {
        val originalFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z", Locale.ENGLISH)
        @SuppressLint("SimpleDateFormat") val targetFormat: DateFormat =
            SimpleDateFormat("EEEE dd yyyy")
        var date1: Date? = null
        try {
            date1 = originalFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(date1 != null)
        return targetFormat.format(date1)
    }

    fun changeDateFormat2(date: String?): String? {
        val originalFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z", Locale.ENGLISH)
        @SuppressLint("SimpleDateFormat") val targetFormat: DateFormat =
            SimpleDateFormat("MMM dd, yyyy")
        var date1: Date? = null
        try {
            date1 = originalFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(date1 != null)
        return targetFormat.format(date1)
    }

}