package com.example.touristguide.data.model

data class Food(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String? = null,
    val rating: Double? = null,
    val price: Double? = null
) 