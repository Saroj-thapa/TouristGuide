package com.example.touristguide.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Place
import com.example.touristguide.data.repository.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.touristguide.data.network.GeoapifyResponse
import com.example.touristguide.data.repository.GeoapifyRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlacesViewModel : ViewModel() {
    private val repository = PlacesRepository()
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Geoapify API support for RestaurantDetailScreen
    private val geoapifyRepository = GeoapifyRepository()
    private val _geoapifyData = MutableStateFlow<GeoapifyResponse?>(null)
    val geoapifyData: StateFlow<GeoapifyResponse?> = _geoapifyData

    fun fetchPlaceInfo(lat: Double, lon: Double) {
        geoapifyRepository.getPlaceInfo(lat, lon).enqueue(object : Callback<GeoapifyResponse> {
            override fun onResponse(call: Call<GeoapifyResponse>, response: Response<GeoapifyResponse>) {
                if (response.isSuccessful) {
                    _geoapifyData.value = response.body()
                } else {
                    _geoapifyData.value = null
                }
            }
            override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                _geoapifyData.value = null
            }
        })
    }

    fun fetchTouristPlaces(lat: Double, lon: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.fetchTouristPlaces(lat, lon) { result ->
                _places.value = result
                _isLoading.value = false
            }
        }
    }
}
