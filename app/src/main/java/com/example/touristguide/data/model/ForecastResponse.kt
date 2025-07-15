package com.example.touristguide.data.model

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<WeatherDescription>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
)

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

data class City(
    val name: String
)

