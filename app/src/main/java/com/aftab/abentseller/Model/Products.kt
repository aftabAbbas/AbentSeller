package com.aftab.abentseller.Model

import java.io.Serializable

class Products : Serializable {

    var id = ""
    var catId = ""
    var imagesUrlList = ArrayList<String>()
    var availableColorsList = ArrayList<String>()
    var availableSizeList = ArrayList<String>()
    var videoUrl = ""
    var title = ""
    var des = ""
    var price = ""
    var quantity = ""
    var sizeFrom = ""
    var sizeTo = ""
    var from = ""
    var availableSize = true

    constructor()
    constructor(
        id: String,
        catId: String,
        imagesUrlList: ArrayList<String>,
        availableColorsList: ArrayList<String>,
        availableSizeList: ArrayList<String>,
        videoUrl: String,
        title: String,
        des: String,
        price: String,
        quantity: String,
        sizeFrom: String,
        sizeTo: String,
        from: String,
        availableSize: Boolean
    ) {
        this.id = id
        this.catId = catId
        this.imagesUrlList = imagesUrlList
        this.availableColorsList = availableColorsList
        this.availableSizeList = availableSizeList
        this.videoUrl = videoUrl
        this.title = title
        this.des = des
        this.price = price
        this.quantity = quantity
        this.sizeFrom = sizeFrom
        this.sizeTo = sizeTo
        this.from = from
        this.availableSize = availableSize
    }


}