package com.aftab.abentseller.Model

import java.io.Serializable


data class Users(

    var uid: String = "",
    var fName: String = "",
    var lName: String = "",
    var email: String = "",
    var phone: String = "",
    var dp: String = "",
    var password: String = "",
    var gender: String = "",
    var dtb: String = "",
    var about: String = "",
    var city: String = "",
    var country: String = "",
    var addressLng: String = "",
    var addressLat: String = "",
    var fcm: String = "",
    var planDate: String = "",
    var planType: String = "",
    var userType: String = "",
    var userStatus: String = "",
    var loginType: String = "",
    var paymentMethod: String = "",
    var language: String = "",
    var onlineStatus: String = "",

    ) : Serializable