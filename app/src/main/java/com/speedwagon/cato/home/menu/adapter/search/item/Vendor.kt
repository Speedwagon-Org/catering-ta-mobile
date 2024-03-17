package com.speedwagon.cato.home.menu.adapter.search.item

import com.google.firebase.storage.StorageReference

data class Vendor (
    val id : String,
    val imgUrl : StorageReference?,
    val name : String,
    val distance : Double,
)