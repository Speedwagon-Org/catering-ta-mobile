package com.speedwagon.cato.home.menu.adapter.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.home.item.NewlyOpen
import com.speedwagon.cato.vendor.DetailVendor

class NewlyOpenAdapter (private val context: Context, private val itemList: List<NewlyOpen>) :
    RecyclerView.Adapter<NewlyOpenAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewContainer: CardView = itemView.findViewById(R.id.cv_item_newly_open_container)
        val vendorImageView: ImageView = itemView.findViewById(R.id.iv_item_newly_open_image)
        val vendorNameTextView: TextView = itemView.findViewById(R.id.tv_newly_open_vendor)
        val vendorDistanceTextView: TextView = itemView.findViewById(R.id.tv_newly_open_distance)
        val vendorFoodTypeTextView: TextView = itemView.findViewById(R.id.tv_newly_opened_food_type)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_newly_open, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.vendorNameTextView.text = currentItem.vendorName
        holder.vendorDistanceTextView.text = currentItem.vendorDistance.toString() + " km"
        holder.vendorFoodTypeTextView.text = currentItem.vendorFoodType

        Glide.with(context)
            .load(currentItem.vendorImgUrl)
            .into(holder.vendorImageView)

        holder.cardViewContainer.setOnClickListener {
            val intent = Intent(context, DetailVendor::class.java)
            intent.putExtra("vendorName", currentItem.vendorName)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}