package com.aftab.abentseller.Model

import java.io.Serializable

class ProductOrder : Serializable {

    var order: Order = Order()
    var products: Products = Products()


}