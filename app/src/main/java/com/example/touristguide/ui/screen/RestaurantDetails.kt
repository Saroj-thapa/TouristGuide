package com.example.touristguide.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.touristguide.viewmodel.FoodViewModel
import com.example.touristguide.ui.components.PlaceMap
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: FoodViewModel = viewModel()
    val restaurant by viewModel.selectedRestaurant.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurant?.name ?: "Restaurant Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (restaurant != null) {
                // Map in a Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(8.dp)
                    ) {
                        PlaceMap(
                            context = context,
                            places = listOf(restaurant!!),
                            currentLocation = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Name, rating, address, price, category (all details below map)
                Text(restaurant!!.name ?: "", fontWeight = FontWeight.Bold, fontSize = 26.sp, modifier = Modifier.padding(start = 20.dp, top = 8.dp))
                Text(restaurant!!.address ?: "", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(start = 4.dp))
                Text("Category: ${restaurant!!.category.displayName}", fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(12.dp))
                // Details Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("Address: ", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                            Text(restaurant!!.address ?: "", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 2.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!restaurant!!.category.name.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(20.dp))
                                Text("Category: ", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                                Text(restaurant!!.category.name ?: "", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 2.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val gmmIntentUri = Uri.parse("geo:${restaurant!!.latitude},${restaurant!!.longitude}?q=${Uri.encode(restaurant!!.name)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Open in Maps", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            } else {
                Text("Restaurant not found.", color = Color.Red, modifier = Modifier.padding(24.dp))
            }
        }
    }
}
