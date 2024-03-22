package com.speedwagon.cato.home.menu.adapter.order

import android.content.ContentValues
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

class OrderHistoryAdapter (private val context: Context, private val itemList: List<OnProcessItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewContainer: CardView = itemView.findViewById(R.id.cv_item_order_history_container)
        val foodImageView: ImageView = itemView.findViewById(R.id.iv_item_order_history_image)
        val foodNameTextView: TextView = itemView.findViewById(R.id.tv_item_order_history_food)
        val vendorNameTextView: TextView = itemView.findViewById(R.id.tv_item_order_history_vendor)
        val foodStatusTextView: TextView = itemView.findViewById(R.id.tv_item_order_history_status)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.foodNameTextView.text = currentItem.foodName.uppercase()
        holder.vendorNameTextView.text = currentItem.vendorName
        holder.foodStatusTextView.text = currentItem.foodStatus.uppercase()

        currentItem.foodImgUrl?.downloadUrl?.addOnSuccessListener {uri ->
            Glide.with(context)
                .load(uri)
                .into(holder.foodImageView)
        }?.addOnFailureListener { exception ->
            Log.e(ContentValues.TAG, "Error getting download URL", exception)
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