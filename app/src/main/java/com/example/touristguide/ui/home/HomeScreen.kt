package com.example.touristguide.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.R
import com.example.touristguide.navigation.Routes
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonBottomBar
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.touristguide.data.model.User
import android.util.Log
import com.example.touristguide.viewmodel.PlacesViewModel
import com.example.touristguide.viewmodel.PlacesViewModelFactory
import com.example.touristguide.data.repository.GeoapifyRepository
import com.example.touristguide.data.network.GeoapifyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.touristguide.data.model.PlaceCategory
import com.example.touristguide.data.model.Place
import coil.compose.AsyncImage
import com.example.touristguide.data.network.PixabayService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf

data class CategoryItem(
    val label: String, 
    val icon: String,
    val route: String
)

val categoryItems = listOf(
    CategoryItem("Places to Visit", "🏞️", Routes.PLACES),
    CategoryItem("Trekking Routes", "🥾", Routes.TREKKING),
    CategoryItem("Hotels", "🏨", Routes.HOTELS),
    CategoryItem("Food", "🍲", Routes.FOOD),
    CategoryItem("Weather", "🌤️", Routes.WEATHER),
    CategoryItem("Transport", "🚌", Routes.TRANSPORT),
    CategoryItem("Hospital", "🚑", Routes.HOSPITAL),
    CategoryItem("Budget", "💵", Routes.BUDGET),
    CategoryItem("Help! - मदत्!", "☎️", Routes.EMERGENCY)
)

data class FeaturedDestination(
    val title: String,
    val price: String,
    val imageRes: Int,
    val isNew: Boolean = false
)

val allDestinations = listOf(
    FeaturedDestination("🏔️ Mountain Peak Paradise", "Starting from $299", R.drawable.mountain, true),
    FeaturedDestination("🏯 Pashupatinath Temple", "Starting from $50", R.drawable.mountain),
    FeaturedDestination("💫 Pokhara Lake Side", "Starting from $199", R.drawable.mountain),
    FeaturedDestination("🌄 Nagarkot Sunrise View", "Starting from $149", R.drawable.mountain),
    FeaturedDestination("🏞️ Chitwan National Park", "Starting from $249", R.drawable.mountain),
    FeaturedDestination("⛰️ Annapurna Base Camp", "Starting from $399", R.drawable.mountain),
    FeaturedDestination("🗻 Everest View Trek", "Starting from $599", R.drawable.mountain),
    FeaturedDestination("🌅 Lumbini Peace Park", "Starting from $99", R.drawable.mountain)
)

@Composable
fun CategoryCard(
    label: String,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 26.sp)
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FeaturedDestinationCarousel(
    places: List<Place>,
    onPlaceClick: (Place) -> Unit
) {
    val listState = rememberLazyListState()
    var currentOffset by rememberSaveable { mutableIntStateOf(0) }

    // Pixabay setup
    val pixabayApiKey = "51346711-84695fc7acb617448e6e8b140"
    val pixabayRetrofit = remember {
        retrofit2.Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }
    val pixabayService = remember { pixabayRetrofit.create(PixabayService::class.java) }
    val imageCache = remember { mutableStateMapOf<String, String?>() }

    LaunchedEffect(currentOffset) {
        if (places.isNotEmpty())
            listState.animateScrollToItem(currentOffset)
    }

    LaunchedEffect(places) {
        while (places.isNotEmpty()) {
            delay(30000)
            currentOffset = (currentOffset + 1) % places.size.coerceAtLeast(1)
        }
    }

    Column {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(places.size) { index ->
                val place = places[index]
                val imageUrl = imageCache[place.name]
                LaunchedEffect(place.name) {
                    if (imageUrl == null && !imageCache.containsKey(place.name)) {
                        try {
                            val query = buildString {
                                append(place.name ?: "")
                                if (!place.category.displayName.isNullOrBlank()) {
                                    append(" ")
                                    append(place.category.displayName)
                                }
                                append(" Nepal")
                            }
                            val response = withContext(Dispatchers.IO) {
                                pixabayService.searchImages(
                                    apiKey = pixabayApiKey,
                                    query = query
                                )
                            }
                            val url = response.hits.firstOrNull()?.webformatURL
                            imageCache[place.name ?: ""] = url
                        } catch (e: Exception) {
                            imageCache[place.name ?: ""] = null
                        }
                    }
                }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                    modifier = Modifier
                        .width(300.dp)
                        .clickable { onPlaceClick(place) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (imageUrl != null) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = place.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.mountain),
                                contentDescription = place.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(place.name ?: "Unknown", fontWeight = FontWeight.Bold)
                        Text(place.address ?: "", color = Color.Gray)
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(places.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .background(
                            color = if (currentOffset == index) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel<AuthViewModel>()) {
    val user by viewModel.user.collectAsState()
    val userName by viewModel.userName.collectAsState("")
    LaunchedEffect(user, userName) {
        Log.d("HomeScreen", "user: $user, userName: $userName")
    }
    LaunchedEffect(Unit) { viewModel.fetchUser() }

    // --- Inject PlacesViewModel for featured destinations ---
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://api.geoapify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val geoapifyService = remember { retrofit.create(GeoapifyApiService::class.java) }
    val apiKey = "6acbf75b57b74b749fd87b61351b7c77" // <-- Replace with your actual API key
    val repository = remember { GeoapifyRepository(geoapifyService, apiKey) }
    val factory = remember { PlacesViewModelFactory(repository, PlaceCategory.TOURIST_ATTRACTIONS) }
    val placesViewModel: PlacesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
    // Fetch places on first load
    LaunchedEffect(Unit) {
        placesViewModel.fetchInitialPlaces(28.3949, 84.1240, 50000)
    }
    val featuredPlaces = placesViewModel.searchResults.value.take(4)

    Scaffold(
        bottomBar = { CommonBottomBar(navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = user?.let { "Welcome, ${it.firstName} ${it.lastName}".trim() } ?: "Welcome, $userName",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Quick Categories", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(300.dp)
            ) {
                items(categoryItems.take(8)) { item ->
                    CategoryCard(
                        label = item.label,
                        icon = item.icon,
                        onClick = { navController.navigate(item.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Emergency", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            CategoryCard(
                label = categoryItems.last().label,
                icon = categoryItems.last().icon,
                onClick = { navController.navigate(categoryItems.last().route) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Featured Destinations", fontWeight = FontWeight.SemiBold)
            Text("Explore our top picks", fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            FeaturedDestinationCarousel(
                places = featuredPlaces,
                onPlaceClick = { place ->
                    navController.navigate("places?placeName=" + java.net.URLEncoder.encode(place.name ?: "", "UTF-8"))
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TouristGuideTheme {
        val navController = rememberNavController()
        HomeScreen(navController)
    }
}
