package com.example.touristguide.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.ui.components.CommonTopBar
import com.example.touristguide.ui.components.CommonBottomBar
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.WeatherViewModel
import com.example.touristguide.viewmodel.LocationViewModel
import androidx.compose.runtime.collectAsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun WeatherScreen(
    navController: NavController,
    weatherViewModel: WeatherViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel()
) {
    val weather = weatherViewModel.weather.collectAsState().value
    val weatherLoading = weatherViewModel.weatherLoading.collectAsState().value
    val weatherError = weatherViewModel.weatherError.collectAsState().value
    val forecast5Day = weatherViewModel.forecast5Day.collectAsState().value
    val forecast5DayLoading = weatherViewModel.forecast5DayLoading.collectAsState().value
    val forecast5DayError = weatherViewModel.forecast5DayError.collectAsState().value
    val latitude = locationViewModel.latitude.collectAsState().value
    val longitude = locationViewModel.longitude.collectAsState().value
    var city by remember { mutableStateOf("Kathmandu") }
    var isMyLocation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Fetch Kathmandu weather on first load
    LaunchedEffect(Unit) {
        weatherViewModel.fetchWeather("Kathmandu")
        weatherViewModel.fetch5DayForecast("Kathmandu")
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Weather",
                navController = navController
            )
        },
        bottomBar = {
            CommonBottomBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {
                    if (latitude != null && longitude != null) {
                        isMyLocation = true
                        city = ""
                        weatherViewModel.fetchWeather("$latitude,$longitude")
                        weatherViewModel.fetch5DayForecast("$latitude,$longitude")
                    }
                }) {
                    Text("My Location")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Enter city name", fontSize = 18.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontSize = 15.sp)
                )
            }
            // Move the Get Weather button below and center it
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    isMyLocation = false
                    weatherViewModel.fetchWeather(city)
                    weatherViewModel.fetch5DayForecast(city)
                }, enabled = city.isNotBlank()) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Get Weather")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Move search UI below the weather card
            when {
                forecast5DayLoading -> {
                    CircularProgressIndicator()
                }
                forecast5DayError != null -> {
                    Text(text = forecast5DayError, color = Color.Red)
                }
                forecast5Day?.list != null -> {
                    Text(text = "7-Day Forecast for  ${weather?.name ?: city}", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    val grouped = forecast5Day.list?.groupBy { item ->
                        SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(item.dt * 1000L))
                    } ?: emptyMap()
                    grouped.forEach { (dayOfWeek, items) ->
                        val first = items.firstOrNull()
                        val temp = first?.main?.temp?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: "-"
                        val desc = first?.weather?.firstOrNull()?.description ?: "-"
                        val icon = first?.weather?.firstOrNull()?.icon
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                            if (icon != null) {
                                val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                                androidx.compose.foundation.Image(
                                    painter = coil.compose.rememberAsyncImagePainter(iconUrl),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = dayOfWeek, fontWeight = FontWeight.Bold)
                                Text(text = "$temp°C, $desc")
                                Text(text = "Humidity: ${first?.main?.humidity ?: "-"}%  Wind: ${first?.wind?.speed ?: "-"} m/s", fontSize = 12.sp)
                            }
                        }
                        Divider()
                    }
                }
                weatherLoading -> {
                    CircularProgressIndicator()
                }
                weatherError != null -> {
                    Text(text = weatherError, color = Color.Red)
                }
                weather != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Weather in ${weather.name}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            weather.weather?.firstOrNull()?.icon?.let { icon ->
                                val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                                androidx.compose.foundation.Image(
                                    painter = coil.compose.rememberAsyncImagePainter(iconUrl),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = weather.weather?.firstOrNull()?.main ?: "-",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = weather.weather?.firstOrNull()?.description ?: "-",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Temperature: ${weather.main?.temp?.let { String.format("%.1f", it) } ?: "-"}°C",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Humidity: ${weather.main?.humidity ?: "-"}%",
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Wind Speed: ${weather.wind?.speed ?: "-"} m/s",
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // Nicer search city UI
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Search City Weather",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("Enter city name", fontSize = 18.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                textStyle = TextStyle(fontSize = 20.sp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        if (latitude != null && longitude != null) {
                                            isMyLocation = true
                                            city = ""
                                            weatherViewModel.fetchWeather("$latitude,$longitude")
                                            weatherViewModel.fetch5DayForecast("$latitude,$longitude")
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("My Location")
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = {
                                        isMyLocation = false
                                        weatherViewModel.fetchWeather(city)
                                        weatherViewModel.fetch5DayForecast(city)
                                    },
                                    enabled = city.isNotBlank(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Get Weather")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    TouristGuideTheme {
        val fakeNavController = rememberNavController()
        WeatherScreen(navController = fakeNavController)
    }
}