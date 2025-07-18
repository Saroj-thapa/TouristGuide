package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Trekking
import com.example.touristguide.data.repository.TrekkingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrekkingViewModel(
    private val repository: TrekkingRepository
) : ViewModel() {
    private val _treks = MutableStateFlow<List<Trekking>>(emptyList())
    val treks: StateFlow<List<Trekking>> = _treks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchTreksNearUser(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.getTreksNearUser(lat, lon)
                _treks.value = result
            } catch (e: Exception) {
                _treks.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTreksInNepal() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.getTreksInNepal()
                _treks.value = result
            } catch (e: Exception) {
                _treks.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchTreks(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.searchTreksByQuery(query)
                _treks.value = result
            } catch (e: Exception) {
                _treks.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 