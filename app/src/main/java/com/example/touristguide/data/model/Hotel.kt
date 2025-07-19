package com.example.touristguide.data.model

data class Hotel(
    val id: String,
    val name: String,
    val location: String?,
    val price: Double? = null,
    val rating: Double? = null,
    val lat: Double, // Added latitude
    val lon: Double, // Added longitude
    val isFavorite: Boolean = false,
    val phone: String? = null,
    val website: String? = null,
    val amenities: List<String> = emptyList(),
    val imageUrl: String? = null
)
