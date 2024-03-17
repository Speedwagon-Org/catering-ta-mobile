package com.speedwagon.cato.home.menu.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.search.FragmentPageAdapter


class Search : Fragment() {
    private lateinit var searchFilter: Spinner
    private lateinit var searchBadge: Spinner
    private  lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: FragmentPageAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        spinnerInitialization(view)

        //connect tablayout to viewpager2
        val fragmentManager = requireActivity().supportFragmentManager
        tabLayout =  view.findViewById(R.id.tabs)!!
        viewPager2 = view.findViewById(R.id.viewpager2)!!
        adapter= FragmentPageAdapter(fragmentManager,lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("Pesan Antar"))
        tabLayout.addTab(tabLayout.newTab().setText("Katering"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!= null){
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        return view
    }
    private fun spinnerInitialization(view : View){
        searchBadge = view.findViewById(R.id.sp_search_badge)
        searchFilter = view.findViewById(R.id.sp_search_filter)

        val filterAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.search_filter)
        )
        filterAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        val badgeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.badge_filter)
        )
        badgeAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        searchFilter.adapter = filterAdapter
        searchBadge.adapter = badgeAdapter
    }

}