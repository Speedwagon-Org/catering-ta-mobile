package com.speedwagon.cato.home.menu.profile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.profile.location.Location


class Profile : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val locationButton = view.findViewById<CardView>(R.id.cv_profile_option_location)
        locationButton.setOnClickListener{
            val intent = Intent(context, Location::class.java)
            startActivity(intent)
        }
        return view
    }

}