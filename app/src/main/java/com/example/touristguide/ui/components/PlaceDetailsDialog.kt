package com.example.touristguide.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.touristguide.data.model.Place
import com.example.touristguide.data.model.PlaceCategory
import androidx.compose.ui.platform.LocalContext
import com.example.touristguide.ui.components.PlaceMap
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.material3.ButtonDefaults

@Composable
fun PlaceDetailsDialog(place: Place, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Hospital Name
                Text(place.name ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                // 2. Map in Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    PlaceMap(
                        context = context,
                        places = listOf(place),
                        currentLocation = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // 3. Full Hospital Details
                Text("Address: ${place.address ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                Text("Category: ${place.category.displayName}", style = MaterialTheme.typography.bodyMedium)
                Text("Rating: ${place.rating?.toString() ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                Text("Price: ${place.price?.toString() ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Latitude: ", style = MaterialTheme.typography.bodyMedium)
                    Text("${place.latitude}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Longitude: ", style = MaterialTheme.typography.bodyMedium)
                    Text("${place.longitude}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                // 4. Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            val gmmIntentUri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${Uri.encode(place.name)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Open in Maps", color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
} 