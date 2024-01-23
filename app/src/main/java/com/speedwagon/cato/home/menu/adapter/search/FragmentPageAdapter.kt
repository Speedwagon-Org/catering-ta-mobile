package com.speedwagon.cato.home.menu.adapter.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.Lifecycle
import com.speedwagon.cato.home.menu.Search.KateringFragment
import com.speedwagon.cato.home.menu.Search.PesanAntarFragment

class FragmentPageAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position ==0)
            PesanAntarFragment()
        else
            KateringFragment()
    }
}