package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.repository.FirebaseRepository
import com.example.touristguide.data.repository.RepositoryException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FirebaseViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {
    private val _userPlans = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val userPlans: StateFlow<List<Map<String, Any>>> = _userPlans

    private val _bookmarks = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val bookmarks: StateFlow<List<Map<String, Any>>> = _bookmarks

    private val _placeReviews = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val placeReviews: StateFlow<List<Map<String, Any>>> = _placeReviews

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun loadUserPlans(userId: String) {
        viewModelScope.launch {
            try {
                _userPlans.value = repository.getPlansForUser(userId)
                _errorState.value = null
            } catch (e: RepositoryException) {
                _errorState.value = e.message
            }
        }
    }
    fun savePlan(planId: String, data: Map<String, Any>) {
        viewModelScope.launch {
            try {
                repository.addPlan(planId, data)
                loadUserPlans(data["userId"] as String)
                _errorState.value = null
            } catch (e: RepositoryException) {
                _errorState.value = e.message
            }
        }
    }
    fun loadBookmarks(userId: String) {
        viewModelScope.launch {
            try {
                _bookmarks.value = repository.getBookmarks(userId)
                _errorState.value = null
            } catch (e: RepositoryException) {
                _errorState.value = e.message
            }
        }
    }
    fun addBookmark(userId: String, bookmarkId: String, data: Map<String, Any>) {
        viewModelScope.launch {
            try {
                repository.addBookmark(userId, bookmarkId, data)
                loadBookmarks(userId)
                _errorState.value = null
            } catch (e: RepositoryException) {
                _errorState.value = e.message
            }
        }
    }
    fun removeBookmark(userId: String, bookmarkId: String) {
        viewModelScope.launch {
            try {
                repository.removeBookmark(userId, bookmarkId)
                loadBookmarks(userId)
                _errorState.value = null
            } catch (e: RepositoryException) {
                _errorState.value = e.message
            }
        }
    }
    fun loadPlaceReviews(placeId: String) {
        viewModelScope.launch {
            try {
                _placeReviews.value = repository.getReviewsForPlace(placeId)
                _errorState.value = null
            } catch (e: RepositoryException) {
                _errorState.value = e.message
            }
        }
    }
    fun saveReview(reviewId: String, data: Map<String, Any>) {
        viewModelScope.launch {
            try {
                repository.addReview(reviewId, data)
                loadPlaceReviews(data["placeId"] as String)
                _errorState.value = null
            } catch (e: RepositoryException) {
                _errorState.value = e.message
            }
        }
    }
} 