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
                            val orderType = order.getLong("order_type")!!
                            val dayLeft = if (order.getLong("order_type")==1L) {
                                order.getLong("order_day_left")
                            } else {
                                null
                            }
                            orderRef.document(orderId).collection("foods").get().addOnCompleteListener {foodTask ->
                                val foods = foodTask.result
                                for (food in foods){
                                    if (vendorId != null){
                                        vendorRef.document(vendorId).get().addOnCompleteListener { vendorTask ->
                                            if (vendorTask.isSuccessful){
                                                val vendorRes = vendorTask.result
                                                if (vendorRes.exists()){
                                                    val vendorName = vendorRes.getString("name")
                                                    val foodName = food.getString("name")
                                                    val foodImgRef = food.getString("photo")!!
                                                    val foodImg = fireStorage.getReferenceFromUrl(foodImgRef)
                                                    ordersData.add(
                                                        OnProcessItem(
                                                            orderId = orderId,
                                                            foodName = foodName!!,
                                                            vendorName = vendorName!!,
                                                            foodStatus = orderStatus!!,
                                                            foodImgUrl = foodImg,
                                                            orderType = orderType,
                                                            orderDayLeft = dayLeft
                                                        )
                                                    )
                                                    recyclerViewInitialization(view, ordersData)
                                                }
                                            }
                                        }
                                    }
                                    break
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
}