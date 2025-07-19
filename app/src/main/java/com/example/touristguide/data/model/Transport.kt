package com.example.touristguide.data.model

data class Transport(
    val id: String,
    val type: String,
    val name: String?,
    val rating: Double? = null,
    val price: Double? = null
) 