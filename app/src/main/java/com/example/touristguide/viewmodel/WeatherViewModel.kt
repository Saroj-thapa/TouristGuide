package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.ForecastResponse
import com.example.touristguide.data.model.WeatherResponse
import com.example.touristguide.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather
    private val _weatherLoading = MutableStateFlow(false)
    val weatherLoading: StateFlow<Boolean> = _weatherLoading
    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> = _weatherError

    private val _forecast5Day = MutableStateFlow<ForecastResponse?>(null)
    val forecast5Day: StateFlow<ForecastResponse?> = _forecast5Day
    private val _forecast5DayLoading = MutableStateFlow(false)
    val forecast5DayLoading: StateFlow<Boolean> = _forecast5DayLoading
    private val _forecast5DayError = MutableStateFlow<String?>(null)
    val forecast5DayError: StateFlow<String?> = _forecast5DayError

    fun fetchWeather(city: String) {
        _weatherLoading.value = true
        _weatherError.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getCurrentWeatherByCity(city).execute()
                if (response.isSuccessful) {
                    _weather.value = response.body()
                } else {
                    _weatherError.value = response.message()
                }
            } catch (e: Exception) {
                _weatherError.value = e.localizedMessage
            } finally {
                _weatherLoading.value = false
            }
        }
    }

    fun fetchWeather(lat: Double, lon: Double) {
        _weatherLoading.value = true
        _weatherError.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getCurrentWeatherByCoords(lat, lon).execute()
                if (response.isSuccessful) {
                    _weather.value = response.body()
                } else {
                    _weatherError.value = response.message()
                }
            } catch (e: Exception) {
                _weatherError.value = e.localizedMessage
            } finally {
                _weatherLoading.value = false
            }
        }
    }

    fun fetch5DayForecast(city: String) {
        _forecast5DayLoading.value = true
        _forecast5DayError.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.get5DayForecastByCity(city).execute()
                if (response.isSuccessful) {
                    _forecast5Day.value = response.body()
                } else {
                    _forecast5DayError.value = response.message()
                }
            } catch (e: Exception) {
                _forecast5DayError.value = e.localizedMessage
            } finally {
                _forecast5DayLoading.value = false
            }
        }
    }

    fun fetch5DayForecast(lat: Double, lon: Double) {
        _forecast5DayLoading.value = true
        _forecast5DayError.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.get5DayForecastByCoords(lat, lon).execute()
                if (response.isSuccessful) {
                    _forecast5Day.value = response.body()
                } else {
                    _forecast5DayError.value = response.message()
                }
            } catch (e: Exception) {
                _forecast5DayError.value = e.localizedMessage
            } finally {
                _forecast5DayLoading.value = false
            }
        }
    }
} 