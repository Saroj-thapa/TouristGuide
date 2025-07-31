package com.example.touristguide.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.google.firebase.database.GenericTypeIndicator
import com.example.touristguide.data.model.Hotel

class RepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)

class FirebaseRepository {
    private val db = FirebaseDatabase.getInstance().reference

    // User Profile
    suspend fun addUser(userId: String, data: Map<String, Any>) {
        try {
            db.child("users").child(userId).setValue(data).await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to add user", e)
        }
    }
    @Suppress("UNCHECKED_CAST")
    suspend fun getUser(userId: String): Map<String, Any>? {
        return try {
            val snapshot = db.child("users").child(userId).get().await()
            val genericTypeIndicator = object : GenericTypeIndicator<Map<String, Any>>() {}
            snapshot.getValue(genericTypeIndicator)
        } catch (e: Exception) {
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
    /**
     * Adds a bookmark for a user.
     * @param userId The user's UID
     * @param bookmarkId The unique ID for the bookmark (e.g., place.id or place.name)
     * @param data Map of bookmark fields (e.g., name, address, latitude, longitude, category, etc.)
     * Usage: addBookmark(userId, bookmarkId, mapOf("name" to ..., ...))
     */
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

    // Hotel enrichment
    @Suppress("UNCHECKED_CAST")
    suspend fun getHotelEnrichment(hotelId: String): Map<String, Any>? {
        return try {
            val snapshot = db.child("hotels").child(hotelId).get().await()
            val genericTypeIndicator = object : GenericTypeIndicator<Map<String, Any>>() {}
            snapshot.getValue(genericTypeIndicator)
        } catch (e: Exception) {
            null
        }
    }

    // Save hotel to user's saved_hotels
    suspend fun saveHotelToFavorites(userId: String, hotel: Hotel) {
        try {
            val data = mapOf(
                "name" to hotel.name,
                "lat" to hotel.lat,
                "lon" to hotel.lon,
                "savedAt" to System.currentTimeMillis()
            )
            db.child("users").child(userId).child("saved_hotels").child(hotel.id).setValue(data).await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to save hotel to favorites", e)
        }
    }
    // Remove hotel from user's saved_hotels
    suspend fun removeHotelFromFavorites(userId: String, hotelId: String) {
        try {
            db.child("users").child(userId).child("saved_hotels").child(hotelId).removeValue().await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to remove hotel from favorites", e)
        }
    }
    // Check if hotel is in user's saved_hotels
    suspend fun isHotelFavorite(userId: String, hotelId: String): Boolean {
        return try {
            val snapshot = db.child("users").child(userId).child("saved_hotels").child(hotelId).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateProfileImageUrl(userId: String, url: String) {
        try {
            db.child("users").child(userId).child("profileImageUrl").setValue(url).await()
        } catch (e: Exception) {
            throw RepositoryException("Failed to update profile image URL", e)
        }
    }
}
