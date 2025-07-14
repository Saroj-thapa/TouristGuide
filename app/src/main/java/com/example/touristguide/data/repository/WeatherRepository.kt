package com.example.touristguide.data.repository

import com.example.touristguide.data.network.RetrofitInstance

class WeatherRepository {
    private val apiKey = "e2eb509886583ef39095ea03aae28ad1"
    fun get7DayForecast(lat: Double, lon: Double) =
        RetrofitInstance.weatherApi.get7DayForecast(lat, lon, apiKey = apiKey)
}