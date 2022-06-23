package com.aftab.abentseller.Model

import java.io.Serializable

class ProductCategories: Serializable {

    var id:String =""
    var name:String=""
    var url:String=""

    constructor()
    constructor(id: String, name: String, url: String) {
        this.id = id
        this.name = name
        this.url = url
    }

}