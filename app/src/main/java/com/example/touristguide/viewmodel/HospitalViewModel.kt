package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.network.GeoapifyResponse
import com.example.touristguide.data.repository.GeoapifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalViewModel : ViewModel() {
    private val repository = GeoapifyRepository()
    private val _geoapifyData = MutableStateFlow<GeoapifyResponse?>(null)
    val geoapifyData: StateFlow<GeoapifyResponse?> = _geoapifyData

    fun fetchHospitalLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getPlaceInfo(lat, lon).enqueue(object : Callback<GeoapifyResponse> {
                override fun onResponse(call: Call<GeoapifyResponse>, response: Response<GeoapifyResponse>) {
                    if (response.isSuccessful) {
                        _geoapifyData.value = response.body()
                    }
                }
                override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                    _geoapifyData.value = null
                }
            })
        }
    }
}