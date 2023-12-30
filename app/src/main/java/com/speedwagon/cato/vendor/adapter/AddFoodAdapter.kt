package com.speedwagon.cato.vendor.adapter

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
import com.speedwagon.cato.vendor.adapter.item.AddFoods

class AddFoodAdapter (private val context: Context, private val itemList: List<AddFoods>):
    RecyclerView.Adapter<AddFoodAdapter.ViewHolder>() {
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
            holder.foodNameTextView.text = currentItem.foodName
            holder.foodQtyTextView.text = currentItem.foodQty.toString()
            holder.foodPriceTextView.text = (currentItem.foodDiscount * ((100 - currentItem.foodDiscount)/100)).toString()
            Glide.with(context)
                .load(currentItem.foodImgUrl)
                .into(holder.foodImageView)

            if (currentItem.foodQty > 0){
                holder.foodBtnSub.visibility = View.VISIBLE
                holder.foodQtyTextView.visibility = View.VISIBLE
            }


            if (currentItem.foodDiscount > 0){
                holder.foodDiscountTextView.visibility = View.VISIBLE
                holder.foodDiscountTextView.text = currentItem.foodDiscount.toString() + "%"
            }

            holder.foodBtnAdd.setOnClickListener {
                currentItem.foodQty++
            }
            holder.foodBtnSub.setOnClickListener {
                currentItem.foodQty--
            }

            holder.cardViewContainer.setOnClickListener {
                // Handle item click event here
                // You can pass data or perform an action based on the clicked item
            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
}