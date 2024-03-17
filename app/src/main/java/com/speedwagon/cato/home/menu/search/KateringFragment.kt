package com.speedwagon.cato.home.menu.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.search.SearchVendorAdapter
import com.speedwagon.cato.home.menu.adapter.search.item.Vendor

class KateringFragment : Fragment() {
    private lateinit var rvSearchVendor: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_katering, container, false)
        recyclerviewInitialization(view)
        return view
    }

    private fun recyclerviewInitialization(view: View){
        rvSearchVendor = view.findViewById(R.id.rv_search_vendor)
        rvSearchVendor.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)
        val dummyDataSearch = ArrayList<Vendor>()
        rvSearchVendor.adapter = SearchVendorAdapter(requireContext(), dummyDataSearch)
    }
}