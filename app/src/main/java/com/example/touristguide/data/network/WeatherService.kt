package com.example.touristguide.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

data class ForecastResponse(val daily: List<DailyForecast>)
data class DailyForecast(
    val dt: Long,
    val temp: Temp,
    val weather: List<WeatherDescription>
)
data class Temp(val day: Double)
data class WeatherDescription(val main: String, val description: String)

interface WeatherService {
    @GET("data/2.5/onecall")
    fun get7DayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely,hourly,alerts,current",
        @Query("appid") apiKey: String
    ): Call<ForecastResponse>
}