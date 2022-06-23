package com.aftab.abentseller.Model

import java.io.Serializable

data class Order(

    var id: String = "",
    var uid: String = "",
    var sellerUid: String = "",
    var deliveryBoyUid: String = "",
    var productId: String = "",
    var count: String = "",
    var size: String = "",
    var color: String = "",
    var fName: String = "",
    var lName: String = "",
    var email: String = "",
    var phone: String = "",
    var addressLat: String = "",
    var addressLng: String = "",
    var paymentMethod: String = "",
    var orderStatus: String = "",
    var orderDate: String = "",
    var arrivalDate: String = "",
    var assigned: String = "",

    ) : Serializable
