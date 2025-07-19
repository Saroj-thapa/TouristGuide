package com.example.touristguide.data.model

val demoTreks = listOf(
    Trekking(
        id = "1",
        name = "Everest Base Camp Trek",
        description = "The classic trek to the base of the world's highest mountain, passing through Sherpa villages and breathtaking Himalayan scenery.",
        distanceKm = 130.0,
        estimatedDuration = "12-14 days",
        difficulty = "Hard",
        maxAltitude = 5545,
        minAltitude = 2800,
        routeGeoJson = null, // Add GeoJSON if available
        images = listOf(
            "https://images.unsplash.com/photo-1506744038136-46273834b3fb",
            "https://images.unsplash.com/photo-1464983953574-0892a716854b",
            "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429"
        ),
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
        description = "A diverse trek around the Annapurna massif, crossing Thorong La Pass and passing through many ethnic villages.",
        distanceKm = 160.0,
        estimatedDuration = "15-20 days",
        difficulty = "Moderate-Hard",
        maxAltitude = 5416,
        minAltitude = 760,
        routeGeoJson = null,
        images = listOf(
            "https://images.unsplash.com/photo-1502086223501-7ea6ecd79368",
            "https://images.unsplash.com/photo-1465101046530-73398c7f28ca"
        ),
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
        description = "A beautiful trek close to Kathmandu, through forests, villages, and up to alpine meadows near the Tibetan border.",
        distanceKm = 70.0,
        estimatedDuration = "7-10 days",
        difficulty = "Moderate",
        maxAltitude = 4773,
        minAltitude = 1460,
        routeGeoJson = null,
        images = listOf(
            "https://images.unsplash.com/photo-1519681393784-d120267933ba",
            "https://images.unsplash.com/photo-1501785888041-af3ef285b470"
        ),
        bestSeason = "March-May, Oct-Nov",
        stops = listOf(
            TrekStop("Syabrubesi", "village", 28.0916, 85.3216, "Starting point"),
            TrekStop("Lama Hotel", "lodge", 28.2116, 85.4421, "Popular stop"),
            TrekStop("Kyanjin Gompa", "village", 28.2345, 85.5632, "Final stop")
        ),
        tips = listOf("Try yak cheese in Kyanjin Gompa", "Watch for landslides in monsoon"),
        weatherForecast = emptyList()
    ),
    Trekking(
        id = "4",
        name = "Manaslu Circuit Trek",
        description = "A remote and stunning circuit trek around the world's eighth highest mountain, with Tibetan culture and dramatic scenery.",
        distanceKm = 177.0,
        estimatedDuration = "14-18 days",
        difficulty = "Hard",
        maxAltitude = 5160,
        minAltitude = 700,
        routeGeoJson = null,
        images = listOf(
            "https://images.unsplash.com/photo-1464013778555-8e723c2f01f8",
            "https://images.unsplash.com/photo-1509228468518-180dd4864904"
        ),
        bestSeason = "March-May, Sept-Nov",
        stops = listOf(
            TrekStop("Soti Khola", "village", 28.3869, 84.8572, "Starting point"),
            TrekStop("Samagaon", "village", 28.5766, 85.2936, "Acclimatization stop"),
            TrekStop("Larkya La Pass", "pass", 28.6111, 84.6106, "Highest point")
        ),
        tips = listOf("Permit required", "Prepare for basic accommodation"),
        weatherForecast = emptyList()
    )
) 