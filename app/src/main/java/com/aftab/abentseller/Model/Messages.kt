package com.aftab.abentseller.Model

import java.io.Serializable

data class Messages(


    var dateTime: String? = "",
    var from: String? = "",
    var msg: String? = "",
    var msgKey: String? = "",
    var type: String? = "",
    var url: String? = "",
    var fileSize: String? = "",
    var isMsgRead: String? = "",
    var receiverId: String? = "",
    var filePath: String? = "",
    var relatedTo: String? = "",
    var productId: String? = ""


):Serializable