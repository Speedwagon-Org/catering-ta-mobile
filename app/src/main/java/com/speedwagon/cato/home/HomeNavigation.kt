package com.speedwagon.cato.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.Home
import com.speedwagon.cato.home.menu.Order
import com.speedwagon.cato.home.menu.Profile
import com.speedwagon.cato.home.menu.Search.Search

class HomeNavigation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_navigation)

        val bottomNavigationViewHome: BottomNavigationView = findViewById(R.id.bnv_home)
        bottomNavigationViewHome.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val homeFragment = Home()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fcv_home, homeFragment)
                    fragmentTransaction.commit()
                    true
                }
                R.id.menu_search -> {
                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val searchFragment = Search()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fcv_home, searchFragment)
                    fragmentTransaction.commit()
                    true
                }
                R.id.menu_order -> {
                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val orderFragment = Order()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fcv_home, orderFragment)
                    fragmentTransaction.commit()
                    true
                }
                R.id.menu_profile -> {
                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val profileFragment = Profile()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fcv_home, profileFragment)
                    fragmentTransaction.commit()
                    true
                }
                else -> false
            }
        }
    }
}