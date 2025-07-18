package com.example.touristguide.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.WeatherViewModel
import com.example.touristguide.viewmodel.LocationViewModel
import androidx.compose.runtime.collectAsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
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

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFe0f7fa), Color(0xFFb2ebf2), Color(0xFFffffff)),
        startY = 0f,
        endY = 1000f
    )

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
                .background(gradient)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = {
                            if (latitude != null && longitude != null) {
                                isMyLocation = true
                                city = ""
                                weatherViewModel.fetchWeather(latitude, longitude)
                                weatherViewModel.fetch5DayForecast(latitude, longitude)
                            }
                        }, modifier = Modifier.weight(1f)) {
                            Text("My Location")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Enter city name", fontSize = 18.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(2f),
                            textStyle = TextStyle(fontSize = 15.sp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            isMyLocation = false
                            weatherViewModel.fetchWeather(city)
                            weatherViewModel.fetch5DayForecast(city)
                        },
                        enabled = city.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Get Weather")
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            // Current Weather Card
            when {
                weatherLoading -> {
                    CircularProgressIndicator()
                }
                weatherError != null -> {
                    Text(text = weatherError, color = Color.Red)
                }
                weather != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFb2ebf2))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            weather.weather?.firstOrNull()?.icon?.let { icon ->
                                val iconUrl = "https://openweathermap.org/img/wn/${icon}@4x.png"
                                androidx.compose.foundation.Image(
                                    painter = coil.compose.rememberAsyncImagePainter(iconUrl),
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = weather.weather?.firstOrNull()?.main ?: "-",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1)
                            )
                            Text(
                                text = weather.weather?.firstOrNull()?.description ?: "-",
                                fontSize = 18.sp,
                                color = Color(0xFF616161)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = weather.main?.temp?.let { String.format("%.1f°C", it) } ?: "-",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0288D1)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Humidity: ${weather.main?.humidity ?: "-"}%",
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Wind: ${weather.wind?.speed ?: "-"} m/s",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = weather.name ?: "-",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            // Forecast Cards
            when {
                forecast5DayLoading -> {
                    CircularProgressIndicator()
                }
                forecast5DayError != null -> {
                    Text(text = forecast5DayError, color = Color.Red)
                }
                forecast5Day?.list != null -> {
                    Text(
                        text = "7-Day Forecast",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val grouped = forecast5Day.list.groupBy { item ->
                            SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(item.dt * 1000L))
                        }
                        grouped.forEach { (dayOfWeek, items) ->
                            val first = items.firstOrNull()
                            val temp = first?.main?.temp?.let { String.format(Locale.getDefault(), "%.1f°C", it) } ?: "-"
                            val desc = first?.weather?.firstOrNull()?.description ?: "-"
                            val icon = first?.weather?.firstOrNull()?.icon
                            Card(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(220.dp),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = dayOfWeek,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF0288D1)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (icon != null) {
                                        val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                                        androidx.compose.foundation.Image(
                                            painter = coil.compose.rememberAsyncImagePainter(iconUrl),
                                            contentDescription = null,
                                            modifier = Modifier.size(56.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = temp,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0288D1)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = desc,
                                        fontSize = 14.sp,
                                        color = Color(0xFF616161),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Humidity: ${first?.main?.humidity ?: "-"}%",
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Wind: ${first?.wind?.speed ?: "-"} m/s",
                                        fontSize = 12.sp
                                    )
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