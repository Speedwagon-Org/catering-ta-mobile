package com.speedwagon.cato.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.home.item.OnProcessItem
import com.speedwagon.cato.home.menu.adapter.order.OrderHistoryAdapter


class Order : Fragment() {
    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var spFilterStatus: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        recyclerViewInitialization(view)
        spinnerInitialization(view)
        return view
    }

    private fun recyclerViewInitialization(view : View){
        rvOrderHistory = view.findViewById(R.id.rv_order_vendor)

        rvOrderHistory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val dummyDataOnProcessList = listOf(
            OnProcessItem(
                foodName = "Food 1",
                vendorName = "Vendor 1",
                foodStatus = 1,
                foodImgUrl = "https://asset.kompas.com/crops/AWXtnkYHOrbSxSggVuTs3EzQprM=/10x36:890x623/750x500/data/photo/2023/03/25/641e5ef63dea4.jpg"
            ),
            OnProcessItem(
                foodName = "Food 2",
                vendorName = "Vendor 2",
                foodStatus = 1,
                foodImgUrl = "https://asset.kompas.com/crops/AWXtnkYHOrbSxSggVuTs3EzQprM=/10x36:890x623/750x500/data/photo/2023/03/25/641e5ef63dea4.jpg"
            )
        )
        rvOrderHistory.adapter = OrderHistoryAdapter(requireContext(), dummyDataOnProcessList)
    }
    private fun spinnerInitialization(view : View){
        spFilterStatus = view.findViewById(R.id.sp_order_status_filter)
        val statusFilterAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.status_filter)
        )
        statusFilterAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spFilterStatus.adapter = statusFilterAdapter
    }
}