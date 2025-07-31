package com.example.touristguide.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Trekking
import com.example.touristguide.data.network.PixabayService
import com.example.touristguide.data.repository.TrekkingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrekkingViewModel(
    private val repository: TrekkingRepository
) : ViewModel() {

    private val _treks = MutableStateFlow<List<Trekking>>(emptyList())
    val treks: StateFlow<List<Trekking>> = _treks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() {
        _errorMessage.value = null
    }

    private var allNepalTreks: List<Trekking> = emptyList()
    private val imageCache = mutableMapOf<String, String?>()
    private val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"

    private val pixabayService: PixabayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayService::class.java)
    }

    fun fetchAllNepalTreks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val treks = repository.getTreksInNepal()
                val treksWithImages = treks.map { trek ->
                    val cachedImage = imageCache[trek.name]
                    if (cachedImage != null) {
                        trek.copy(images = listOf(cachedImage))
                    } else {
                        val imageQuery = "${trek.name} Trek Nepal"
                        val imageUrl = withContext(Dispatchers.IO) {
                            try {
                                val response = pixabayService.searchImages(
                                    apiKey = pixabayApiKey,
                                    query = imageQuery
                                )
                                response.hits.firstOrNull()?.webformatURL
                            } catch (e: Exception) {
                                _errorMessage.value = "Failed to load image for ${trek.name}"
                                null
                            }
                        }
                        imageCache[trek.name] = imageUrl
                        trek.copy(images = imageUrl?.let { listOf(it) } ?: emptyList())
                    }
                }
                allNepalTreks = treksWithImages
                _treks.value = allNepalTreks
            } catch (e: Exception) {
                Log.e("TrekkingViewModel", "Error fetching treks", e)
                _errorMessage.value = "Failed to fetch treks. Please check your connection."
                _treks.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateNearbyTreks(userLat: Double, userLon: Double, radiusKm: Double = 1000.0) {
        val nearby = allNepalTreks.map {
            it.copy(distanceKm = calculateDistance(userLat, userLon, it.stops.firstOrNull()?.lat ?: userLat, it.stops.firstOrNull()?.lon ?: userLon))
        }.filter { it.distanceKm <= radiusKm }
            .sortedBy { it.distanceKm }
        _treks.value = nearby
    }

    fun searchTreks(query: String, userLat: Double, userLon: Double, radiusKm: Double = 50.0) {
        val results = allNepalTreks.map {
            it.copy(distanceKm = calculateDistance(userLat, userLon, it.stops.firstOrNull()?.lat ?: userLat, it.stops.firstOrNull()?.lon ?: userLon))
        }.filter {
            it.name.contains(query, ignoreCase = true) && it.distanceKm <= radiusKm
        }.sortedBy { it.distanceKm }
        _treks.value = results
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
}
