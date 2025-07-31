package com.example.touristguide.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Place
import com.example.touristguide.data.model.PlaceCategory
import com.example.touristguide.data.repository.GeoapifyRepository
import com.example.touristguide.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint
import kotlinx.coroutines.launch
import com.example.touristguide.utils.Constants
import android.util.Log
import com.example.touristguide.data.network.PixabayService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlacesViewModel(
    private val repository: GeoapifyRepository,
    private val defaultCategory: PlaceCategory = PlaceCategory.TOURIST_ATTRACTIONS,
    private val firebaseRepository: FirebaseRepository = FirebaseRepository(),
    private val userId: String = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
) : ViewModel() {
    private var currentOffset = 0
    private val pageSize = 20
    private var lastLat: Double = 28.3949
    private var lastLon: Double = 84.1240
    private var lastRadius: Int = 50000
    private var hasMoreData = true
    private val _searchResults = mutableStateOf<List<Place>>(emptyList())
    val searchResults: State<List<Place>> = _searchResults
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _selectedPlace = mutableStateOf<Place?>(null)
    val selectedPlace: State<Place?> = _selectedPlace
    // Always use TOURIST_ATTRACTIONS
    private val _currentCategory = mutableStateOf(PlaceCategory.TOURIST_ATTRACTIONS)
    val currentCategory: State<PlaceCategory> = _currentCategory
    private val _currentLocation = mutableStateOf<GeoPoint?>(null)
    val currentLocation: State<GeoPoint?> = _currentLocation
    // Remove/ignore categoryFilter
    private val _bookmarkedPlaces = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedPlaces: StateFlow<Set<String>> = _bookmarkedPlaces
    private var placesCache: Map<String, List<Place>> = emptyMap()
    private var allNepalPlaces: List<Place> = emptyList()
    // Add these properties for Pixabay
    private val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    private val pixabayService: PixabayService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayService::class.java)
    }

    init {
        _currentLocation.value = GeoPoint(27.7172, 85.3240)
        searchPlaces()
    }

    fun setCurrentLocation(latitude: Double, longitude: Double) {
        _currentLocation.value = GeoPoint(latitude, longitude)
    }

    // Remove selectCategory
    fun selectPlace(place: Place) {
        _selectedPlace.value = place
    }

    // Remove setCategoryFilter
    fun searchPlaces(query: String = "") {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val category = PlaceCategory.TOURIST_ATTRACTIONS
                val cacheKey = "${category.name}_${_currentLocation.value?.latitude}_${_currentLocation.value?.longitude}"
                val cached = placesCache[cacheKey]
                val places = if (cached != null) {
                    cached
                } else {
                    val result = repository.searchPlaces(
                        category = category,
                        location = _currentLocation.value
                    )
                    placesCache = placesCache + (cacheKey to result)
                    result
                }
                // Fetch images from Pixabay for each place
                val placesWithImages = places.map { place ->
                    val imageUrl = try {
                        val pixabayQuery = buildString {
                            append(place.name ?: "")
                            if (!place.category.displayName.isNullOrBlank()) {
                                append(" ")
                                append(place.category.displayName)
                            }
                            append(" Nepal")
                        }
                        val response = pixabayService.searchImages(
                            apiKey = pixabayApiKey,
                            query = pixabayQuery
                        )
                        response.hits.firstOrNull()?.webformatURL
                    } catch (e: Exception) {
                        null
                    }
                    place.copy(imageUrl = imageUrl)
                }
                _searchResults.value = placesWithImages
                if (placesWithImages.isNotEmpty()) {
                    _selectedPlace.value = placesWithImages.first()
                }
                loadBookmarks()
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchResults(places: List<Place>) {
        _searchResults.value = places
        if (places.isNotEmpty()) {
            _selectedPlace.value = places.first()
        }
        _isLoading.value = false
    }

    fun searchHeritageAndTouristAttractions(
        geoapifyService: com.example.touristguide.data.network.GeoapifyApiService,
        apiKey: String,
        lat: Double,
        lon: Double
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val categories = "heritage.unesco,heritage,tourism.sights.fort,tourism.sights.castle,tourism.sights.ruines,tourism.sights.archaeological_site,tourism.sights.monastery,tourism.sights.place_of_worship.church,tourism.sights.place_of_worship.temple,tourism.sights.place_of_worship.mosque,tourism.sights.lighthouse,tourism.sights.tower,natural.mountain.peak"
                val rectFilter = "rect:85.2805,27.7405,85.3605,27.6705"
                val response = geoapifyService.searchPlaces(
                    categories = categories,
                    filter = rectFilter,
                    bias = "proximity:$lon,$lat",
                    limit = 500,
                    apiKey = apiKey
                )
                val places = response.features.mapNotNull { feature ->
                    val props = feature.properties
                    if (props.name != null) {
                        Place(
                            id = "${props.name}_${props.lat}_${props.lon}",
                            name = props.name,
                            address = props.formatted,
                            category = PlaceCategory.TOURIST_ATTRACTIONS,
                            latitude = props.lat,
                            longitude = props.lon
                        )
                    } else null
                }
                // Optionally, sort by distance (calculate locally)
                val sorted = places.sortedBy { calculateDistance(lat, lon, it.latitude, it.longitude) }
                updateSearchResults(sorted)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
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

    fun loadBookmarks() {
        viewModelScope.launch {
            try {
                val bookmarks = firebaseRepository.getBookmarks(userId)
                val ids = bookmarks.mapNotNull { it["name"] as? String }.toSet()
                _bookmarkedPlaces.value = ids
            } catch (_: Exception) {}
        }
    }

    fun isPlaceBookmarked(place: Place): Boolean {
        return _bookmarkedPlaces.value.contains(place.name)
    }

    fun savePlace(place: Place) {
        viewModelScope.launch {
            try {
                val bookmarkId = place.id // always non-null and unique
                val data: Map<String, Any> = mapOf(
                    "name" to (place.name ?: ""),
                    "address" to (place.address ?: ""),
                    "lat" to place.latitude,
                    "lon" to place.longitude,
                    "category" to place.category.name,
                    "savedAt" to System.currentTimeMillis()
                )
                firebaseRepository.addBookmark(userId, bookmarkId, data)
                loadBookmarks()
            } catch (_: Exception) {}
        }
    }

    fun unsavePlace(place: Place) {
        viewModelScope.launch {
            try {
                val bookmarkId = place.id
                firebaseRepository.removeBookmark(userId, bookmarkId)
                loadBookmarks()
            } catch (_: Exception) {}
        }
    }

    fun fetchAllNepalPlaces(geoapifyService: com.example.touristguide.data.network.GeoapifyApiService, apiKey: String) {
        viewModelScope.launch {
            try {
                val categories = "heritage.unesco,heritage,tourism.sights.fort,tourism.sights.castle,tourism.sights.ruines,tourism.sights.archaeological_site,tourism.sights.monastery,tourism.sights.place_of_worship.church,tourism.sights.place_of_worship.temple,tourism.sights.place_of_worship.mosque,tourism.sights.lighthouse,tourism.sights.tower,natural.mountain.peak"
                val rectFilter = "rect:80.0586,26.347,88.2015,30.447" // Nepal bounds
                val response = geoapifyService.searchPlaces(
                    categories = categories,
                    filter = rectFilter,
                    bias = "",
                    limit = 1000,
                    apiKey = apiKey
                )
                allNepalPlaces = response.features.mapNotNull { feature ->
                    val props = feature.properties
                    if (props.name != null) {
                        Place(
                            id = "${props.name}_${props.lat}_${props.lon}",
                            name = props.name,
                            address = props.formatted,
                            category = PlaceCategory.TOURIST_ATTRACTIONS,
                            latitude = props.lat,
                            longitude = props.lon
                        )
                    } else null
                }
                // Optionally, update UI with nearby places
                updateNearbyPlaces(Constants.NEPAL_LAT, Constants.NEPAL_LON)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun updateNearbyPlaces(userLat: Double, userLon: Double, radiusKm: Double = 10.0) {
        val nearby = allNepalPlaces.filter {
            calculateDistance(userLat, userLon, it.latitude, it.longitude) <= radiusKm
        }.sortedBy { calculateDistance(userLat, userLon, it.latitude, it.longitude) }
        _searchResults.value = nearby
    }

    fun searchPlaces(query: String, userLat: Double, userLon: Double, radiusKm: Double = 10.0) {
        val results = allNepalPlaces.filter {
            it.name?.contains(query, ignoreCase = true) == true &&
            calculateDistance(userLat, userLon, it.latitude, it.longitude) <= radiusKm
        }.sortedBy { calculateDistance(userLat, userLon, it.latitude, it.longitude) }
        _searchResults.value = results
    }

    fun fetchTouristPlacesForNepal() {
        viewModelScope.launch {
            try {
                val majorCities = listOf(
                    Pair(27.7172, 85.3240), // Kathmandu
                    Pair(28.2096, 83.9856), // Pokhara
                    Pair(27.6761, 85.4298), // Bhaktapur
                    Pair(27.6841, 85.3188), // Lalitpur
                    Pair(28.3949, 84.1240), // Central Nepal
                    Pair(26.4837, 87.2832)  // Biratnagar
                )
                val places = repository.fetchTouristPlacesMultipleCities(
                    points = majorCities,
                    radius = 20000,
                    limitPerCity = 50
                )
                Log.d("PlacesViewModel", "Fetched tourist places: ${places.size}")
                _searchResults.value = places
            } catch (e: Exception) {
                Log.e("PlacesViewModel", "Error fetching tourist places", e)
                _searchResults.value = emptyList()
            }
        }
    }

    fun fetchTouristPlacesForWholeNepal() {
        viewModelScope.launch {
            try {
                val places = repository.fetchTouristPlacesForWholeNepal(limit = 500)
                Log.d("PlacesViewModel", "Fetched tourist places (whole Nepal): ${places.size}")
                _searchResults.value = places
            } catch (e: Exception) {
                Log.e("PlacesViewModel", "Error fetching tourist places (whole Nepal)", e)
                _searchResults.value = emptyList()
            }
        }
    }

    fun fetchTouristPlacesByLatLon(
        lat: Double,
        lon: Double,
        radius: Int = 100000,
        limit: Int = 500,
        offset: Int = 0,
        websiteOnly: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val places = repository.fetchTouristPlacesByLatLon(lat, lon, radius, limit, offset, websiteOnly)
                Log.d("PlacesViewModel", "Fetched tourist places by lat/lon: ${places.size}")
                _searchResults.value = places
            } catch (e: Exception) {
                Log.e("PlacesViewModel", "Error fetching tourist places by lat/lon", e)
                _searchResults.value = emptyList()
            }
        }
    }

    fun fetchTouristPlacesByLatLonPaged(
        lat: Double,
        lon: Double,
        radius: Int = 100000,
        pageSize: Int = 500,
        pages: Int = 2,
        websiteOnly: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val places = repository.fetchTouristPlacesByLatLonPaged(lat, lon, radius, pageSize, pages)
                Log.d("PlacesViewModel", "Fetched paged tourist places: ${places.size}")
                _searchResults.value = places
            } catch (e: Exception) {
                Log.e("PlacesViewModel", "Error fetching paged tourist places", e)
                _searchResults.value = emptyList()
            }
        }
    }

    fun loadMoreTouristPlaces(
        lat: Double,
        lon: Double,
        radius: Int = 100000,
        currentCount: Int,
        pageSize: Int = 500,
        websiteOnly: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val morePlaces = repository.fetchTouristPlacesByLatLon(lat, lon, radius, pageSize, currentCount, websiteOnly)
                val merged = (_searchResults.value + morePlaces).distinctBy { it.name + "_" + it.latitude + "_" + it.longitude }
                Log.d("PlacesViewModel", "Loaded more tourist places: ${morePlaces.size}, total: ${merged.size}")
                _searchResults.value = merged
            } catch (e: Exception) {
                Log.e("PlacesViewModel", "Error loading more tourist places", e)
            }
        }
    }

    fun fetchInitialPlaces(lat: Double, lon: Double, radius: Int = 50000) {
        currentOffset = 0
        lastLat = lat
        lastLon = lon
        lastRadius = radius
        hasMoreData = true
        _searchResults.value = emptyList()
        fetchMorePlaces()
    }

    fun fetchMorePlaces() {
        if (!hasMoreData) return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newPlaces = repository.fetchTouristPlacesByLatLon(
                    lat = lastLat,
                    lon = lastLon,
                    radius = lastRadius,
                    limit = pageSize,
                    offset = currentOffset
                )
                val merged = (_searchResults.value + newPlaces).distinctBy { it.name + it.latitude + it.longitude }
                _searchResults.value = merged
                currentOffset += newPlaces.size
                hasMoreData = newPlaces.size == pageSize
                Log.d("PlacesViewModel", "Fetched "+newPlaces.size+", total: "+merged.size+", hasMoreData: "+hasMoreData)
            } catch (e: Exception) {
                Log.e("PlacesViewModel", "Error: "+e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add this public getter
    val hasMore: Boolean
        get() = hasMoreData
}
