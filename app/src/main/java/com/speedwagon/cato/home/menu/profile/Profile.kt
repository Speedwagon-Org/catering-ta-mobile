package com.speedwagon.cato.home.menu.profile


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.auth.Authentication
import com.speedwagon.cato.home.menu.profile.information.Information
import com.speedwagon.cato.home.menu.profile.location.Location


class Profile : Fragment() {
    private lateinit var profileImg : ImageView
    private lateinit var username : TextView
    private lateinit var email : TextView
    private lateinit var phone : TextView
    private lateinit var logoutBtn : CardView
    private lateinit var infoMenu : CardView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profileImg = view.findViewById(R.id.iv_profile_image)
        username = view.findViewById(R.id.tv_profile_username)
        email = view.findViewById(R.id.tv_profile_email)
        phone = view.findViewById(R.id.tv_profile_phone_number)
        logoutBtn = view.findViewById(R.id.tv_profile_logout)
        infoMenu = view.findViewById(R.id.cv_profile_option_customer_information)
        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid
        val customerRef = FirebaseFirestore.getInstance().collection("customer")

        if (uid != null){
            customerRef.document(uid).get().addOnCompleteListener {task ->
                if (task.isSuccessful){
                    val data = task.result
                    if (data.exists()){
                        username.text = data.get("name") as String
                        email.text = data.get("email") as String
                        phone.text = data.get("phone") as String
                        val profileImgPath = data.get("profile_picture") as String
                        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profileImgPath)

                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(requireContext())
                                .load(uri)
                                .into(profileImg)
                        }
                    }
                }
            }
        }

        infoMenu.setOnClickListener {
            val intent = Intent(context, Information::class.java)
            startActivity(intent)
        }
        logoutBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Apakah anda yakin untuk keluar?")
                .setPositiveButton("Iya") { _, _ ->
                    // User clicked Yes, so proceed with logout
                    auth.signOut()
                    val intent = Intent(context, Authentication::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Tidak", null) // Do nothing if the user clicks No
                .show()
        }

        val locationButton = view.findViewById<CardView>(R.id.cv_profile_option_location)
        locationButton.setOnClickListener{
            val intent = Intent(context, Location::class.java)
            startActivity(intent)
        }
        return view
    }

}