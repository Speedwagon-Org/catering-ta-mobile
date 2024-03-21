package com.speedwagon.cato.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.home.item.OnProcessItem
import com.speedwagon.cato.home.menu.adapter.order.OrderHistoryAdapter


class Order : Fragment() {
    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var fireStorage : FirebaseStorage
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fireStorage = FirebaseStorage.getInstance()

        val userId = auth.currentUser?.uid
        val orderRef = db.collection("orders")
        val vendorRef = db.collection("vendor")
        val ordersData = ArrayList<OnProcessItem>()

        if (userId != null){
            orderRef.get().addOnCompleteListener { orderTask ->
                if (orderTask.isSuccessful){
                    val orderRes = orderTask.result
                    if (!orderRes.isEmpty){
                        for (order in orderRes){
                            val orderId = order.id
                            val orderStatus = order.getString("status")
                            val vendorId = order.getString("vendor")

                            if (vendorId != null){
                                vendorRef.document(vendorId).get().addOnCompleteListener { vendorTask ->
                                    if (vendorTask.isSuccessful){
                                        val vendorRes = vendorTask.result
                                        if (vendorRes.exists()){
                                            val vendorName = vendorRes.getString("name")
                                            val foodsRef = vendorRef.document(vendorId).collection("foods")
                                            foodsRef.get().addOnCompleteListener { foodsTask ->
                                                if (foodsTask.isSuccessful){
                                                    val foodsRes = foodsTask.result
                                                    if (!foodsRes.isEmpty){
                                                        var count = 0
                                                        for (food in foodsRes){
                                                            if (count == 0){
                                                                val foodName = food.getString("name")
                                                                val foodImgRef = food.getString("photo")!!
                                                                val foodImg = fireStorage.getReferenceFromUrl(foodImgRef)
                                                                ordersData.add(
                                                                    OnProcessItem(
                                                                        orderId = orderId,
                                                                        foodName = foodName!!,
                                                                        vendorName = vendorName!!,
                                                                        foodStatus = orderStatus!!,
                                                                        foodImgUrl = foodImg
                                                                    )
                                                                )
                                                                recyclerViewInitialization(view, ordersData)
                                                                count++
                                                            } else {
                                                                break
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            activity?.finish()
        }

        return view
    }

    private fun recyclerViewInitialization(view : View, ordersData : ArrayList<OnProcessItem>){
        println("Order Data : $ordersData")
        rvOrderHistory = view.findViewById(R.id.rv_order_vendor)
        rvOrderHistory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        rvOrderHistory.adapter = OrderHistoryAdapter(requireContext(), ordersData)

    }

//            listOf(
//            OnProcessItem(
//                orderId = "10",
//                foodName = "Food 1",
//                vendorName = "Vendor 1",
//                foodStatus = "On Proess",
//                foodImgUrl = null
//            ),
//            OnProcessItem(
//                orderId = "20",
//                foodName = "Food 2",
//                vendorName = "Vendor 2",
//                foodStatus = "On Proess",
//                foodImgUrl = null
//            )
//        )
//    private fun spinnerInitialization(view : View){
//        spFilterStatus = view.findViewById(R.id.sp_order_status_filter)
//        val statusFilterAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            resources.getStringArray(R.array.status_filter)
//        )
//        statusFilterAdapter.setDropDownViewResource(
//            android.R.layout.simple_spinner_dropdown_item
//        )
//
//        spFilterStatus.adapter = statusFilterAdapter
//    }
}