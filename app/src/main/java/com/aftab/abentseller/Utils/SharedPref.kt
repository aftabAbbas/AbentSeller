package com.aftab.abentseller.Utils

import android.content.Context
import android.content.SharedPreferences
import com.aftab.abentseller.Model.Users
import com.google.gson.Gson

class SharedPref(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun putBoolean(key: String?, value: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value!!)
        editor.apply()
    }

    fun getBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key: String?, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String?): String {
        return sharedPreferences.getString(key, "").toString()
    }

    fun saveUsers(user: Users) {
        val editor = sharedPreferences.edit()
        editor.putString("users", Gson().toJson(user))
        editor.apply()
    }

    fun getUsers(): Users? {
        val data = getString("users")

        return if (data.isEmpty())
            null
        else
            Gson().fromJson(data, Users::class.java)
    }

    fun clearPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}