package com.example.touristguide.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.touristguide.data.model.Place

@Composable
fun PlaceList(
    places: List<Place>,
    selectedPlace: Place?,
    onPlaceClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(places) { place ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onPlaceClick(place) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (place == selectedPlace) 8.dp else 2.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (place == selectedPlace) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(place.name, style = MaterialTheme.typography.titleMedium)
                    Text(place.address, style = MaterialTheme.typography.bodySmall)
                    Text("Rating: ${place.rating} | Price: ${place.price} | Distance: ${place.distance} km", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
} 