package com.example.touristguide.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val message = when (e) {
                is FirebaseAuthInvalidUserException -> "No account found with this email."
                is FirebaseAuthInvalidCredentialsException -> "Password didn't match."
                else -> e.message ?: "Login failed."
            }
            Result.failure(Exception(message))
        }
    }

    suspend fun signup(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val message = when (e) {
                is FirebaseAuthUserCollisionException -> "You already have an account."
                is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
                else -> e.message ?: "Signup failed."
            }
            Result.failure(Exception(message))
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val message = when (e) {
                is FirebaseAuthInvalidUserException -> "No account found with this email."
                else -> e.message ?: "Reset password failed."
            }
            Result.failure(Exception(message))
        }
    }

    suspend fun loginAnonymously(): Result<Unit> {
        return try {
            auth.signInAnonymously().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Anonymous login failed."))
        }
    }

    suspend fun firebaseAuthWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Google sign-in failed."))
        }
    }

    suspend fun changePassword(newPassword: String): Result<Unit> {
        val user = FirebaseAuth.getInstance().currentUser
        return if (user != null) {
            try {
                user.updatePassword(newPassword).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(Exception(e.localizedMessage ?: "Failed to update password."))
            }
        } else {
            Result.failure(Exception("No user is currently logged in."))
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser() = auth.currentUser
} 