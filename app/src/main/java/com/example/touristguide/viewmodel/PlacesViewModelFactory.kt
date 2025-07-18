package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.touristguide.data.repository.GeoapifyRepository
import com.example.touristguide.data.model.PlaceCategory

class PlacesViewModelFactory(
    private val repository: GeoapifyRepository,
    private val defaultCategory: PlaceCategory = PlaceCategory.ACCOMMODATION
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlacesViewModel(repository, defaultCategory) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

