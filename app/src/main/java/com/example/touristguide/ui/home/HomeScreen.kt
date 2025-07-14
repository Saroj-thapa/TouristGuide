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
fun FeaturedDestinationCarousel() {
    var displayedDestinations by remember { mutableStateOf(allDestinations.take(4)) }
    var currentOffset by rememberSaveable { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    // Auto scroll effect
    LaunchedEffect(currentOffset) {
        listState.animateScrollToItem(currentOffset)
    }

    // Timer for updating content and scrolling
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000) // Wait for 10 seconds
            // Update the offset for scrolling
            currentOffset = (currentOffset + 1) % 4

            // If we've scrolled through all items, shuffle and update content
            if (currentOffset == 0) {
                val shuffled = allDestinations.shuffled(Random(System.currentTimeMillis()))
                displayedDestinations = shuffled.take(4)
            }
        }
    }

    Column {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(displayedDestinations.size) { index ->
                val destination = displayedDestinations[index]
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                    modifier = Modifier.width(300.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box {
                            Image(
                                painter = painterResource(id = destination.imageRes),
                                contentDescription = destination.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                            if (destination.isNew) {
                                Text(
                                    text = "New",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .background(Color.Red, shape = RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(destination.title, fontWeight = FontWeight.Bold)
                        Text(destination.price, color = Color.Gray)
                    }
                }
            }
        }

        // Scroll indicators
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(4) { index ->
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

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search places, food, hotels...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            FeaturedDestinationCarousel()
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
