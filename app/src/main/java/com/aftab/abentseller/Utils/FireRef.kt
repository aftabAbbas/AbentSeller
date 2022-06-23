package com.aftab.abentseller.Utils

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@SuppressLint("StaticFieldLeak")
@Suppress("deprecation")
object FireRef {

    // main initialization
    var myRef = FirebaseDatabase.getInstance().reference
    var mAuth = FirebaseAuth.getInstance()
    var storageRef = FirebaseStorage.getInstance()
    private var fireDB = FirebaseFirestore.getInstance()

    var profileStorage = FirebaseStorage.getInstance().reference.child("Profile Pictures")
    var productPhotosStorage = FirebaseStorage.getInstance().reference.child("Product Pictures")
    var productVideosStorage = FirebaseStorage.getInstance().reference.child("Product Videos")

    var USERS_REF: CollectionReference = FirebaseFirestore.getInstance().collection(
        Constants.USERS.toLowerCase(
            Locale.ROOT
        )
    )

    var PRODUCT_CATEGORIES_REF: CollectionReference =
        FirebaseFirestore.getInstance().collection(Constants.PRODUCT_CATEGORIES)
    var PRODUCTS_REF: CollectionReference =
        FirebaseFirestore.getInstance().collection(Constants.PRODUCTS)
    var ORDER_REF: CollectionReference = fireDB.collection(Constants.ORDER)

    var CHATS = myRef.child(Constants.KEY_REF_CHATS)
    var NOTIFICATIONS_REF: CollectionReference = fireDB.collection(Constants.NOTIFICATIONS)
    var HELP_REF: CollectionReference = fireDB.collection(Constants.HELP)
    var ADMIN_REF: CollectionReference = fireDB.collection(Constants.ADMIN)
}