package com.speedwagon.cato.home.menu.adapter.search

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
import com.speedwagon.cato.home.menu.adapter.search.item.Vendor
import com.speedwagon.cato.vendor.DetailVendor

class SearchVendorAdapter (private val context: Context, private val itemList: List<Vendor>)
    :RecyclerView.Adapter<SearchVendorAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val searchVendorContainerCardView : CardView = itemView.findViewById(R.id.cv_search_vendor_container)
        val searchVendorImageImageView : ImageView = itemView.findViewById(R.id.iv_search_vendor_image)
        val searchVendorNameTextView : TextView = itemView.findViewById(R.id.tv_search_vendor_name)
        val searchVendorDistanceTextView : TextView = itemView.findViewById(R.id.tv_search_vendor_distance)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vendor_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.searchVendorDistanceTextView.text = currentItem.distance.toString() + " km"
        holder.searchVendorNameTextView.text = currentItem.name
        currentItem.imgUrl!!.downloadUrl.addOnSuccessListener {uri ->
            Glide.with(context)
                .load(uri)
                .into(holder.searchVendorImageImageView)
        }

        holder.searchVendorContainerCardView.setOnClickListener {
            val intent = Intent(context, DetailVendor::class.java)
            intent.putExtra("vendor_id", currentItem.id)

            context.startActivity(intent)
        }

    }

}