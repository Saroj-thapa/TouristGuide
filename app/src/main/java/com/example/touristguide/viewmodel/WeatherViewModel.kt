package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import com.example.touristguide.data.network.ForecastResponse
import com.example.touristguide.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()
    private val _forecast = MutableStateFlow<ForecastResponse?>(null)
    val forecast: StateFlow<ForecastResponse?> = _forecast

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetch7DayForecast(lat: Double, lon: Double) {
        _isLoading.value = true
        _error.value = null
        repository.get7DayForecast(lat, lon).enqueue(object : Callback<ForecastResponse> {
            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _forecast.value = response.body()
                } else {
                    _error.value = "No data found"
                }
            }
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
            }
        })
    }
}