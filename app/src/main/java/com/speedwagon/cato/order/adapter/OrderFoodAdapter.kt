package com.speedwagon.cato.order.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.speedwagon.cato.R
import com.speedwagon.cato.order.adapter.item.OrderedFood

class OrderFoodAdapter(private val context: Context, private val itemList: List<OrderedFood>) :
    RecyclerView.Adapter<OrderFoodAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val foodNameTextView : TextView = itemView.findViewById(R.id.tv_item_order_detail_food_name)
            val foodPriceTextView : TextView = itemView.findViewById(R.id.tv_item_order_detail_food_price)
            val foodQtyTextView : TextView = itemView.findViewById(R.id.tv_item_order_detail_food_qty)
            val foodPictImageView : ImageView = itemView.findViewById(R.id.iv_item_order_detail_food_pict)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_food, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.foodQtyTextView.text = currentItem.foodQty.toString() + "x"
        holder.foodNameTextView.text = currentItem.foodName
        holder.foodPriceTextView.text = "Rp " + currentItem.foodPrice.toString()
        Glide.with(context)
            .load(currentItem.foodPictUrl)
            .into(holder.foodPictImageView)
    }

}