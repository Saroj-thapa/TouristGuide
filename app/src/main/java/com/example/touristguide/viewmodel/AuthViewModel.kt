package com.example.touristguide.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.repository.FirebaseService
import com.example.touristguide.data.repository.FirebaseRepository
import com.example.touristguide.data.repository.RepositoryException
import com.example.touristguide.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.widget.Toast
import android.util.Log
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val firebaseService: FirebaseService = FirebaseService(),
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState: StateFlow<Result<Unit>?> = _authState

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isAnonymous = MutableStateFlow(false)
    val isAnonymous: StateFlow<Boolean> = _isAnonymous

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = firebaseService.login(email, password)
            _authState.value = result
            if (result.isSuccess) {
                fetchUser()
            }
        }
    }

    fun signup(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            val result = firebaseService.signup(email, password)
            if (result.isSuccess) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val user = User(uid, firstName, lastName, email)
                firebaseRepository.addUser(uid, mapOf(
                    "uid" to uid,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email
                ))
                fetchUser()
            }
            _authState.value = result
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = firebaseService.resetPassword(email)
        }
    }

    fun loginAnonymously() {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().signInAnonymously().await()
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                _isAnonymous.value = firebaseUser?.isAnonymous == true
                if (firebaseUser != null && firebaseUser.isAnonymous) {
                    val user = User(uid = firebaseUser.uid, firstName = "Anonymous", lastName = "", email = "")
                    _user.value = user
                    _userName.value = "Anonymous"
                    _userEmail.value = ""
                }
            } catch (e: Exception) {
                _isAnonymous.value = false
            }
        }
    }

    fun logout() {
        firebaseService.logout()
        _userName.value = ""
        _user.value = null
        _isAnonymous.value = false
    }

    fun isUserLoggedIn(): Boolean = firebaseService.isUserLoggedIn()

    fun fetchUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        val uid = firebaseUser.uid
        _isAnonymous.value = firebaseUser.isAnonymous
        if (firebaseUser.isAnonymous) {
            // For anonymous users, do not load or save from RTDB
            val user = User(uid = uid, firstName = "Anonymous", lastName = "", email = "")
            _user.value = user
            _userName.value = "Anonymous"
            _userEmail.value = ""
            return
        }
        viewModelScope.launch {
            try {
                val userMap = firebaseRepository.getUser(uid)
                if (userMap != null) {
                    val user = User(
                        uid = userMap["uid"] as? String ?: uid,
                        firstName = userMap["firstName"] as? String ?: "",
                        lastName = userMap["lastName"] as? String ?: "",
                        email = userMap["email"] as? String ?: (firebaseUser.email ?: ""),
                        profileImageUrl = userMap["profileImageUrl"] as? String ?: ""
                    )
                    _user.value = user
                    _userName.value = "${user.firstName} ${user.lastName}".trim()
                    _userEmail.value = user.email
                } else {
                    // User data missing in RTDB, create it from FirebaseAuth info
                    val email = firebaseUser.email ?: ""
                    val displayName = firebaseUser.displayName ?: ""
                    val (firstName, lastName) = if (displayName.contains(" ")) {
                        val parts = displayName.split(" ", limit = 2)
                        parts[0] to parts.getOrElse(1) { "" }
                    } else displayName to ""
                    val user = User(uid, firstName, lastName, email, "")
                    try {
                        firebaseRepository.addUser(uid, mapOf(
                            "uid" to uid,
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "profileImageUrl" to ""
                        ))
                        Log.d("AuthViewModel", "User $uid created in RTDB: $user")
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Failed to create user $uid in RTDB", e)
                    }
                    _user.value = user
                    _userName.value = "${user.firstName} ${user.lastName}".trim()
                    _userEmail.value = user.email
                }
            } catch (e: RepositoryException) {
                Log.e("AuthViewModel", "Failed to fetch user $uid", e)
                val user = User(uid, "", "", firebaseUser.email ?: "", "")
                _user.value = user
                _userName.value = ""
                _userEmail.value = user.email
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                if (authResult.user != null) {
                    fetchUser()
                    onResult(true, null)
                } else {
                    onResult(false, "Authentication failed.")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }
}
