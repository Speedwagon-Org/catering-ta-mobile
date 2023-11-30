package com.speedwagon.cato.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.Beranda
import com.speedwagon.cato.home.menu.Cari
import com.speedwagon.cato.home.menu.Pesanan
import com.speedwagon.cato.home.menu.Profil

class HomeNavigation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_navigation)

        val bottomNavigationViewHome: BottomNavigationView = findViewById(R.id.bnv_home)
        bottomNavigationViewHome.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_beranda -> {
                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val berandaFragment = Beranda()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fcv_home, berandaFragment)
                    fragmentTransaction.commit()
                    true
                }
                R.id.menu_cari -> {
                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val cariFragment = Cari()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fcv_home, cariFragment)
                    fragmentTransaction.commit()
                    true
                }
                R.id.menu_pesanan -> {
                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val pesananFragment = Pesanan()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fcv_home, pesananFragment)
                    fragmentTransaction.commit()
                    true
                }
                R.id.menu_profile -> {
                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val profilFragment = Profil()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.fcv_home, profilFragment)
                    fragmentTransaction.commit()
                    true
                }
                else -> false
            }
        }
    }
}