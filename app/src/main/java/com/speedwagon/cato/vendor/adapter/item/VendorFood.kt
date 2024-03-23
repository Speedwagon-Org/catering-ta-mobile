package com.speedwagon.cato.vendor.adapter.item

import com.google.firebase.storage.StorageReference

data class VendorFood(
    var foodId : String,
    var foodPrice : Long,
    var foodName : String,
    var foodQty : Int = 0,
    var foodImgUrl : StorageReference,
    var foodDiscount : Double
)
