package com.speedwagon.cato.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.home.NewlyOpenAdapter
import com.speedwagon.cato.home.menu.adapter.home.OnProcessAdapter
import com.speedwagon.cato.home.menu.adapter.home.PopularFoodAdapter
import com.speedwagon.cato.home.menu.adapter.home.item.NewlyOpen
import com.speedwagon.cato.home.menu.adapter.home.item.OnProcessItem
import com.speedwagon.cato.home.menu.adapter.home.item.PopularFood


class Home : Fragment() {
    private lateinit var rvOnProcessItem: RecyclerView
    private lateinit var rvPopularItem : RecyclerView
    private lateinit var rvNewlyOpen: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // RecyclerView value initializer
        rvOnProcessItem = view.findViewById(R.id.rv_home_on_process)
        rvPopularItem = view.findViewById(R.id.rv_home_popular)
        rvNewlyOpen = view.findViewById(R.id.rv_home_newly_open)

        // On Process RecyclerView
        rvOnProcessItem.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
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
        rvOnProcessItem.adapter = OnProcessAdapter(requireContext(), dummyDataOnProcessList)

        // Popular RecyclerVIew
        rvPopularItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dummyDataPopularFood = listOf(
            PopularFood(
                foodName = "Food 1",
                vendorName = "Vendor 1",
                foodPrice = 12000,
                foodImgUrl = "https://www.indonesia.travel/content/dam/indtravelrevamp/id-id/ide-liburan/fakta-menarik-di-balik-nikmatnya-sate-padang/thumbnail.jpg"
            )
        )
        rvPopularItem.adapter = PopularFoodAdapter(requireContext(), dummyDataPopularFood)

        // NewlyOpen RecyclerView
        rvNewlyOpen.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dummyDataNewlyOpen = listOf(
            NewlyOpen(
                vendorName = "Vendor 1",
                vendorFoodType = "Type 1",
                vendorDistance = 1.1,
                vendorImgUrl = "data:image/png;base64,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"
            )
        )
        rvNewlyOpen.adapter = NewlyOpenAdapter(requireContext(), dummyDataNewlyOpen)

        return view
    }
}