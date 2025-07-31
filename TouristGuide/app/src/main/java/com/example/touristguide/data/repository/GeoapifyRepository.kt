package com.example.touristguide.data.repository

import com.example.touristguide.data.model.Place
import com.example.touristguide.data.model.PlaceCategory
import com.example.touristguide.data.network.GeoapifyApiService
import org.osmdroid.util.GeoPoint
import com.example.touristguide.data.model.PlaceSearchFeature

class GeoapifyRepository(private val geoapifyService: GeoapifyApiService, private val apiKey: String) {
    suspend fun searchPlaces(
        category: PlaceCategory,
        location: GeoPoint?,
        limit: Int = 20
    ): List<Place> {
        val locationString = if (location != null) {
            "${location.longitude},${location.latitude}"
        } else {
            "85.3240,27.7172"
        }
        val response = geoapifyService.searchPlaces(
            categories = category.apiCategory,
            filter = "circle:$locationString,10000",
            bias = "proximity:$locationString",
            limit = limit,
            apiKey = apiKey
        )
        return response.features.mapNotNull { feature: PlaceSearchFeature ->
            val props = feature.properties
            if (props.name != null) {
                Place(
                    id = "${props.name}_${props.lat}_${props.lon}",
                    name = props.name,
                    address = props.formatted,
                    category = category,
                    latitude = props.lat,
                    longitude = props.lon,
                    imageUrl = null,
                    rating = null, // Not available from API
                    price = if (category == PlaceCategory.RESTAURANTS) (200..2000).random().toDouble() else if (category == PlaceCategory.ACCOMMODATION) (500..3000).random().toDouble() else null
                )
            } else null
        }
    }

    suspend fun searchTransportPlaces(
        categories: String,
        location: org.osmdroid.util.GeoPoint?,
        limit: Int = 20
    ): List<com.example.touristguide.data.model.Place> {
        val locationString = if (location != null) {
            "${location.longitude},${location.latitude}"
        } else {
            "85.3240,27.7172"
        }
        val response = geoapifyService.searchPlaces(
            categories = categories,
            filter = "circle:$locationString,3000",
            bias = "proximity:$locationString",
            limit = limit,
            apiKey = apiKey
        )
        return response.features.mapNotNull { feature: PlaceSearchFeature ->
            val props = feature.properties
            if (props.name != null) {
                com.example.touristguide.data.model.Place(
                    id = "${props.name}_${props.lat}_${props.lon}",
                    name = props.name,
                    address = props.formatted,
                    category = com.example.touristguide.data.model.PlaceCategory.TOURIST_ATTRACTIONS, // Use a generic or custom category for transport
                    latitude = props.lat,
                    longitude = props.lon,
                    imageUrl = null,
                    rating = null, // Use rating from API if available
                    price = null   // Use price from API if available
                )
            } else null
        }
    }

    suspend fun searchTransportPlacesWithName(
        categories: String,
        location: org.osmdroid.util.GeoPoint?,
        name: String?,
        limit: Int = 20
    ): List<com.example.touristguide.data.model.Place> {
        val locationString = if (location != null) {
            "${location.longitude},${location.latitude}"
        } else {
            "85.3240,27.7172"
        }
        val response = geoapifyService.searchPlaces(
            categories = categories,
            filter = "circle:$locationString,3000",
            bias = "proximity:$locationString",
            limit = limit,
            apiKey = apiKey,
            name = name
        )
        return response.features.mapNotNull { feature: PlaceSearchFeature ->
            val props = feature.properties
            if (props.name != null) {
                com.example.touristguide.data.model.Place(
                    id = "${props.name}_${props.lat}_${props.lon}",
                    name = props.name,
                    address = props.formatted,
                    category = com.example.touristguide.data.model.PlaceCategory.TOURIST_ATTRACTIONS, // Use a generic or custom category for transport
                    latitude = props.lat,
                    longitude = props.lon,
                    imageUrl = null,
                    rating = null, // Use rating from API if available
                    price = null   // Use price from API if available
                )
            } else null
        }
    }

    suspend fun searchPlacesRect(
        category: PlaceCategory,
        rect: String = "rect:80.0586,26.347,88.2015,30.447",
        limit: Int = 1000
    ): List<Place> {
        val response = geoapifyService.searchPlaces(
            categories = category.apiCategory,
            filter = rect,
            bias = "",
            limit = limit,
            apiKey = apiKey
        )
        return response.features.mapNotNull { feature: PlaceSearchFeature ->
            val props = feature.properties
            if (props.name != null) {
                Place(
                    id = "${props.name}_${props.lat}_${props.lon}",
                    name = props.name,
                    address = props.formatted,
                    category = category,
                    latitude = props.lat,
                    longitude = props.lon,
                    imageUrl = null,
                    rating = null, // Use rating from API if available
                    price = if (category == PlaceCategory.RESTAURANTS) (200..2000).random().toDouble() else if (category == PlaceCategory.ACCOMMODATION) (500..3000).random().toDouble() else null   // Use random price for hotels
                )
            } else null
        }
    }

    suspend fun fetchTouristPlacesMultipleCities(
        points: List<Pair<Double, Double>>,
        radius: Int = 20000,
        limitPerCity: Int = 50
    ): List<Place> {
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
        val allPlaces = mutableListOf<Place>()
        for ((lat, lon) in points) {
            val filter = "circle:$lon,$lat,$radius"
            val response = geoapifyService.searchPlaces(
                categories = categories,
                filter = filter,
                bias = "proximity:$lon,$lat",
                limit = limitPerCity,
                apiKey = apiKey
            )
            val places = response.features.mapNotNull { feature: PlaceSearchFeature ->
                val props = feature.properties
                if (!props.name.isNullOrBlank()) {
                    Place(
                        id = "${props.name}_${props.lat}_${props.lon}",
                        name = props.name,
                        address = props.formatted,
                        category = PlaceCategory.TOURIST_ATTRACTIONS,
                        latitude = props.lat,
                        longitude = props.lon,
                        imageUrl = null,
                        rating = null, // Use rating from API if available
                        price = null   // Use price from API if available
                    )
                } else null
            }
            allPlaces.addAll(places)
        }
        // Remove duplicates by name and location
        return allPlaces.distinctBy { it.name + "_" + it.latitude + "_" + it.longitude }
    }

    suspend fun fetchTouristPlacesForWholeNepal(limit: Int = 500): List<Place> {
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
        val response = geoapifyService.searchPlaces(
            categories = categories,
            filter = "rect:80.0586,26.347,88.2015,30.447",
            bias = "",
            limit = limit,
            apiKey = apiKey
        )
        return response.features.mapNotNull { feature: PlaceSearchFeature ->
            val props = feature.properties
            if (!props.name.isNullOrBlank()) {
                Place(
                    id = "${props.name}_${props.lat}_${props.lon}",
                    name = props.name,
                    address = props.formatted,
                    category = PlaceCategory.TOURIST_ATTRACTIONS,
                    latitude = props.lat,
                    longitude = props.lon,
                    imageUrl = null,
                    rating = null, // Use rating from API if available
                    price = null   // Use price from API if available
                )
            } else null
        }
    }

    suspend fun fetchTouristPlacesByLatLon(
        lat: Double,
        lon: Double,
        radius: Int = 100000, // 100km for wider area
        limit: Int = 500, // max per request
        offset: Int = 0, // for pagination
        websiteOnly: Boolean = false // example filter
    ): List<Place> {
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
        val response = geoapifyService.searchPlaces(
            categories = categories,
            filter = filter,
            bias = "proximity:$lon,$lat",
            limit = limit,
            offset = offset,
            apiKey = apiKey
        )
        var places = response.features.mapNotNull { feature: PlaceSearchFeature ->
            val props = feature.properties
            if (!props.name.isNullOrBlank()) {
                Place(
                    id = "${props.name}_${props.lat}_${props.lon}",
                    name = props.name,
                    address = props.formatted,
                    category = PlaceCategory.TOURIST_ATTRACTIONS,
                    latitude = props.lat,
                    longitude = props.lon,
                    imageUrl = null,
                    rating = null, // Use rating from API if available
                    price = null   // Use price from API if available
                )
            } else null
        }
        // Example filter: only places with a website
        // Website field not available from API, so this filter is disabled.
        // if (websiteOnly) {
        //     places = places.filter { it.website != null && it.website.isNotBlank() }
        // }
        return places
    }

    // Fetch and merge multiple pages for 'Load More' support
    suspend fun fetchTouristPlacesByLatLonPaged(
        lat: Double,
        lon: Double,
        radius: Int = 100000,
        pageSize: Int = 500,
        pages: Int = 2 // fetch 2 pages (1000 results)
    ): List<Place> {
        val allPlaces = mutableListOf<Place>()
        for (i in 0 until pages) {
            val offset = i * pageSize
            val page = fetchTouristPlacesByLatLon(lat, lon, radius, pageSize, offset)
            allPlaces.addAll(page)
            if (page.size < pageSize) break // no more data
        }
        // Deduplicate by name and location
        return allPlaces.distinctBy { it.name + "_" + it.latitude + "_" + it.longitude }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return Math.round(r * c * 10) / 10.0
    }
}
