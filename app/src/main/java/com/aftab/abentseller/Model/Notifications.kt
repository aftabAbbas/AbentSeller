package com.aftab.abentseller.Model

import java.io.Serializable

data class Notifications(

    var id: String = "",
    var title: String = "",
    var message: String = "",
    var date: String = "",
    var receiverId: String = "",
    var senderId: String = "",
    var read: Boolean = false,

    ) : Serializable