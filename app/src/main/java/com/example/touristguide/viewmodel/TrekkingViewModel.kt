package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Data class for a Trek
data class Trek(val name: String, val duration: String, val difficulty: String)

class TrekkingViewModel : ViewModel() {
    private val _treks = MutableStateFlow<List<Trek>>(
        listOf(
            Trek("Everest Base Camp Trek", "12 days", "Hard"),
            Trek("Annapurna Base Camp Trek", "10 days", "Medium"),
            Trek("Langtang Valley Trek", "8 days", "Easy")
        )
    )
    val treks: StateFlow<List<Trek>> = _treks
}