package com.speedwagon.cato.vendor.adapter.item

data class VendorFood(
    var foodId : String,
    var foodPrice : Int,
    var foodName : String,
    var foodQty : Int = 0,
    var foodImgUrl : String,
    var foodDiscount : Int
)
