package com.aftab.abentuser.Model

import java.io.Serializable

data class Admin(

    var uid: String = "",
    var email: String = "",
    var fName: String = "",
    var lName: String = "",
    var dp: String = "",
    var fcm: String = "",
    var password: String = "",

) : Serializable