package com.speedwagon.cato.vendor.adapter

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
import com.speedwagon.cato.vendor.adapter.item.VendorFood
import com.speedwagon.cato.vendor.foods.DetailFood

class DetailVendorFoodAdapter (private val context: Context, private val itemList: List<VendorFood>):
    RecyclerView.Adapter<DetailVendorFoodAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardViewContainer : CardView = itemView.findViewById(R.id.cv_item_add_container)
            val foodImageView : ImageView = itemView.findViewById(R.id.iv_item_add_image)
            val foodNameTextView : TextView = itemView.findViewById(R.id.tv_item_add_food_name)
            val foodQtyTextView : TextView = itemView.findViewById(R.id.tv_item_add_food_qty)
            val foodDiscountTextView : TextView = itemView.findViewById(R.id.tv_item_add_food_discount)
            val foodPriceTextView : TextView = itemView.findViewById(R.id.tv_item_add_food_price)
            val foodBtnAdd : ImageView = itemView.findViewById(R.id.iv_item_add_plus_btn)
            val foodBtnSub : ImageView = itemView.findViewById(R.id.iv_item_add_min_btn)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_add, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = itemList[position]
            if (currentItem.foodQty > 0){
                holder.foodBtnSub.visibility = View.VISIBLE
                holder.foodQtyTextView.visibility = View.VISIBLE
            } else {
                holder.foodBtnSub.visibility = View.GONE
                holder.foodQtyTextView.visibility = View.GONE
            }
            holder.foodNameTextView.text = currentItem.foodName
            holder.foodQtyTextView.text = currentItem.foodQty.toString()
            holder.foodPriceTextView.text = "Rp " + (currentItem.foodPrice * ((100F - currentItem.foodDiscount)/100F)).toInt().toString()

            currentItem.foodImgUrl.downloadUrl.addOnSuccessListener { uri  ->
                println("result $uri")
                Glide.with(context)
                    .load(uri)
                    .into(holder.foodImageView)
            }.addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error getting download URL", exception)
            }



            if (currentItem.foodDiscount > 0){
                holder.foodDiscountTextView.visibility = View.VISIBLE
                holder.foodDiscountTextView.text = "Discount " + currentItem.foodDiscount.toString() + "%"
            } else {
                holder.foodDiscountTextView.visibility = View.GONE
            }
    
            holder.foodBtnAdd.setOnClickListener {
                currentItem.foodQty++
                notifyItemChanged(position)
            }

            holder.foodBtnSub.setOnClickListener {
                if (currentItem.foodQty > 0) {
                    currentItem.foodQty--
                    notifyItemChanged(position)
                }
            }

            holder.cardViewContainer.setOnClickListener {
                val intent = Intent(context, DetailFood::class.java)
                intent.putExtra("foodId", currentItem.foodId)
                intent.putExtra("foodQty", currentItem.foodQty)

                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
}