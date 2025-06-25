package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.repository.FirebaseService
import com.example.touristguide.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val firebaseService: FirebaseService = FirebaseService()
) : ViewModel() {

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState: StateFlow<Result<Unit>?> = _authState

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = firebaseService.login(email, password)
            _authState.value = result
            if (result.isSuccess) {
                fetchUserName()
            }
        }
    }

    fun signup(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            val result = firebaseService.signup(email, password)
            if (result.isSuccess) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val user = User(uid, firstName, lastName, email)
                FirebaseFirestore.getInstance().collection("users").document(uid).set(user)
                fetchUserName()
            }
            _authState.value = result
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = firebaseService.resetPassword(email)
        }
    }

    fun logout() {
        firebaseService.logout()
        _userName.value = ""
    }

    fun isUserLoggedIn(): Boolean = firebaseService.isUserLoggedIn()

    fun fetchUserName() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val firstName = doc.getString("firstName") ?: ""
                val lastName = doc.getString("lastName") ?: ""
                val email = doc.getString("email") ?: ""
                _userName.value = "$firstName $lastName".trim()
                _userEmail.value = email
            }
    }

    fun changePassword(newPassword: String, onResult: (Boolean, String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, "Password updated successfully.")
                    } else {
                        onResult(false, task.exception?.message ?: "Password update failed.")
                    }
                }
        } else {
            onResult(false, "No user logged in.")
        }
    }
}
