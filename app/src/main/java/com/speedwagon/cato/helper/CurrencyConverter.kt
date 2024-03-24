package com.speedwagon.cato.helper

object CurrencyConverter {
    fun intToIDR(amount: Long): String {
        val formatter = java.text.NumberFormat.getCurrencyInstance()
        formatter.maximumFractionDigits = 0 // To remove decimal digits
        formatter.currency = java.util.Currency.getInstance("IDR")
        return formatter.format(amount)
    }
}