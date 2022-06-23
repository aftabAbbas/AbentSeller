package com.aftab.abentseller.Model

import java.io.Serializable

data class RecentChat(

    var userId: String = "",
    var users: Users = Users(),
    var messages: Messages = Messages()


) : Serializable