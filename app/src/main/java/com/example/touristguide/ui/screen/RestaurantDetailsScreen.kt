package com.example.touristguide.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(restaurantName: String, navController: NavController) {
    val heroImage = android.R.drawable.ic_menu_gallery // Placeholder
    val images = listOf(heroImage, heroImage, heroImage)
    val cuisine = "Thakali"
    val isVeg = false
    val rating = 4.7f
    val reviewCount = 128
    val location = "Lazimpat, Kathmandu"
    val phone = "+977-1-1234567"
    val openHours = "10:00 AM - 10:00 PM"
    val avgCost = 500
    val signatureDishes = listOf("Thakali Set", "Momo", "Dal Bhat")
    val description = "A cozy place serving authentic Thakali cuisine with a modern twist. Known for its warm ambiance and delicious food."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Image with overlay
        Box(modifier = Modifier.height(220.dp)) {
            Image(
                painter = painterResource(id = heroImage),
                contentDescription = restaurantName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 0f, endY = 600f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    restaurantName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = {},
                        label = { Text(cuisine) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    FilterChip(
                        selected = isVeg,
                        onClick = {},
                        label = { Text(if (isVeg) "Veg" else "Non-veg") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isVeg) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            // Back button
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        // Ratings & Reviews
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
            Text("$rating", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(start = 4.dp))
            Text("($reviewCount reviews)", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { /* Write review */ }, shape = RoundedCornerShape(20.dp)) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Write a Review", modifier = Modifier.padding(start = 4.dp))
            }
        }

        // Image Carousel
        Text("Photos", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 4.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(images) { imageRes ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.size(width = 140.dp, height = 90.dp)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // About Section
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("About", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(description, color = Color.Gray, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Signature Dishes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    signatureDishes.forEach {
                        AssistChip(onClick = {}, label = { Text(it) })
                    }
                }
            }
        }

        // Basic Info
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoItem(icon = Icons.Default.FoodBank, label = if (isVeg) "Veg" else "Non-veg")
                InfoItem(icon = Icons.Default.AttachMoney, label = "Avg Cost: Rs $avgCost / person")
                InfoItem(icon = Icons.Default.Place, label = location)
                InfoItem(icon = Icons.Default.Call, label = phone)
                InfoItem(icon = Icons.Default.AccessTime, label = openHours)
            }
        }

        // Contact & Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { /* Call */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Call Now", modifier = Modifier.padding(start = 4.dp))
            }
            OutlinedButton(
                onClick = { /* Add to Itinerary */ },
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Add to Itinerary", modifier = Modifier.padding(start = 4.dp))
            }
            Button(
                onClick = { /* Bookmark */ },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Bookmark", modifier = Modifier.padding(start = 4.dp))
            }
        }

        // Map Preview
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Map Preview", color = Color.DarkGray)
                Button(
                    onClick = { /* Open in Maps */ },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Open in Maps", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label)
    }
}
