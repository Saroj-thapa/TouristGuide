package com.example.touristguide

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.touristguide.guest.GuestManager
import com.example.touristguide.navigation.NavigationGraph
import com.example.touristguide.navigation.Routes
import com.example.touristguide.ui.theme.TouristGuideTheme
import com.example.touristguide.viewmodel.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.touristguide.ui.auth.isLoggedIn

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val locationViewModel: LocationViewModel by viewModels()
        setContent {
            TouristGuideTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

                    // Request permission on first launch
                    LaunchedEffect(Unit) {
                        if (locationPermissionState.status is PermissionStatus.Denied) {
                            locationPermissionState.launchPermissionRequest()
                        }
                    }

                    // When permission is granted, fetch location and update ViewModel
                    LaunchedEffect(locationPermissionState.status) {
                        if (locationPermissionState.status is PermissionStatus.Granted) {
                            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                if (loc != null) {
                                    locationViewModel.updateLocation(loc.latitude, loc.longitude)
                                }
                            }
                        }
                    }

                    val rememberMe = com.example.touristguide.ui.auth.isRememberMeEnabled(context)
                    val startDestination = if (rememberMe) Routes.HOME else Routes.LOGIN
                    NavigationGraph(navController, startDestination = startDestination)
                }
            }
        }
    }
}
