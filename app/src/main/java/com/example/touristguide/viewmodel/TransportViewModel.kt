package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Place
import com.example.touristguide.data.network.GeoapifyApiService
import com.example.touristguide.data.repository.GeoapifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.touristguide.utils.Constants
import com.example.touristguide.data.network.PixabayService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransportViewModel : ViewModel() {
    private val apiKey = "6acbf75b57b74b749fd87b61351b7c77"
    private val apiService = Retrofit.Builder()
        .baseUrl("https://api.geoapify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeoapifyApiService::class.java)
    private val repository = GeoapifyRepository(apiService, apiKey)

    val filterOptions = listOf(
        "Bus", "Taxi", "Parking", "Fuel", "Airport"
    )
    private val filterToCategory = mapOf(
        "Bus" to "public_transport.bus",
        "Taxi" to "service.taxi",
        "Parking" to "service.vehicle.parking",
        "Fuel" to "service.vehicle.fuel",
        "Airport" to "aeroway.aerodrome"
    )
    private val _selectedFilter = MutableStateFlow("Bus")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    var searchQuery = MutableStateFlow("")

    private var allNepalTransportPlaces: List<Place> = emptyList()

    private val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    private val pixabayService: PixabayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayService::class.java)
    }
    private val imageCache = mutableMapOf<String, String?>()

    fun fetchAllNepalTransportPlaces() {
        viewModelScope.launch {
            try {
                val results = repository.searchTransportPlaces(
                    categories = filterToCategory["All"] ?: "public_transport.bus,service.taxi,service.vehicle.parking,service.vehicle.rental,service.vehicle.fuel,aeroway.aerodrome",
                    location = null // null to use Nepal default
                )
                allNepalTransportPlaces = results
                updateNearbyTransportPlaces(Constants.NEPAL_LAT, Constants.NEPAL_LON)
            } catch (e: Exception) {
                _places.value = emptyList()
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return Math.round(r * c * 10) / 10.0
    }

    fun updateNearbyTransportPlaces(userLat: Double, userLon: Double, radiusKm: Double = 10.0) {
        val nearby = allNepalTransportPlaces.filter {
            calculateDistance(userLat, userLon, it.latitude, it.longitude) <= radiusKm
        }.sortedBy { calculateDistance(userLat, userLon, it.latitude, it.longitude) }
        _places.value = nearby
    }

    fun searchTransportPlaces(query: String, userLat: Double, userLon: Double, radiusKm: Double = 10.0) {
        val results = allNepalTransportPlaces.filter {
            it.name.orEmpty().contains(query, ignoreCase = true) && calculateDistance(userLat, userLon, it.latitude, it.longitude) <= radiusKm
        }.sortedBy { calculateDistance(userLat, userLon, it.latitude, it.longitude) }
        _places.value = results
    }

    fun updateFilter(newFilter: String, lat: Double, lon: Double) {
        _selectedFilter.value = newFilter
        fetchNearbyTransport(lat, lon, newFilter)
    }

    fun fetchNearbyTransport(lat: Double, lon: Double, filter: String = "All", name: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val categories = filterToCategory[filter] ?: filterToCategory["All"]!!
                val results = try {
                    if (name.isNullOrBlank()) {
                        repository.searchTransportPlaces(
                            categories = categories,
                            location = GeoPoint(lat, lon)
                        )
                    } else {
                        repository.searchTransportPlacesWithName(
                            categories = categories,
                            location = GeoPoint(lat, lon),
                            name = name
                        )
                    }
                } catch (e: Exception) {
                    // Fallback for Parking if HTTP 400
                    if (filter == "Parking" && e.message?.contains("400") == true) {
                        repository.searchTransportPlaces(
                            categories = "parking",
                            location = GeoPoint(lat, lon)
                        )
                    } else if (filter == "Airport" && e.message?.contains("400") == true) {
                        repository.searchTransportPlaces(
                            categories = "aeroway",
                            location = GeoPoint(lat, lon)
                        )
                    } else {
                        throw e
                    }
                }
                val busKeywords = listOf(
                    "bus", "bus stop", "buspark", "bus station", "bus stand", "microbus", "micro bus",
                    "बस", "बसपार्क", "बस स्टप", "बस स्टेशन", "माइक्रोबस", "माइक्रो बस"
                )
                val taxiKeywords = listOf(
                    "taxi", "taxi stand", "cab", "taxi park", "taxi point",
                    "ट्याक्सी", "ट्याक्सी स्ट्यान्ड", "ट्याक्सी स्टप", "क्याब", "ट्याक्सी पार्क", "ट्याक्सी पोइन्ट"
                )
                val parkingKeywords = listOf(
                    "parking", "car park", "parking lot", "vehicle parking", "bike parking", "motorcycle parking",
                    "पार्किङ", "कार पार्क", "पार्किङ स्थल", "सवारी पार्किङ", "बाइक पार्किङ", "मोटरसाइकल पार्किङ"
                )
                val airportKeywords = listOf(
                    "airport", "aerodrome", "airfield", "domestic airport", "international airport", "terminal",
                    "विमानस्थल", "एयरपोर्ट", "आन्तरिक विमानस्थल", "अन्तर्राष्ट्रिय विमानस्थल", "टर्मिनल"
                )
                val rentalKeywords = listOf(
                    "car rental", "bike rental", "motorbike rental", "cycle rental", "bicycle rental",
                    "workshop", "repair", "service center",
                    "गाडी भाडा", "कार भाडा", "बाइक भाडा", "साइकल भाडा", "वर्कशप", "मर्मत", "सर्भिस सेन्टर"
                )
                val filteredResults = when (filter) {
                    "Bus" -> results.filter { place -> busKeywords.any { keyword -> place.name.orEmpty().contains(keyword, ignoreCase = true) } }
                    "Taxi" -> results.filter { place -> taxiKeywords.any { keyword -> place.name.orEmpty().contains(keyword, ignoreCase = true) } }
                    "Parking" -> results.filter { place -> parkingKeywords.any { keyword -> place.name.orEmpty().contains(keyword, ignoreCase = true) } }
                    "Airport" -> results.filter { place -> airportKeywords.any { keyword -> place.name.orEmpty().contains(keyword, ignoreCase = true) } }
                    "Rental" -> results.filter { place -> rentalKeywords.any { keyword -> place.name.orEmpty().contains(keyword, ignoreCase = true) } }
                    else -> results
                }
                val placesWithImages = filteredResults.map { place ->
                    val imageUrl = imageCache[place.name.orEmpty()]
                    if (imageUrl != null) {
                        place.copy(imageUrl = imageUrl)
                    } else {
                        val query = withContext(Dispatchers.IO) {
                            try {
                                val response = pixabayService.searchImages(
                                    apiKey = pixabayApiKey,
                                    query = "${place.name} Transport Nepal"
                                )
                                response.hits.firstOrNull()?.webformatURL
                            } catch (e: Exception) {
                                null
                            }
                        }
                        imageCache[place.name.orEmpty()] = query
                        place.copy(imageUrl = query)
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
    fun searchTransport(lat: Double, lon: Double, filter: String = "All", name: String) {
        fetchNearbyTransport(lat, lon, filter, name)
    }
}
