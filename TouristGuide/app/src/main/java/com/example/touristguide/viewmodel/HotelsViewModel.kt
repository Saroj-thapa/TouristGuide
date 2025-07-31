package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristguide.data.model.Hotel
import com.example.touristguide.data.repository.HotelRepository
import com.example.touristguide.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.touristguide.utils.Constants
import com.example.touristguide.data.network.PixabayService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HotelFilterBar(
    openNow: Boolean,
    onOpenNowChange: (Boolean) -> Unit,
    minRating: Int,
    onMinRatingChange: (Int) -> Unit,
    priceLevel: String?,
    onPriceLevelChange: (String?) -> Unit,
    amenities: List<String>,
    onAmenitiesChange: (List<String>) -> Unit
) {
    val scrollState = rememberScrollState()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = CloudWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Min rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = SunsetOrange,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Rating",
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 2.dp)
                )
                Slider(
                    value = minRating.toFloat(),
                    onValueChange = { onMinRatingChange(it.toInt()) },
                    valueRange = 0f..5f,
                    steps = 5,
                    modifier = Modifier.width(60.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Price chips
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AttachMoney,
                    contentDescription = null,
                    tint = SunsetOrange,
                    modifier = Modifier.size(16.dp)
                )
                PriceChip(
                    label = "Budget",
                    selected = priceLevel == "budget"
                ) { onPriceLevelChange(if (priceLevel == "budget") null else "budget") }

                PriceChip(
                    label = "Mid",
                    selected = priceLevel == "mid"
                ) { onPriceLevelChange(if (priceLevel == "mid") null else "mid") }

                PriceChip(
                    label = "Luxury",
                    selected = priceLevel == "luxury"
                ) { onPriceLevelChange(if (priceLevel == "luxury") null else "luxury") }
            }

            Spacer(Modifier.width(8.dp))

            // Amenities chips
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = null,
                    tint = LeafGreen,
                    modifier = Modifier.size(16.dp)
                )

                AmenityChip(
                    label = "WiFi",
                    selected = amenities.contains("wifi")
                ) {
                    onAmenitiesChange(
                        if (amenities.contains("wifi"))
                            amenities - "wifi"
                        else
                            amenities + "wifi"
                    )
                }

                AmenityChip(
                    label = "Parking",
                    selected = amenities.contains("parking")
                ) {
                    onAmenitiesChange(
                        if (amenities.contains("parking"))
                            amenities - "parking"
                        else
                            amenities + "parking"
                    )
                }

                AmenityChip(
                    label = "Breakfast",
                    selected = amenities.contains("breakfast")
                ) {
                    onAmenitiesChange(
                        if (amenities.contains("breakfast"))
                            amenities - "breakfast"
                        else
                            amenities + "breakfast"
                    )
                }
            }
        }
    }
}
