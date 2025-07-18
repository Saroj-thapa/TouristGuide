package com.example.touristguide.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.touristguide.data.model.Place
import com.example.touristguide.data.model.PlaceCategory

@Composable
fun PlaceItemCard(
    place: Place,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon by category
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (place.category) {
                    PlaceCategory.TOURIST_ATTRACTIONS -> Icons.Default.LocationOn
                    PlaceCategory.ACCOMMODATION -> Icons.Default.Map
                    PlaceCategory.RESTAURANTS -> Icons.Default.Map
                    else -> Icons.Default.LocationOn
                }
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(place.name ?: "", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(place.address ?: "", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                Text("Category: ${place.category}", fontSize = 12.sp, color = Color.Gray)
                Text("Rating: ${place.rating?.toString() ?: "N/A"}", fontSize = 12.sp, color = Color.Gray)
                Text("Price: ${place.price?.toString() ?: "N/A"}", fontSize = 12.sp, color = Color.Gray)
            }
            // Save/unsave button
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isBookmarked) "Unsave" else "Save",
                    tint = if (isBookmarked) Color.Red else Color.Gray
                )
            }
            // Directions button
            val context = LocalContext.current
            IconButton(onClick = onDirectionsClick) {
                Icon(Icons.Default.Map, contentDescription = "Directions", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
