package com.speedwagon.cato.home.menu.adapter.home.item

import com.google.firebase.storage.StorageReference

data class NearVendor(
    val vendorId : String,
    val vendorName: String,
    val vendorDistance: Double,
    val vendorImgUrl: StorageReference?
)
