package com.speedwagon.cato.home.menu.adapter.home

import android.annotation.SuppressLint
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
import com.speedwagon.cato.home.menu.adapter.home.item.NearVendor
import com.speedwagon.cato.vendor.DetailVendor

class NearVendorAdapter (private val context: Context, private val itemList: List<NearVendor>) :
    RecyclerView.Adapter<NearVendorAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewContainer: CardView = itemView.findViewById(R.id.cv_item_newly_open_container)
        val vendorImageView: ImageView = itemView.findViewById(R.id.iv_item_newly_open_image)
        val vendorNameTextView: TextView = itemView.findViewById(R.id.tv_newly_open_vendor)
        val vendorDistanceTextView: TextView = itemView.findViewById(R.id.tv_newly_open_distance)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_newly_open, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.vendorNameTextView.text = currentItem.vendorName
        holder.vendorDistanceTextView.text = "%.2f km".format(currentItem.vendorDistance)
        currentItem.vendorImgUrl!!.downloadUrl.addOnSuccessListener { uri  ->
            println("result $uri")
            Glide.with(context)
                .load(uri)
                .into(holder.vendorImageView)
        }.addOnFailureListener { exception ->
            Log.e(ContentValues.TAG, "Error getting download URL", exception)
        }


        holder.cardViewContainer.setOnClickListener {
            val intent = Intent(context, DetailVendor::class.java)
            intent.putExtra("vendorId", currentItem.vendorId)
            intent.putExtra("vendorName",currentItem.vendorName)
            intent.putExtra("vendorDistance",currentItem.vendorDistance)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}