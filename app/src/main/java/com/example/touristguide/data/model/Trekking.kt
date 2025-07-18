package com.example.touristguide.data.model

data class Trekking(
    val id: String,
    val name: String,
    val description: String,
    val distanceKm: Double,
    val estimatedDuration: String,
    val difficulty: String,
    val maxAltitude: Int,
    val minAltitude: Int,
    val routeGeoJson: String?, // GeoJSON for the route
    val images: List<String> = emptyList(),
    val bestSeason: String = "",
    val stops: List<TrekStop> = emptyList(),
    val tips: List<String> = emptyList(),
    val weatherForecast: List<WeatherForecastDay> = emptyList()
)

data class TrekStop(
    val name: String,
    val type: String, // e.g., "lodge", "water point", "camp"
    val lat: Double,
    val lon: Double,
    val description: String = ""
)

data class WeatherForecastDay(
    val date: String,
    val summary: String,
    val minTemp: Double,
    val maxTemp: Double,
    val iconUrl: String? = null
) 