package com.speedwagon.cato.order.adapter.item

import com.google.firebase.storage.StorageReference

data class OrderedFood(
    val foodId : String,
    val foodName: String,
    val foodPrice: Long,
    val foodPictUrl: StorageReference,
    val foodQty: Int,
)
