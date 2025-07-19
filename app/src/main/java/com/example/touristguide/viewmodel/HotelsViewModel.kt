package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Hotel
import com.example.touristguide.data.repository.HotelRepository
import com.example.touristguide.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.touristguide.utils.Constants
import com.example.touristguide.data.network.PixabayService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HotelsViewModel : ViewModel() {
    private val apiKey = "6acbf75b57b74b749fd87b61351b7c77"
    private val apiService = Retrofit.Builder()
        .baseUrl("https://api.geoapify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(com.example.touristguide.data.network.GeoapifyApiService::class.java)
    private val repository = HotelRepository(apiService, apiKey)
    private val firebaseRepository = FirebaseRepository()

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Filters
    private val _radius = MutableStateFlow(5000)
    val radius: StateFlow<Int> = _radius
    private val _openNow = MutableStateFlow(false)
    val openNow: StateFlow<Boolean> = _openNow
    private val _websiteOnly = MutableStateFlow(false)
    val websiteOnly: StateFlow<Boolean> = _websiteOnly
    private val _minRating = MutableStateFlow(0)
    val minRating: StateFlow<Int> = _minRating
    private val _priceLevel = MutableStateFlow<String?>(null)
    val priceLevel: StateFlow<String?> = _priceLevel
    private val _amenities = MutableStateFlow<List<String>>(emptyList())
    val amenities: StateFlow<List<String>> = _amenities

    private val _selectedHotelId = MutableStateFlow<String?>(null)
    val selectedHotelId: StateFlow<String?> = _selectedHotelId.asStateFlow()
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var allNepalHotels: List<Hotel> = emptyList()

    private val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    private val pixabayService: PixabayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayService::class.java)
    }
    private val imageCache = mutableMapOf<String, String?>()

    fun setRadius(value: Int) { _radius.value = value }
    fun setOpenNow(value: Boolean) { _openNow.value = value }
    fun setWebsiteOnly(value: Boolean) { _websiteOnly.value = value }
    fun setMinRating(value: Int) { _minRating.value = value }
    fun setPriceLevel(value: String?) { _priceLevel.value = value }
    fun setAmenities(value: List<String>) { _amenities.value = value }

    fun selectHotel(hotelId: String) {
        _selectedHotelId.value = hotelId
        checkFavoriteStatus(hotelId)
    }

    fun checkFavoriteStatus(hotelId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _isFavorite.value = firebaseRepository.isHotelFavorite(userId, hotelId)
        }
    }

    fun saveHotelToFavorites(hotel: Hotel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            firebaseRepository.saveHotelToFavorites(userId, hotel)
            _isFavorite.value = true
        }
    }

    fun removeHotelFromFavorites(hotelId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            firebaseRepository.removeHotelFromFavorites(userId, hotelId)
            _isFavorite.value = false
        }
    }

    fun fetchAllNepalHotels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = repository.getNearbyHotels(
                    location = null, // null to use Nepal default
                    radius = 1000000, // Large radius to cover Nepal
                    limit = 1000
                )
                allNepalHotels = results
                updateNearbyHotels(Constants.NEPAL_LAT, Constants.NEPAL_LON)
            } catch (e: Exception) {
                _error.value = e.message
                _hotels.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateNearbyHotels(userLat: Double, userLon: Double, radiusKm: Double = 10.0) {
        val nearby = allNepalHotels.map {
            it.copy(
                // Add distance calculation if Hotel model supports it
            )
        } // Optionally filter by distance if you add a distance field
        _hotels.value = nearby
    }

    fun searchHotels(query: String, userLat: Double, userLon: Double, radiusKm: Double = 10.0) {
        val results = allNepalHotels.filter {
            it.name.contains(query, ignoreCase = true)
            // Optionally add distance filter if Hotel model supports it
        }
        _hotels.value = results
    }

    fun fetchHotels(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = repository.getNearbyHotels(
                    location = GeoPoint(lat, lon),
                    radius = _radius.value,
                    openNow = _openNow.value,
                    websiteOnly = _websiteOnly.value,
                    minRating = _minRating.value,
                    priceLevel = _priceLevel.value,
                    amenities = _amenities.value,
                    limit = 30
                )
                val hotelsWithImages = results.map { hotel ->
                    val imageUrl = imageCache[hotel.name]
                    if (imageUrl != null) {
                        hotel.copy(imageUrl = imageUrl)
                    } else {
                        val query = buildString {
                            append(hotel.name)
                            append(" Hotel Nepal")
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
                        imageCache[hotel.name] = url
                        hotel.copy(imageUrl = url)
                    }
                }
                _hotels.value = hotelsWithImages
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getHotelById(hotelId: String): Hotel? {
        return hotels.value.find { it.id == hotelId }
    }

    suspend fun getEnrichedHotelById(hotelId: String): Hotel? {
        val baseHotel = getHotelById(hotelId) ?: return null
        val enrichment = firebaseRepository.getHotelEnrichment(hotelId)
        if (enrichment == null) return baseHotel
        return baseHotel.copy(
            phone = enrichment["phone"] as? String ?: baseHotel.phone,
            website = enrichment["website"] as? String ?: baseHotel.website,
            amenities = (enrichment["amenities"] as? List<*>)?.mapNotNull { it as? String } ?: baseHotel.amenities,
            // Add more fields as needed
        )
    }
}
