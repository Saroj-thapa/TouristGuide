package com.example.touristguide.data.repository

import com.example.touristguide.data.model.Trekking
import com.example.touristguide.data.model.TrekStop
import com.example.touristguide.data.model.WeatherForecastDay
import com.example.touristguide.data.network.GeoapifyApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.util.UUID
import android.util.Log
import com.example.touristguide.data.model.PlacesSearchResponse
import com.example.touristguide.data.model.PlaceSearchFeature
import com.example.touristguide.data.model.PlaceSearchProperties

class TrekkingRepository(private val geoapifyService: GeoapifyApiService, private val apiKey: String) {
    // Example: fetch trekking routes near the user (within 50km)
    suspend fun getTreksNearUser(lat: Double, lon: Double): List<Trekking> = withContext(Dispatchers.IO) {
        try {
            val categories = "route.hiking" // Focus on hiking routes for better results
            val filter = "circle:$lon,$lat,50000" // 50km radius
            val response = geoapifyService.searchPlaces(
                categories = categories,
                filter = filter,
                bias = "proximity:$lon,$lat",
                limit = 20,
                apiKey = apiKey
            )
            Log.d("TrekkingRepository", "Geoapify full response: $response")
            Log.d("TrekkingRepository", "Geoapify features: ${response.features}")
            val treks = response.features.mapNotNull { feature: PlaceSearchFeature ->
                val props: PlaceSearchProperties = feature.properties
                val trekName = props.name ?: return@mapNotNull null
                val trekLat = props.lat
                val trekLon = props.lon
                val distanceKm = calculateDistance(lat, lon, trekLat, trekLon)
                Trekking(
                    id = UUID.randomUUID().toString(),
                    name = trekName,
                    description = props.formatted,
                    distanceKm = distanceKm,
                    estimatedDuration = "-",
                    difficulty = "-",
                    maxAltitude = 0,
                    minAltitude = 0,
                    routeGeoJson = null,
                    images = emptyList(),
                    bestSeason = "",
                    stops = emptyList(),
                    tips = emptyList(),
                    weatherForecast = emptyList()
                )
            }
            if (treks.isEmpty()) {
                Log.d("TrekkingRepository", "No treks found for $lat, $lon. Returning fallback demo treks.")
                return@withContext listOf(
                    Trekking(
                        id = "1",
                        name = "Everest Base Camp Trek",
                        description = "The classic trek to the base of the world's highest mountain.",
                        distanceKm = 130.0,
                        estimatedDuration = "12-14 days",
                        difficulty = "Hard",
                        maxAltitude = 5545,
                        minAltitude = 2800,
                        routeGeoJson = null,
                        images = emptyList(),
                        bestSeason = "March-May, Sept-Nov",
                        stops = listOf(
                            TrekStop("Lukla", "village", 27.6881, 86.7314, "Gateway to Everest region"),
                            TrekStop("Namche Bazaar", "village", 27.8057, 86.7120, "Main trading center"),
                            TrekStop("Gorakshep", "village", 28.0046, 86.8571, "Last stop before base camp")
                        ),
                        tips = listOf("Acclimatize properly", "Carry cash", "Prepare for cold nights"),
                        weatherForecast = emptyList()
                    ),
                    Trekking(
                        id = "2",
                        name = "Annapurna Circuit Trek",
                        description = "A diverse trek around the Annapurna massif.",
                        distanceKm = 160.0,
                        estimatedDuration = "15-20 days",
                        difficulty = "Moderate-Hard",
                        maxAltitude = 5416,
                        minAltitude = 760,
                        routeGeoJson = null,
                        images = emptyList(),
                        bestSeason = "March-May, Oct-Nov",
                        stops = listOf(
                            TrekStop("Besisahar", "town", 28.2296, 84.4197, "Starting point"),
                            TrekStop("Manang", "village", 28.6698, 84.0236, "Acclimatization stop"),
                            TrekStop("Thorong La Pass", "pass", 28.7966, 83.9407, "Highest point")
                        ),
                        tips = listOf("Start early for Thorong La Pass", "Watch for landslides"),
                        weatherForecast = emptyList()
                    ),
                    Trekking(
                        id = "3",
                        name = "Langtang Valley Trek",
                        description = "A scenic trek through Langtang National Park, close to Kathmandu.",
                        distanceKm = 65.0,
                        estimatedDuration = "7-10 days",
                        difficulty = "Moderate",
                        maxAltitude = 4773,
                        minAltitude = 1460,
                        routeGeoJson = null,
                        images = emptyList(),
                        bestSeason = "March-May, Sept-Nov",
                        stops = listOf(
                            TrekStop("Syabrubesi", "village", 28.1206, 85.3066, "Starting point"),
                            TrekStop("Kyanjin Gompa", "village", 28.2126, 85.4432, "Famous monastery"),
                            TrekStop("Langtang Village", "village", 28.2116, 85.4386, "Beautiful valley")
                        ),
                        tips = listOf("Watch for wildlife", "Try yak cheese"),
                        weatherForecast = emptyList()
                    ),
                    Trekking(
                        id = "4",
                        name = "Manaslu Circuit Trek",
                        description = "A remote and challenging circuit around the Manaslu massif.",
                        distanceKm = 177.0,
                        estimatedDuration = "14-18 days",
                        difficulty = "Hard",
                        maxAltitude = 5160,
                        minAltitude = 700,
                        routeGeoJson = null,
                        images = emptyList(),
                        bestSeason = "March-May, Sept-Nov",
                        stops = listOf(
                            TrekStop("Soti Khola", "village", 28.1036, 84.8846, "Starting point"),
                            TrekStop("Samagaon", "village", 28.5492, 84.6352, "Acclimatization stop"),
                            TrekStop("Larke Pass", "pass", 28.6111, 84.5208, "Highest point")
                        ),
                        tips = listOf("Restricted area permit required", "Remote villages"),
                        weatherForecast = emptyList()
                    ),
                    Trekking(
                        id = "5",
                        name = "Ghorepani Poon Hill Trek",
                        description = "A short and popular trek with stunning sunrise views of Annapurna and Dhaulagiri.",
                        distanceKm = 32.0,
                        estimatedDuration = "4-5 days",
                        difficulty = "Easy-Moderate",
                        maxAltitude = 3210,
                        minAltitude = 1070,
                        routeGeoJson = null,
                        images = emptyList(),
                        bestSeason = "All year",
                        stops = listOf(
                            TrekStop("Nayapul", "village", 28.2262, 83.8203, "Starting point"),
                            TrekStop("Ghorepani", "village", 28.4006, 83.6917, "Famous for rhododendrons"),
                            TrekStop("Poon Hill", "viewpoint", 28.4000, 83.6950, "Sunrise viewpoint")
                        ),
                        tips = listOf("Bring a camera", "Can be done year-round"),
                        weatherForecast = emptyList()
                    )
                )
            }
            treks
        } catch (e: Exception) {
            Log.e("TrekkingRepository", "Error fetching treks", e)
            return@withContext listOf(
                Trekking(
                    id = "1",
                    name = "Everest Base Camp Trek",
                    description = "The classic trek to the base of the world's highest mountain.",
                    distanceKm = 130.0,
                    estimatedDuration = "12-14 days",
                    difficulty = "Hard",
                    maxAltitude = 5545,
                    minAltitude = 2800,
                    routeGeoJson = null,
                    images = emptyList(),
                    bestSeason = "March-May, Sept-Nov",
                    stops = listOf(
                        TrekStop("Lukla", "village", 27.6881, 86.7314, "Gateway to Everest region"),
                        TrekStop("Namche Bazaar", "village", 27.8057, 86.7120, "Main trading center"),
                        TrekStop("Gorakshep", "village", 28.0046, 86.8571, "Last stop before base camp")
                    ),
                    tips = listOf("Acclimatize properly", "Carry cash", "Prepare for cold nights"),
                    weatherForecast = emptyList()
                ),
                Trekking(
                    id = "2",
                    name = "Annapurna Circuit Trek",
                    description = "A diverse trek around the Annapurna massif.",
                    distanceKm = 160.0,
                    estimatedDuration = "15-20 days",
                    difficulty = "Moderate-Hard",
                    maxAltitude = 5416,
                    minAltitude = 760,
                    routeGeoJson = null,
                    images = emptyList(),
                    bestSeason = "March-May, Oct-Nov",
                    stops = listOf(
                        TrekStop("Besisahar", "town", 28.2296, 84.4197, "Starting point"),
                        TrekStop("Manang", "village", 28.6698, 84.0236, "Acclimatization stop"),
                        TrekStop("Thorong La Pass", "pass", 28.7966, 83.9407, "Highest point")
                    ),
                    tips = listOf("Start early for Thorong La Pass", "Watch for landslides"),
                    weatherForecast = emptyList()
                ),
                Trekking(
                    id = "3",
                    name = "Langtang Valley Trek",
                    description = "A scenic trek through Langtang National Park, close to Kathmandu.",
                    distanceKm = 65.0,
                    estimatedDuration = "7-10 days",
                    difficulty = "Moderate",
                    maxAltitude = 4773,
                    minAltitude = 1460,
                    routeGeoJson = null,
                    images = emptyList(),
                    bestSeason = "March-May, Sept-Nov",
                    stops = listOf(
                        TrekStop("Syabrubesi", "village", 28.1206, 85.3066, "Starting point"),
                        TrekStop("Kyanjin Gompa", "village", 28.2126, 85.4432, "Famous monastery"),
                        TrekStop("Langtang Village", "village", 28.2116, 85.4386, "Beautiful valley")
                    ),
                    tips = listOf("Watch for wildlife", "Try yak cheese"),
                    weatherForecast = emptyList()
                ),
                Trekking(
                    id = "4",
                    name = "Manaslu Circuit Trek",
                    description = "A remote and challenging circuit around the Manaslu massif.",
                    distanceKm = 177.0,
                    estimatedDuration = "14-18 days",
                    difficulty = "Hard",
                    maxAltitude = 5160,
                    minAltitude = 700,
                    routeGeoJson = null,
                    images = emptyList(),
                    bestSeason = "March-May, Sept-Nov",
                    stops = listOf(
                        TrekStop("Soti Khola", "village", 28.1036, 84.8846, "Starting point"),
                        TrekStop("Samagaon", "village", 28.5492, 84.6352, "Acclimatization stop"),
                        TrekStop("Larke Pass", "pass", 28.6111, 84.5208, "Highest point")
                    ),
                    tips = listOf("Restricted area permit required", "Remote villages"),
                    weatherForecast = emptyList()
                ),
                Trekking(
                    id = "5",
                    name = "Ghorepani Poon Hill Trek",
                    description = "A short and popular trek with stunning sunrise views of Annapurna and Dhaulagiri.",
                    distanceKm = 32.0,
                    estimatedDuration = "4-5 days",
                    difficulty = "Easy-Moderate",
                    maxAltitude = 3210,
                    minAltitude = 1070,
                    routeGeoJson = null,
                    images = emptyList(),
                    bestSeason = "All year",
                    stops = listOf(
                        TrekStop("Nayapul", "village", 28.2262, 83.8203, "Starting point"),
                        TrekStop("Ghorepani", "village", 28.4006, 83.6917, "Famous for rhododendrons"),
                        TrekStop("Poon Hill", "viewpoint", 28.4000, 83.6950, "Sunrise viewpoint")
                    ),
                    tips = listOf("Bring a camera", "Can be done year-round"),
                    weatherForecast = emptyList()
                )
            )
        }
    }

    // Fetch trekking routes for all of Nepal using a rectangle filter
    suspend fun getTreksInNepal(): List<Trekking> = withContext(Dispatchers.IO) {
        try {
            val categories = "route.hiking"
            val filter = "rect:80.0586,26.347,88.2015,30.447" // Whole Nepal
            val response = geoapifyService.searchPlaces(
                categories = categories,
                filter = filter,
                bias = "",
                limit = 100,
                apiKey = apiKey
            )
            Log.d("TrekkingRepository", "Geoapify full response: $response")
            Log.d("TrekkingRepository", "Geoapify features: ${response.features}")
            val treks = response.features.mapNotNull { feature: PlaceSearchFeature ->
                val props: PlaceSearchProperties = feature.properties
                val trekName = props.name ?: return@mapNotNull null
                val trekLat = props.lat
                val trekLon = props.lon
                Trekking(
                    id = UUID.randomUUID().toString(),
                    name = trekName,
                    description = props.formatted,
                    distanceKm = 0.0,
                    estimatedDuration = "-",
                    difficulty = "-",
                    maxAltitude = 0,
                    minAltitude = 0,
                    routeGeoJson = null,
                    images = emptyList(),
                    bestSeason = "",
                    stops = listOf(TrekStop(trekName, "point", trekLat, trekLon, props.formatted)),
                    tips = emptyList(),
                    weatherForecast = emptyList()
                )
            }
            if (treks.isEmpty()) {
                Log.d("TrekkingRepository", "No treks found for Nepal. Returning fallback demo treks.")
                return@withContext fallbackTreks()
            }
            treks
        } catch (e: Exception) {
            Log.e("TrekkingRepository", "Error fetching treks", e)
            fallbackTreks()
        }
    }

    // Search trekking routes by query (name)
    suspend fun searchTreksByQuery(query: String): List<Trekking> = withContext(Dispatchers.IO) {
        try {
            val categories = "route.hiking"
            val filter = "rect:80.0586,26.347,88.2015,30.447" // Nepal bounds
            val response = geoapifyService.searchPlaces(
                categories = categories,
                filter = filter,
                bias = "",
                limit = 100,
                apiKey = apiKey,
                name = query
            )
            Log.d("TrekkingRepository", "Geoapify search by query '$query' response: $response")
            val treks = response.features.mapNotNull { feature: PlaceSearchFeature ->
                val props: PlaceSearchProperties = feature.properties
                val trekName = props.name ?: return@mapNotNull null
                val trekLat = props.lat
                val trekLon = props.lon
                Trekking(
                    id = UUID.randomUUID().toString(),
                    name = trekName,
                    description = props.formatted,
                    distanceKm = 0.0,
                    estimatedDuration = "-",
                    difficulty = "-",
                    maxAltitude = 0,
                    minAltitude = 0,
                    routeGeoJson = null,
                    images = emptyList(),
                    bestSeason = "",
                    stops = listOf(TrekStop(trekName, "point", trekLat, trekLon, props.formatted)),
                    tips = emptyList(),
                    weatherForecast = emptyList()
                )
            }
            if (treks.isEmpty()) fallbackTreks() else treks
        } catch (e: Exception) {
            Log.e("TrekkingRepository", "Error searching treks by query", e)
            fallbackTreks()
        }
    }

    private fun fallbackTreks(): List<Trekking> = listOf(
        Trekking(
            id = "1",
            name = "Everest Base Camp Trek",
            description = "The classic trek to the base of the world's highest mountain.",
            distanceKm = 130.0,
            estimatedDuration = "12-14 days",
            difficulty = "Hard",
            maxAltitude = 5545,
            minAltitude = 2800,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "March-May, Sept-Nov",
            stops = listOf(
                TrekStop("Lukla", "village", 27.6881, 86.7314, "Gateway to Everest region"),
                TrekStop("Namche Bazaar", "village", 27.8057, 86.7120, "Main trading center"),
                TrekStop("Gorakshep", "village", 28.0046, 86.8571, "Last stop before base camp")
            ),
            tips = listOf("Acclimatize properly", "Carry cash", "Prepare for cold nights"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "2",
            name = "Annapurna Circuit Trek",
            description = "A diverse trek around the Annapurna massif.",
            distanceKm = 160.0,
            estimatedDuration = "15-20 days",
            difficulty = "Moderate-Hard",
            maxAltitude = 5416,
            minAltitude = 760,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "March-May, Oct-Nov",
            stops = listOf(
                TrekStop("Besisahar", "town", 28.2296, 84.4197, "Starting point"),
                TrekStop("Manang", "village", 28.6698, 84.0236, "Acclimatization stop"),
                TrekStop("Thorong La Pass", "pass", 28.7966, 83.9407, "Highest point")
            ),
            tips = listOf("Start early for Thorong La Pass", "Watch for landslides"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "3",
            name = "Langtang Valley Trek",
            description = "A scenic trek through Langtang National Park, close to Kathmandu.",
            distanceKm = 65.0,
            estimatedDuration = "7-10 days",
            difficulty = "Moderate",
            maxAltitude = 4773,
            minAltitude = 1460,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "March-May, Sept-Nov",
            stops = listOf(
                TrekStop("Syabrubesi", "village", 28.1206, 85.3066, "Starting point"),
                TrekStop("Kyanjin Gompa", "village", 28.2126, 85.4432, "Famous monastery"),
                TrekStop("Langtang Village", "village", 28.2116, 85.4386, "Beautiful valley")
            ),
            tips = listOf("Watch for wildlife", "Try yak cheese"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "4",
            name = "Manaslu Circuit Trek",
            description = "A remote and challenging circuit around the Manaslu massif.",
            distanceKm = 177.0,
            estimatedDuration = "14-18 days",
            difficulty = "Hard",
            maxAltitude = 5160,
            minAltitude = 700,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "March-May, Sept-Nov",
            stops = listOf(
                TrekStop("Soti Khola", "village", 28.1036, 84.8846, "Starting point"),
                TrekStop("Samagaon", "village", 28.5492, 84.6352, "Acclimatization stop"),
                TrekStop("Larke Pass", "pass", 28.6111, 84.5208, "Highest point")
            ),
            tips = listOf("Restricted area permit required", "Remote villages"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "5",
            name = "Ghorepani Poon Hill Trek",
            description = "A short and popular trek with stunning sunrise views of Annapurna and Dhaulagiri.",
            distanceKm = 32.0,
            estimatedDuration = "4-5 days",
            difficulty = "Easy-Moderate",
            maxAltitude = 3210,
            minAltitude = 1070,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "All year",
            stops = listOf(
                TrekStop("Nayapul", "village", 28.2262, 83.8203, "Starting point"),
                TrekStop("Ghorepani", "village", 28.4006, 83.6917, "Famous for rhododendrons"),
                TrekStop("Poon Hill", "viewpoint", 28.4000, 83.6950, "Sunrise viewpoint")
            ),
            tips = listOf("Bring a camera", "Can be done year-round"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "6",
            name = "Mardi Himal Trek",
            description = "A short and scenic trek to Mardi Himal Base Camp with stunning views of Annapurna.",
            distanceKm = 50.0,
            estimatedDuration = "5-7 days",
            difficulty = "Moderate",
            maxAltitude = 4500,
            minAltitude = 1700,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "March-May, Sept-Nov",
            stops = listOf(
                TrekStop("Kande", "village", 28.2826, 83.8203, "Starting point"),
                TrekStop("Forest Camp", "camp", 28.4312, 83.9001, "Midway camp"),
                TrekStop("Mardi Himal Base Camp", "base camp", 28.5097, 83.9502, "Final destination")
            ),
            tips = listOf("Great for short trek", "Less crowded"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "7",
            name = "Upper Mustang Trek",
            description = "A unique trek into the ancient kingdom of Mustang, rich in Tibetan culture.",
            distanceKm = 120.0,
            estimatedDuration = "12-15 days",
            difficulty = "Moderate",
            maxAltitude = 4200,
            minAltitude = 2800,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "May-Nov",
            stops = listOf(
                TrekStop("Jomsom", "town", 28.7804, 83.4608, "Gateway to Mustang"),
                TrekStop("Lo Manthang", "village", 29.1786, 83.7691, "Ancient walled city"),
                TrekStop("Chhoser Cave", "cave", 29.2432, 83.8312, "Historic cave dwellings")
            ),
            tips = listOf("Restricted area permit required", "Unique culture"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "8",
            name = "Helambu Trek",
            description = "A short trek near Kathmandu, famous for Sherpa culture and rhododendron forests.",
            distanceKm = 70.0,
            estimatedDuration = "6-8 days",
            difficulty = "Easy-Moderate",
            maxAltitude = 3600,
            minAltitude = 800,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "March-May, Sept-Nov",
            stops = listOf(
                TrekStop("Sundarijal", "village", 27.7826, 85.4486, "Starting point"),
                TrekStop("Chisapani", "village", 27.8333, 85.4667, "First stop"),
                TrekStop("Melamchi Gaon", "village", 28.0333, 85.4833, "Sherpa village")
            ),
            tips = listOf("Close to Kathmandu", "Good for beginners"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "9",
            name = "Kanchenjunga Base Camp Trek",
            description = "A remote trek to the base of the world's third highest mountain.",
            distanceKm = 220.0,
            estimatedDuration = "20-25 days",
            difficulty = "Hard",
            maxAltitude = 5143,
            minAltitude = 1200,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "April-May, Oct-Nov",
            stops = listOf(
                TrekStop("Taplejung", "town", 27.3546, 87.6710, "Starting point"),
                TrekStop("Ghunsa", "village", 27.6872, 87.7462, "Sherpa village"),
                TrekStop("Pangpema", "base camp", 27.8333, 87.9667, "Kanchenjunga Base Camp")
            ),
            tips = listOf("Requires camping", "Remote and wild"),
            weatherForecast = emptyList()
        ),
        Trekking(
            id = "10",
            name = "Rara Lake Trek",
            description = "A beautiful trek to Nepal's largest lake, Rara, in the remote northwest.",
            distanceKm = 55.0,
            estimatedDuration = "8-10 days",
            difficulty = "Moderate",
            maxAltitude = 3010,
            minAltitude = 2000,
            routeGeoJson = null,
            images = emptyList(),
            bestSeason = "April-June, Sept-Nov",
            stops = listOf(
                TrekStop("Jumla", "town", 29.2742, 82.1838, "Starting point"),
                TrekStop("Chauta", "village", 29.3833, 82.4167, "Midway village"),
                TrekStop("Rara Lake", "lake", 29.5333, 82.0833, "Nepal's largest lake")
            ),
            tips = listOf("Remote and peaceful", "Best for nature lovers"),
            weatherForecast = emptyList()
        )
    )

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
