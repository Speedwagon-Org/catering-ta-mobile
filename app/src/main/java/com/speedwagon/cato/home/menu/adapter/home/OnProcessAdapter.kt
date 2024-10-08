package com.speedwagon.cato.home.menu.adapter.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.home.item.OnProcessItem
import com.speedwagon.cato.order.OrderStatus

class OnProcessAdapter(private val context: Context, private val itemList: List<OnProcessItem>) :
    RecyclerView.Adapter<OnProcessAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewContainer: CardView = itemView.findViewById(R.id.cv_item_on_process_container)
        val foodImageView: ImageView = itemView.findViewById(R.id.iv_item_on_process_image)
        val foodNameTextView: TextView = itemView.findViewById(R.id.tv_item_on_process_food)
        val vendorNameTextView: TextView = itemView.findViewById(R.id.tv_item_on_process_vendor)
        val foodStatusTextView: TextView = itemView.findViewById(R.id.tv_item_on_process_status)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_on_process, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        val orderTypeLabel: String
        var orderFoodStatus = ""
        val cateringDayLeft = if (currentItem.orderDayLeft == null || currentItem.orderDayLeft == 0L){
            ""
        }
        else {
            "(${currentItem.orderDayLeft} Hari lagi)"
        }

        if (currentItem.orderType == 0L){
            orderTypeLabel= "Pesan antar"
            orderFoodStatus = currentItem.foodStatus.capitalize()
        } else {
            orderTypeLabel = "Katering"
            if (currentItem.orderDayLeft!!.toLong() > 0L && currentItem.foodStatus == "delivered"){
                orderFoodStatus = "Pesanan hari ini selesai"
            } else {
                orderFoodStatus = currentItem.foodStatus.capitalize()
            }
        }

        holder.foodNameTextView.text = currentItem.foodName.uppercase()

        holder.vendorNameTextView.text = "${currentItem.vendorName } ($orderTypeLabel)"
        holder.foodStatusTextView.text = "$orderFoodStatus $cateringDayLeft"

        currentItem.foodImgUrl?.downloadUrl?.addOnSuccessListener { uri ->

            Glide.with(context)
                .load(uri)
                .into(holder.foodImageView)
        }?.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting download URL", exception)
        }


        holder.cardViewContainer.setOnClickListener {
            val intent = Intent(context, OrderStatus::class.java)
            intent.putExtra("orderId", currentItem.orderId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
