package com.example.touristguide.data.model

data class Hospital(
    val id: String,
    val name: String,
    val address: String?,
    val rating: Double? = null,
    val price: Double? = null
) 