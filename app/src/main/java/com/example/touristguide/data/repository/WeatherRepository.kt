package com.example.touristguide.data.repository

import com.example.touristguide.data.network.WeatherService
import com.example.touristguide.data.model.ForecastResponse
import com.example.touristguide.data.model.WeatherResponse
import com.example.touristguide.data.network.RetrofitInstance

class WeatherRepository {
    private val apiKey = "7524a8ef33c381084fef9f7871b4306c"
    private val weatherApi: WeatherService by lazy { RetrofitInstance.weatherApi }

    fun get5DayForecastByCoords(lat: Double, lon: Double) =
        weatherApi.get5DayForecastByCoords(lat, lon, apiKey = apiKey)

    fun get5DayForecastByCity(city: String) =
        weatherApi.get5DayForecastByCity(city, apiKey = apiKey)

    fun getCurrentWeatherByCoords(lat: Double, lon: Double) =
        weatherApi.getCurrentWeatherByCoords(lat, lon, apiKey = apiKey)

    fun getCurrentWeatherByCity(city: String) =
        weatherApi.getCurrentWeatherByCity(city, apiKey = apiKey)
} 