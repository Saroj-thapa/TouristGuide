package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Trekking
import com.example.touristguide.data.repository.TrekkingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.touristguide.utils.Constants
import android.util.Log
import com.example.touristguide.data.network.PixabayService
import kotlinx.coroutines.Dispatchers
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

    private var allNepalTreks: List<Trekking> = emptyList()

    private val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    private val pixabayService: PixabayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayService::class.java)
    }
    private val imageCache = mutableMapOf<String, String?>()

    fun fetchAllNepalTreks(repository: TrekkingRepository) {
        viewModelScope.launch {
            try {
                val treks = repository.getTreksInNepal()
                val treksWithImages = treks.map { trek ->
                    val imageUrl = imageCache[trek.name]
                    if (imageUrl != null) {
                        trek.copy(images = listOf(imageUrl))
                    } else {
                        val query = buildString {
                            append(trek.name)
                            append(" Trek Nepal")
                        }
                        val url = withContext(Dispatchers.IO) {
                            try {
                                val response = pixabayService.searchImages(
                                    apiKey = pixabayApiKey,
                                    query = query
                                )
                                response.hits.firstOrNull()?.webformatURL
                            } catch (e: Exception) {
                                null
                            }
                        }
                        imageCache[trek.name] = url
                        trek.copy(images = if (url != null) listOf(url) else emptyList())
                    }
                }
                allNepalTreks = treksWithImages
                _treks.value = allNepalTreks
                // updateNearbyTreks(Constants.NEPAL_LAT, Constants.NEPAL_LON) // skip filtering for debug
            } catch (e: Exception) {
                Log.e("TrekkingViewModel", "Error fetching treks", e)
                _treks.value = emptyList()
            }
        }
    }

    fun updateNearbyTreks(userLat: Double, userLon: Double, radiusKm: Double = 1000.0) {
        Log.d("TrekkingViewModel", "Total fetched: ${allNepalTreks.size}")
        val nearby = allNepalTreks.map {
            it.copy(distanceKm = calculateDistance(userLat, userLon, it.stops.firstOrNull()?.lat ?: userLat, it.stops.firstOrNull()?.lon ?: userLon))
        }.filter { it.distanceKm <= radiusKm }
         .sortedBy { it.distanceKm }
        Log.d("TrekkingViewModel", "Nearby found: ${nearby.size}")
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
        val R = 6371.0 // Radius of Earth in km
        val dLat = (lat2 - lat1) * Math.PI / 180.0
        val dLon = (lon2 - lon1) * Math.PI / 180.0
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180.0) * Math.cos(lat2 * Math.PI / 180.0) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = R * c // Distance in km
        return distance
            }
        }
