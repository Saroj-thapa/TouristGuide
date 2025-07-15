package com.example.touristguide.data.repository

import com.example.touristguide.data.network.WeatherService
import com.example.touristguide.data.model.ForecastResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val apiKey = "7524a8ef33c381084fef9f7871b4306c"
    private val weatherApi: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
    fun get5DayForecastByCoords(lat: Double, lon: Double) =
        weatherApi.get5DayForecastByCoords(lat, lon, apiKey = apiKey)

    fun get5DayForecastByCity(city: String) =
        weatherApi.get5DayForecastByCity(city, apiKey = apiKey)

    fun getCurrentWeatherByCoords(lat: Double, lon: Double) =
        weatherApi.getCurrentWeatherByCoords(lat, lon, apiKey = apiKey)

    fun getCurrentWeatherByCity(city: String) =
        weatherApi.getCurrentWeatherByCity(city, apiKey = apiKey)
}
