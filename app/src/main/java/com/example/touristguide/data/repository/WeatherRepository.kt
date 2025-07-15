package com.example.touristguide.data.repository

import com.example.touristguide.network.OpenWeatherService
import com.example.touristguide.network.OneCallResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val apiKey = "7524a8ef33c381084fef9f7871b4306c"
    private val weatherApi: OpenWeatherService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherService::class.java)
    }
    suspend fun get7DayForecast(lat: Double, lon: Double): OneCallResponse =
        weatherApi.getOneCallForecast(lat, lon, apiKey = apiKey)
}
