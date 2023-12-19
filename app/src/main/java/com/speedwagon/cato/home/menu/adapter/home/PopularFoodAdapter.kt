package com.speedwagon.cato.home.menu.adapter.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.home.item.PopularFood

class PopularFoodAdapter(private val context: Context, private val itemList: List<PopularFood>) :
    RecyclerView.Adapter<PopularFoodAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val cardViewContainer: CardView = itemView.findViewById(R.id.cv_item_popular_food_container)
            val foodImageView: ImageView = itemView.findViewById(R.id.iv_item_popular_image)
            val foodNameTextView : TextView = itemView.findViewById(R.id.tv_item_popular_food)
            val vendorNameTextView : TextView = itemView.findViewById(R.id.tv_item_popular_vendor)
            val foodPriceTextView : TextView = itemView.findViewById(R.id.tv_item_popular_price)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_popular_food, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.foodNameTextView.text = currentItem.foodName
        holder.foodNameTextView.text = currentItem.foodName
        holder.vendorNameTextView.text = currentItem.vendorName
        holder.foodPriceTextView.text = "Rp. ${currentItem.foodPrice}"

        Glide.with(context)
            .load(currentItem.foodImgUrl)
            .into(holder.foodImageView)
        holder.cardViewContainer.setOnClickListener {

        }
    }
}