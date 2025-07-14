package com.example.touristguide.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.google.firebase.database.GenericTypeIndicator

class RepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)

class FirebaseRepository {
    private val db = FirebaseDatabase.getInstance().reference

    // User Profile
    suspend fun addUser(userId: String, data: Map<String, Any>) {
        try {
            Log.d("FirebaseRepository", "Saving user $userId: $data")
            db.child("users").child(userId).setValue(data).await()
            Log.d("FirebaseRepository", "User $userId saved successfully")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to add user $userId", e)
            throw RepositoryException("Failed to add user", e)
        }
    }
    @Suppress("UNCHECKED_CAST")
    suspend fun getUser(userId: String): Map<String, Any>? {
        return try {
            Log.d("FirebaseRepository", "Loading user $userId from RTDB")
            val snapshot = db.child("users").child(userId).get().await()
            val genericTypeIndicator = object : GenericTypeIndicator<Map<String, Any>>() {}
            val result = snapshot.getValue(genericTypeIndicator)
            Log.d("FirebaseRepository", "Loaded user $userId: $result")
            result
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to get user $userId", e)
            throw RepositoryException("Failed to get user", e)
        }
    }
    suspend fun updateUser(userId: String, data: Map<String, Any>) {
        try {
            db.child("users").child(userId).updateChildren(data).await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to update user", e)
        }
    }

    // Trip Plan
    suspend fun addPlan(planId: String, data: Map<String, Any>) {
        try {
            db.child("plans").child(planId).setValue(data).await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to add plan", e)
        }
    }
    @Suppress("UNCHECKED_CAST")
    suspend fun getPlansForUser(userId: String): List<Map<String, Any>> {
        return try {
            val snapshot = db.child("plans").orderByChild("userId").equalTo(userId).get().await()
            val result = mutableListOf<Map<String, Any>>()
            for (child in snapshot.children) {
                val map = child.getValue(Map::class.java) as? Map<String, Any>
                if (map != null) result.add(map)
            }
            result
        } catch (e: Exception) {
            throw RepositoryException("Failed to get plans", e)
        }
    }
    suspend fun updatePlan(planId: String, data: Map<String, Any>) {
        try {
            db.child("plans").child(planId).updateChildren(data).await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to update plan", e)
        }
    }

    // Reviews
    suspend fun addReview(reviewId: String, data: Map<String, Any>) {
        try {
            db.child("reviews").child(reviewId).setValue(data).await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to add review", e)
        }
    }
    @Suppress("UNCHECKED_CAST")
    suspend fun getReviewsForPlace(placeId: String): List<Map<String, Any>> {
        return try {
            val snapshot = db.child("reviews").orderByChild("placeId").equalTo(placeId).get().await()
            val result = mutableListOf<Map<String, Any>>()
            for (child in snapshot.children) {
                val map = child.getValue(Map::class.java) as? Map<String, Any>
                if (map != null) result.add(map)
            }
            result
        } catch (e: Exception) {
            throw RepositoryException("Failed to get reviews", e)
        }
    }

    // Bookmarks (under user)
    suspend fun addBookmark(userId: String, bookmarkId: String, data: Map<String, Any>) {
        try {
            db.child("users").child(userId).child("bookmarks").child(bookmarkId).setValue(data).await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to add bookmark", e)
        }
    }
    @Suppress("UNCHECKED_CAST")
    suspend fun getBookmarks(userId: String): List<Map<String, Any>> {
        return try {
            val snapshot = db.child("users").child(userId).child("bookmarks").get().await()
            val result = mutableListOf<Map<String, Any>>()
            for (child in snapshot.children) {
                val map = child.getValue(Map::class.java) as? Map<String, Any>
                if (map != null) result.add(map)
            }
            result
        } catch (e: Exception) {
            throw RepositoryException("Failed to get bookmarks", e)
        }
    }
    suspend fun removeBookmark(userId: String, bookmarkId: String) {
        try {
            db.child("users").child(userId).child("bookmarks").child(bookmarkId).removeValue().await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to remove bookmark", e)
        }
    }
}
