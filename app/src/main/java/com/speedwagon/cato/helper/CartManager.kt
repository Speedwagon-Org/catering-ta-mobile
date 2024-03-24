package com.speedwagon.cato.helper

import android.content.Context

object CartManager {
    private const val PREFS_NAME = "CartPref"
    private const val KEY_CART_DATA = "cartData"

    data class CartItem(val vendorId: String, val foodId: String, var quantity: Int)

    fun addToCart(context: Context, vendorId: String, foodId: String, quantity: Int) {
        val cartData = getCartData(context).toMutableList()

        val existingItemIndex = cartData.indexOfFirst { it.vendorId == vendorId && it.foodId == foodId }

        if (existingItemIndex != -1) {
            clearCart(context)
        }  else {
            cartData.add(CartItem(vendorId, foodId, quantity))
            saveCartData(context, cartData)
        }

    }

    fun updateCartItem(context: Context, vendorId: String, foodId: String, newQuantity: Int) {
        val cartData = getCartData(context).map {
            if (it.vendorId == vendorId && it.foodId == foodId) {
                it.copy(quantity = newQuantity)
            } else {
                it
            }
        }
        saveCartData(context, cartData)
    }

    fun clearCart(context: Context) {
        saveCartData(context, emptyList())
    }

    fun getCartData(context: Context): List<CartItem> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cartDataString = sharedPreferences.getString(KEY_CART_DATA, null)
        return if (cartDataString != null) {
            cartDataString.split(";").mapNotNull {
                val parts = it.split(",")
                if (parts.size == 3) {
                    CartItem(parts[0], parts[1], parts[2].toInt())
                } else {
                    null
                }
            }
        } else {
            emptyList()
        }
    }

    private fun saveCartData(context: Context, cartData: List<CartItem>) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val cartDataString = cartData.filter { it.quantity > 0 }.joinToString(separator = ";") {
            "${it.vendorId},${it.foodId},${it.quantity}"
        }
        editor.putString(KEY_CART_DATA, cartDataString)
        editor.apply()
    }

    fun isCartEmpty(context: Context): Boolean {
        val cartData = getCartData(context)
        return cartData.isEmpty()
    }

    fun isItemInCart(context: Context, vendorId: String, foodId: String): Boolean {
        val cartData = getCartData(context)
        return cartData.any { it.vendorId == vendorId && it.foodId == foodId }
    }
}
