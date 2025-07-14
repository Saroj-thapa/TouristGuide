package com.example.touristguide.data.model

// Placeholder for Hotel data model
class Hotel (
    val id: String,
    val name: String,
    val location: String,
    val price: Int,
    val rating: Int,
    val lat: Double, // Added latitude
    val lon: Double, // Added longitude
    val isFavorite: Boolean = false
)