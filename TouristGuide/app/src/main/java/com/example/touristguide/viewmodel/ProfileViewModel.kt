package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileViewModel : ViewModel() {
    fun changePassword(newPassword: String, callback: (Boolean, String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Password updated successfully.")
                    } else {
                        callback(false, task.exception?.localizedMessage ?: "Failed to update password.")
                    }
                }
        } else {
            callback(false, "No user is currently logged in.")
        }
    }

    fun updateProfileImageUrl(url: String, callback: (Boolean, String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseDatabase.getInstance().reference
            db.child("users").child(user.uid).child("profileImageUrl").setValue(url)
                .addOnSuccessListener { callback(true, "Profile image updated!") }
                .addOnFailureListener { e -> callback(false, e.localizedMessage ?: "Failed to update image URL.") }
        } else {
            callback(false, "No user is currently logged in.")
        }
    }
}
