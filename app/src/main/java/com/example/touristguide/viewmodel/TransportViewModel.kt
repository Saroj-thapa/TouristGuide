package com.example.touristguide.viewmodel


import androidx.lifecycle.ViewModel
import com.example.touristguide.data.network.GeoapifyResponse
import com.example.touristguide.data.repository.GeoapifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Data class for transport info
data class TransportInfo(val route: String, val duration: String)

class TransportViewModel : ViewModel() {
    private val repository = GeoapifyRepository()
    private val _geoapifyData = MutableStateFlow<GeoapifyResponse?>(null)
    val geoapifyData: StateFlow<GeoapifyResponse?> = _geoapifyData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Add a dynamic transport list for UI
    private val _transports = MutableStateFlow<List<TransportInfo>>(
        listOf(
            TransportInfo("City A to City B", "Approx. 1 hour"),
            TransportInfo("City B to City C", "Approx. 45 minutes")
        )
    )
    val transports: StateFlow<List<TransportInfo>> = _transports

    fun fetchNearbyTransport(lat: Double, lon: Double) {
        _isLoading.value = true
        _error.value = null
        repository.getPlaceInfo(lat, lon).enqueue(object : Callback<GeoapifyResponse> {
            override fun onResponse(call: Call<GeoapifyResponse>, response: Response<GeoapifyResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _geoapifyData.value = response.body()
                } else {
                    _error.value = "No data found"
                }
            }
            override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
            }
        })
    }
}
