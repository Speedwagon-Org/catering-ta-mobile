package com.speedwagon.cato.vendor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speedwagon.cato.R
import com.speedwagon.cato.vendor.adapter.DetailVendorFoodAdapter
import com.speedwagon.cato.vendor.adapter.item.VendorFood

class DetailVendor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_vendor)

        recyclerViewInit()
    }

    private fun recyclerViewInit(){
        val rvDetailVendorFood = findViewById<RecyclerView>(R.id.rv_detail_vendor_foods)
        rvDetailVendorFood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val dummyDetailVendorFood = listOf(
            VendorFood(
                foodId = "1",
                foodPrice = 12000,
                foodName = "Food 1",
                foodQty = 0,
                foodImgUrl = "https://asset.kompas.com/crops/AWXtnkYHOrbSxSggVuTs3EzQprM=/10x36:890x623/750x500/data/photo/2023/03/25/641e5ef63dea4.jpg",
                foodDiscount = 0,
            ),
            VendorFood(
                foodId = "2",
                foodPrice = 12000,
                foodName = "Food 2",
                foodQty = 1,
                foodImgUrl = "https://asset.kompas.com/crops/AWXtnkYHOrbSxSggVuTs3EzQprM=/10x36:890x623/750x500/data/photo/2023/03/25/641e5ef63dea4.jpg",
                foodDiscount = 10,
            )
        )
        rvDetailVendorFood.adapter = DetailVendorFoodAdapter(this, dummyDetailVendorFood)
    }
}