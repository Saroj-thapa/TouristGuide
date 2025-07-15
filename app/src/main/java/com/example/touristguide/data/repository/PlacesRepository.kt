package com.example.touristguide.data.repository
import com.example.touristguide.data.model.Place
import com.example.touristguide.data.network.GeoapifyResponse
import com.example.touristguide.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlacesRepository {
    private val apiKey = "0af866806fa14f3786a60554ca3452f3"

    fun fetchTouristPlaces(lat: Double, lon: Double, radius: Int = 5000, onResult: (List<Place>) -> Unit) {
        val categories = listOf(
            "heritage.unesco",
            "heritage",
            "tourism.sights.fort",
            "tourism.sights.castle",
            "tourism.sights.ruines",
            "tourism.sights.archaeological_site",
            "tourism.sights.monastery",
            "tourism.sights.place_of_worship.church",
            "tourism.sights.place_of_worship.temple",
            "tourism.sights.place_of_worship.mosque",
            "tourism.sights.place_of_worship.synagogue",
            "tourism.sights.place_of_worship.shrine",
            "tourism.sights.lighthouse",
            "tourism.sights.tower",
            "tourism.sights.battlefield",
            "natural.mountain.peak"
        ).joinToString(",")
        val filter = "circle:$lon,$lat,$radius"
        RetrofitInstance.api.searchNearbyPlaces(categories, filter, 20, apiKey)
            .enqueue(object : Callback<GeoapifyResponse> {
                override fun onResponse(call: Call<GeoapifyResponse>, response: Response<GeoapifyResponse>) {
                    if (response.isSuccessful) {
                        val features = response.body()?.features ?: emptyList()
                        val places = features.mapIndexed { idx, f ->
                            Place(
                                id = idx.toString(),
                                name = f.properties.name ?: "Unknown Place",
                                address = f.properties.formatted ?: "",
                                category = f.properties.amenity ?: "",
                                latitude = f.properties.lat ?: 0.0,
                                longitude = f.properties.lon ?: 0.0
                            )
                        }
                        onResult(places)
                    } else {
                        onResult(emptyList())
                    }
                }
                override fun onFailure(call: Call<GeoapifyResponse>, t: Throwable) {
                    onResult(emptyList())
                }
            })
    }
}