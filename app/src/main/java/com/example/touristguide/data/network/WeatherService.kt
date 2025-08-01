package com.example.touristguide.data.network

import com.example.touristguide.data.model.ForecastResponse
import com.example.touristguide.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface WeatherService {
    // 5-day forecast by coordinates
    @GET("data/2.5/forecast")
    fun get5DayForecastByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<ForecastResponse>

    // 5-day forecast by city
    @GET("data/2.5/forecast")
    fun get5DayForecastByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<ForecastResponse>

    // Current weather by coordinates
    @GET("data/2.5/weather")
    fun getCurrentWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherResponse>

    // Current weather by city
    @GET("data/2.5/weather")
    fun getCurrentWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherResponse>
} 