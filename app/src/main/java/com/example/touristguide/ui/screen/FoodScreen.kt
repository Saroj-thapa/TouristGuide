package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
private data class Restaurant(
    val name: String,
    val cuisine: String,
    val pricePerPerson: Int,
    val rating: Float,
    val isVeg: Boolean,
    val location: String,
    val isOpen: Boolean,
    val imageRes: Int = android.R.drawable.ic_menu_gallery // Placeholder
)
private val featuredRestaurants = listOf(
    Restaurant("Annapura Thakali Ghar", "Thakali", 500, 4.7f, false, "Lazimpat", true),
    Restaurant("Newari Bhanchha Ghar", "Newari", 500, 4.5f, false, "Patan", false),
    Restaurant("Indian Spice", "Indian", 500, 4.2f, true, "Thamel", true)
)
private val allRestaurants = featuredRestaurants + listOf(
    Restaurant("Everest Dine", "Multi-cuisine", 700, 4.6f, false, "Durbar Marg", true),
    Restaurant("Veggie Delight", "Vegetarian", 350, 4.8f, true, "Jawalakhel", true),
    Restaurant("Momo Magic", "Nepali", 250, 4.3f, false, "Baneshwor", false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCuisine by remember { mutableStateOf<String?>(null) }
    var selectedVeg by remember { mutableStateOf<String?>(null) }
    var selectedBudget by remember { mutableStateOf<String?>(null) }

    val cuisines = listOf("Thakali", "Newari", "Indian", "Multi-cuisine", "Vegetarian", "Nepali")
    val vegOptions = listOf("Veg", "Non-veg")
    val budgetOptions = listOf("Low", "Medium", "High")

    val filteredRestaurants = allRestaurants.filter {
        (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)) &&
                (selectedCuisine == null || it.cuisine == selectedCuisine) &&
                (selectedVeg == null || (selectedVeg == "Veg" && it.isVeg) || (selectedVeg == "Non-veg" && !it.isVeg)) &&
                (selectedBudget == null ||
                        (selectedBudget == "Low" && it.pricePerPerson <= 400) ||
                        (selectedBudget == "Medium" && it.pricePerPerson in 401..700) ||
                        (selectedBudget == "High" && it.pricePerPerson > 700))
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Food & Restaurants",
                navController = navController
            )
        },
        bottomBar = {
            CommonBottomBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Suggest a restaurant or show map */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Suggest Restaurant", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search restaurants...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            // Featured Carousel
            Text(
                text = "Featured",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featuredRestaurants) { restaurant ->
                    FeaturedRestaurantCard(restaurant = restaurant, onClick = {
                        navController.navigate("restaurantDetails/${restaurant.name}")
                    })
                }
            }

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cuisines.forEach { cuisine ->
                    FilterChip(
                        selected = selectedCuisine == cuisine,
                        onClick = { selectedCuisine = if (selectedCuisine == cuisine) null else cuisine },
                        label = { Text(cuisine) },
                        modifier = Modifier
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vegOptions.forEach { veg ->
                    FilterChip(
                        selected = selectedVeg == veg,
                        onClick = { selectedVeg = if (selectedVeg == veg) null else veg },
                        label = { Text(veg) },
                        modifier = Modifier
                    )
                }
                budgetOptions.forEach { budget ->
                    FilterChip(
                        selected = selectedBudget == budget,
                        onClick = { selectedBudget = if (selectedBudget == budget) null else budget },
                        label = { Text("$budget Budget") },
                        modifier = Modifier
                    )
                }
            }

            // Restaurant List
            Text(
                text = "Popular Restaurants",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredRestaurants) { restaurant ->
                    RestaurantCard(restaurant = restaurant, onClick = {
                        navController.navigate("restaurantDetails/${restaurant.name}")
                    })
                }
            }
        }
    }
}

@Composable
private fun FeaturedRestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .width(220.dp)
            .height(140.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Restaurant image
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.LightGray)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text("${restaurant.rating}", fontSize = 14.sp, modifier = Modifier.padding(start = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Restaurant image
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = restaurant.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(restaurant.cuisine, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (restaurant.isVeg) Icons.Default.Star else Icons.Default.Star, // Replace with veg/non-veg icon
                        contentDescription = if (restaurant.isVeg) "Veg" else "Non-veg",
                        tint = if (restaurant.isVeg) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(if (restaurant.isVeg) "Veg" else "Non-veg", fontSize = 12.sp, modifier = Modifier.padding(start = 2.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                    Text(restaurant.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (restaurant.isOpen) "Open" else "Closed",
                        color = if (restaurant.isOpen) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rs ${restaurant.pricePerPerson}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "/person",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text("${restaurant.rating}", fontSize = 14.sp, modifier = Modifier.padding(start = 2.dp))
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun FoodScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        FoodScreen(navController = fakeNavController)
    }
}