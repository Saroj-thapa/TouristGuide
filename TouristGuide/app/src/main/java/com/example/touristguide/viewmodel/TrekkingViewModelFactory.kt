package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.touristguide.data.repository.TrekkingRepository

class TrekkingViewModelFactory(
    private val repository: TrekkingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrekkingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrekkingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 