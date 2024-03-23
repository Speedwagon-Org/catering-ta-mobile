package com.speedwagon.cato.home.menu.profile.location.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.profile.location.DetailLocation

class LocationsAdapter(private val activity: Activity, private val itemList: List<LocationItem>):
    RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelTextView: TextView = itemView.findViewById(R.id.tv_item_location_label)
        val descTextView: TextView = itemView.findViewById(R.id.tv_item_location_detail)
        val containerCardView: CardView = itemView.findViewById(R.id.cv_item_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: LocationsAdapter.ViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.labelTextView.text = currentItem.label
        holder.descTextView.text = currentItem.description

        holder.containerCardView.setOnClickListener{
            val intent = Intent(activity, DetailLocation::class.java)
            intent.putExtra("data", currentItem)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    companion object {
        const val REQUEST_CODE =100
    }
}
