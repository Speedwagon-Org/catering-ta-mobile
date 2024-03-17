package com.speedwagon.cato.home.menu.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.search.SearchVendorAdapter
import com.speedwagon.cato.home.menu.adapter.search.item.Vendor

class KateringFragment : Fragment() {
    private lateinit var rvSearchVendor: RecyclerView
    private lateinit var vendors : ArrayList<Map<String, *>>
    private lateinit var db : FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_katering, container, false)
        rvSearchVendor = view.findViewById(R.id.rv_search_vendor)
        rvSearchVendor.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)
        db = FirebaseFirestore.getInstance()
        vendors = ArrayList()
        val vendorRef = db.collection("vendor")
        vendorRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val snapshot = task.result
                if (snapshot != null){
                    for (document in snapshot.documents){
                        val vendorId = document.id
                        val vendorData = document.data
                        vendors.add(
                            mapOf(
                                "id" to vendorId,
                                "data" to vendorData
                            )
                        )
                    }
                    recyclerviewInitialization()
                }
            }
        }

        return view
    }

    private fun recyclerviewInitialization(){
        val dataVendor : ArrayList<Vendor> = ArrayList()
        for (vendor in vendors){
            val id = vendor["id"] as String
            val name = (vendor["data"] as Map<*,*>) ["name"] as String
            val img =(vendor["data"] as Map<*,*>) ["profile_picture"] as String
            val isAvailableCatering =(vendor["data"] as Map<*,*>) ["catering_is_available"] as Boolean
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(img)
            if (isAvailableCatering){
                dataVendor.add(
                    Vendor(
                        id = id,
                        name = name,
                        distance = 10.1,
                        imgUrl = storageReference
                    )
                )
            }

        }
        rvSearchVendor.adapter = SearchVendorAdapter(requireContext(), dataVendor)
    }
}