package com.aftab.abentuser.Model

import java.io.Serializable

data class Help(

    var id: String = "",
    var addedOn: String = "",
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var des: String = "",

) : Serializable