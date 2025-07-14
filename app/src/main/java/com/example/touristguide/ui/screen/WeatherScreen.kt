package com.example.touristguide.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.location.Location
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
    val forecast = weatherViewModel.forecast.collectAsState().value
    val isLoading = weatherViewModel.isLoading.collectAsState().value
    val error = weatherViewModel.error.collectAsState().value
    val lat = locationViewModel.latitude.collectAsState().value
    val lon = locationViewModel.longitude.collectAsState().value
    val context = LocalContext.current
    val sdf = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val permissionStatus = locationPermissionState.status

    // Request permission on first launch
    LaunchedEffect(Unit) {
        if (permissionStatus is PermissionStatus.Denied) {
            locationPermissionState.launchPermissionRequest()
        }
    }
    // When permission is granted, fetch location and update ViewModel
    LaunchedEffect(permissionStatus) {
        if (permissionStatus is PermissionStatus.Granted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    locationViewModel.updateLocation(loc.latitude, loc.longitude)
                }
            }
        }
    }

    // Fetch weather when location is available
    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null) {
            weatherViewModel.fetch7DayForecast(lat, lon)
        }
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
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.padding(5.dp)
            ) {
                item {
                    if (isLoading) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (error != null) {
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else if (forecast != null && forecast.daily.isNotEmpty()) {
                        // Current Weather (today)
                        val today = forecast.daily.first()
                        Text(
                            text = "Current Weather",
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.LightGray)
                            ) {
                                // You can use an icon or image here
                                Icon(
                                    imageVector = Icons.Default.Cloud,
                                    contentDescription = "Weather Icon",
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Column {
                                Text(
                                    text = today.weather.firstOrNull()?.main ?: "-",
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                                Text(today.weather.firstOrNull()?.description ?: "-")
                                Text("${String.format("%.1f", today.temp.day - 273.15)}°C")
                            }
                        }
                        HorizontalDivider(
                            color = Color.Black.copy(alpha = 0.1f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Forecast for 7 Days",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        )
                        forecast.daily.take(7).forEachIndexed { idx, day ->
                            val date = Date(day.dt * 1000)
                            val dayName = sdf.format(date)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFFB2EBF2), Color(0xFF80DEEA))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cloud,
                                        contentDescription = "Weather Icon",
                                        tint = Color(0xFF0288D1),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Day ${idx + 1}",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0288D1)
                                        )
                                    )
                                    Text(
                                        text = dayName,
                                        style = TextStyle(
                                            fontSize = 13.sp,
                                            color = Color(0xFF616161)
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 5.dp)
                                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = day.weather.firstOrNull()?.description ?: "-",
                                            fontSize = 8.sp,
                                            color = Color.Black
                                        )
                                    }
                                    Text(
                                        text = "${String.format("%.1f", day.temp.day - 273.15)}°C",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(top = 5.dp)
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = Color.Black.copy(alpha = 0.1f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    } else if (!isLoading && error == null && (forecast == null || forecast.daily.isEmpty())) {
                        Text("No weather data available.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                    } else if (!isLoading && error != null) {
                        Text("Error: $error", color = Color.Red, modifier = Modifier.padding(8.dp))
                    } else {
                        Text(
                            text = "Waiting for location permission...",
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                        )
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