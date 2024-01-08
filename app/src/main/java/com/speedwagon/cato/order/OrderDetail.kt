package com.speedwagon.cato.order

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speedwagon.cato.R
import com.speedwagon.cato.order.adapter.OrderFoodAdapter
import com.speedwagon.cato.order.adapter.item.OrderedFood

class OrderDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        recyclerViewInit()
        spinnerInit()
    }
    private fun recyclerViewInit(){
        val rvOrderFood = findViewById<RecyclerView>(R.id.rv_order_detail_foods)
        rvOrderFood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val dummyDataOrderFood = listOf(
            OrderedFood(
                foodName="Food 1",
                foodPrice=24000,
                foodPictUrl="https://asset.kompas.com/crops/AWXtnkYHOrbSxSggVuTs3EzQprM=/10x36:890x623/750x500/data/photo/2023/03/25/641e5ef63dea4.jpg",
                foodQty = 2
            ),
            OrderedFood(
                foodName="Food 2",
                foodPrice=38000,
                foodPictUrl="https://asset.kompas.com/crops/AWXtnkYHOrbSxSggVuTs3EzQprM=/10x36:890x623/750x500/data/photo/2023/03/25/641e5ef63dea4.jpg",
                foodQty = 5
            )
        )
        rvOrderFood.adapter = OrderFoodAdapter(this, dummyDataOrderFood)
    }
    private fun spinnerInit(){
        val paymentMethod = findViewById<Spinner>(R.id.sp_order_detail_payment)
        val orderType = findViewById<Spinner>(R.id.sp_order_detail_type)

        val paymentMethodAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.payment_method)
        )
        paymentMethodAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        val orderTypeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.order_type)
        )
        orderTypeAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        paymentMethod.adapter = paymentMethodAdapter
        orderType.adapter = orderTypeAdapter
    }
}