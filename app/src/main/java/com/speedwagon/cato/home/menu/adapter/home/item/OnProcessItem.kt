package com.speedwagon.cato.home.menu.adapter.home.item

import com.google.firebase.storage.StorageReference

data class OnProcessItem (
    val orderId : String,
    val foodName : String,
    val vendorName : String,
    val foodStatus : String,
    val foodImgUrl : StorageReference?
)