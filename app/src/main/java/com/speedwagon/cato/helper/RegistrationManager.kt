package com.speedwagon.cato.helper
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationManager {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun registerWithEmailAndPassword(email: String, username: String, phone: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        // Check if email is already registered
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result?.signInMethods?.isNotEmpty() == true) {
                        onComplete(false, "Email is already registered")
                    } else {
                        // Email is not registered, proceed with registration
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { registrationTask ->
                                if (registrationTask.isSuccessful) {
                                    // Registration successful, get UID and create Firestore document
                                    val user = auth.currentUser
                                    user?.let { currentUser ->
                                        createFirestoreDocument(currentUser.uid, email, username, phone)
                                        onComplete(true, null)
                                    }
                                } else {
                                    // Registration failed
                                    onComplete(false, registrationTask.exception?.message)
                                }
                            }
                    }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }



    private fun createFirestoreDocument(uid: String, email: String, username: String, phone: String) {
        val data = hashMapOf(
            "email" to email,
            "name" to username,
            "phone" to phone,
            "profile_picture" to "gs://ta-catering-online.appspot.com/user_template/profile_pict.jpg",
            "default_location" to "NY5lLQOlI76B3x923WzE"
        )

        firestore.collection("customer")
            .document(uid) // Set document ID to the UID
            .set(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Document created with ID: $uid")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

}
