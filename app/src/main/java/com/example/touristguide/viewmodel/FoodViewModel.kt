package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.PlaceCategory
import com.example.touristguide.data.model.Place
import com.example.touristguide.data.network.GeoapifyApiService
import com.example.touristguide.data.network.PixabayService
import com.example.touristguide.data.repository.GeoapifyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodViewModel : ViewModel() {
    private val apiKey = "6acbf75b57b74b749fd87b61351b7c77"
    private val apiService = Retrofit.Builder()
        .baseUrl("https://api.geoapify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeoapifyApiService::class.java)
    private val repository = GeoapifyRepository(apiService, apiKey)
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedRestaurant = MutableStateFlow<Place?>(null)
    val selectedRestaurant: StateFlow<Place?> = _selectedRestaurant

    private val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    private val pixabayService: PixabayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayService::class.java)
    }
    private val imageCache = mutableMapOf<String, String?>()

    fun selectRestaurant(place: Place) {
        _selectedRestaurant.value = place
    }

    fun fetchNearbyRestaurants(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = repository.searchPlaces(
                    category = PlaceCategory.RESTAURANTS,
                    location = org.osmdroid.util.GeoPoint(lat, lon)
                )
                val placesWithImages = results.map { place ->
                    val imageUrl = imageCache[place.name.orEmpty()]
                    if (imageUrl != null) {
                        place.copy(imageUrl = imageUrl)
                    } else {
                        val query = buildString {
                            append(place.name.orEmpty())
                            append(" Food Nepal")
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
                _places.value = placesWithImages
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

