package com.example.touristguide.data.model

data class WeatherResponse(
    val name: String?,
    val main: Main?,
    val weather: List<WeatherDescription>?,
    val wind: Wind?
) 