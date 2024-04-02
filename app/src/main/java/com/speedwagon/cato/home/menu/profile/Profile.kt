package com.speedwagon.cato.home.menu.profile


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.speedwagon.cato.R
import com.speedwagon.cato.auth.Authentication
import com.speedwagon.cato.home.menu.profile.information.Information
import com.speedwagon.cato.home.menu.profile.location.Location
import java.util.UUID


class Profile : Fragment() {

    private val PICK_IMAGE_REQUEST = 1

    private lateinit var profileImg : ImageView
    private lateinit var username : TextView
    private lateinit var email : TextView
    private lateinit var phone : TextView
    private lateinit var logoutBtn : CardView
    private lateinit var infoMenu : CardView

    private lateinit var storage : FirebaseStorage
    private lateinit var storageReference : StorageReference
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.uid
        val customerRef = FirebaseFirestore.getInstance().collection("customer")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        if (uid != null){
            customerRef.document(uid).get().addOnCompleteListener {task ->
                if (task.isSuccessful){
                    val data = task.result
                    if (data.exists()){
                        username.text = data.get("name") as String
                        email.text = data.get("email") as String
                        phone.text = data.get("phone") as String
                        val profileImgPath = data.get("profile_picture") as String
                        val storageReference = storage.getReferenceFromUrl(profileImgPath)

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


        profileImg.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PICK_IMAGE_REQUEST
                )
            } else if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PICK_IMAGE_REQUEST
                )
            }
            else {
                openGallery()
            }
        }
        val locationButton = view.findViewById<CardView>(R.id.cv_profile_option_location)
        locationButton.setOnClickListener{
            val intent = Intent(context, Location::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            uploadImage(imageUri)
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(requireContext().contentResolver.getType(imageUri))
        val imageName = "${UUID.randomUUID()}.$fileExtension"
        val imageRef = storageReference.child("${auth.currentUser!!.uid}/$imageName")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { _ ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Update the profile picture URL in Firestore
                    db.collection("customer").document(auth.currentUser!!.uid)
                        .update("profile_picture", imageUrl)
                        .addOnSuccessListener {
                            // Profile picture URL updated successfully
                            Toast.makeText(context, "Gambar telah diupdate", Toast.LENGTH_SHORT)
                                .show()
                            // Load the image using Glide into profileImg
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .into(profileImg)
                        }
                        .addOnFailureListener { e ->
                            // Handle error updating profile picture URL
                            Log.e(TAG, "Error updating profile picture URL: $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle image upload failure
                Log.e(TAG, "Error uploading image: $e")
                // You may want to show a Toast or dialog to inform the user about the error
            }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
        }
    }
}