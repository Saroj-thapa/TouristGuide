package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Place
import com.example.touristguide.data.model.PlaceCategory
import com.example.touristguide.data.network.GeoapifyApiService
import com.example.touristguide.data.network.PixabayService
import com.example.touristguide.data.repository.GeoapifyRepository
import com.example.touristguide.data.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HospitalViewModel : ViewModel() {
    private val apiKey = "6acbf75b57b74b749fd87b61351b7c77"
    private val apiService = Retrofit.Builder()
        .baseUrl("https://api.geoapify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeoapifyApiService::class.java)
    private val repository = GeoapifyRepository(apiService, apiKey)
    private val firebaseRepository = FirebaseRepository()
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Search and filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    private val _selectedRegion = MutableStateFlow("")
    val selectedRegion: StateFlow<String> = _selectedRegion
    private val _regions = MutableStateFlow<List<String>>(emptyList())
    val regions: StateFlow<List<String>> = _regions

    // Bookmarks (hospital name as id for simplicity)
    private val _bookmarks = MutableStateFlow<Set<String>>(setOf())
    val bookmarks: StateFlow<Set<String>> = _bookmarks

    // Filtered hospitals
    val filteredHospitals = MutableStateFlow<List<Place>>(emptyList())

    private val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    private val pixabayService: PixabayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayService::class.java)
    }
    private val imageCache = mutableMapOf<String, String?>()

    fun fetchHospitalLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = repository.searchPlaces(
                    category = PlaceCategory.HOSPITALS,
                    location = org.osmdroid.util.GeoPoint(lat, lon)
                )
                val hospitalsWithImages = results.map { place ->
                    val imageUrl = imageCache[place.name.orEmpty()]
                    if (imageUrl != null) {
                        place.copy(imageUrl = imageUrl)
                    } else {
                        val query = buildString {
                            append(place.name.orEmpty())
                            append(" Hospital Nepal")
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
                        imageCache[place.name.orEmpty()] = url
                        place.copy(imageUrl = url)
                    }
                }
                _places.value = hospitalsWithImages
                // Extract unique regions (city/district)
                val regionSet = hospitalsWithImages.mapNotNull { it.address?.split(",")?.getOrNull(1)?.trim() }.distinct()
                _regions.value = regionSet
                applyFilters()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun setSelectedRegion(region: String) {
        _selectedRegion.value = region
        applyFilters()
    }

    private fun applyFilters() {
        val query = _searchQuery.value.trim().lowercase()
        val region = _selectedRegion.value.trim().lowercase()
        filteredHospitals.value = _places.value.filter { place ->
            (query.isBlank() || place.name?.lowercase()?.contains(query) == true || place.address?.lowercase()?.contains(query) == true) &&
            (region.isBlank() || place.address?.lowercase()?.contains(region) == true)
        }
    }

    // Bookmarking
    fun bookmarkHospital(place: Place) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val bookmarkId = place.name ?: "" // Use name as id for now
                val data = mapOf(
                    "name" to (place.name ?: ""),
                    "address" to (place.address ?: ""),
                    "latitude" to place.latitude,
                    "longitude" to place.longitude,
                    "category" to place.category.displayName
                )
                firebaseRepository.addBookmark(userId, bookmarkId, data)
                _bookmarks.value = _bookmarks.value + bookmarkId
            } catch (_: Exception) {}
        }
    }
    fun removeBookmark(place: Place) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val bookmarkId = place.name ?: ""
                firebaseRepository.removeBookmark(userId, bookmarkId)
                _bookmarks.value = _bookmarks.value - bookmarkId
            } catch (_: Exception) {}
        }
    }
    fun loadBookmarks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val bookmarksList = firebaseRepository.getBookmarks(userId)
                val ids = bookmarksList.mapNotNull { it["name"] as? String }.toSet()
                _bookmarks.value = ids
            } catch (_: Exception) {}
        }
    }
}
