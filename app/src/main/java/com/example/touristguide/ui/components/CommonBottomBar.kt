package com.example.touristguide.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.touristguide.navigation.Routes

@Composable
fun CommonBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == Routes.HOME } == true,
            onClick = { 
                // Clear the entire back stack and navigate to home
                navController.navigate(Routes.HOME) {
                    popUpTo(0) // Clear the entire back stack
                    launchSingleTop = true
                }
            },
            icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                    Text("Home", fontSize = 12.sp)
                }
            },
            label = null
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == Routes.PLACES } == true,
            onClick = { 
                navController.navigate(Routes.PLACES) {
                    // Pop up to home and save its state
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Place, contentDescription = "Places")
                    Text("Places", fontSize = 12.sp)
                }
            },
            label = null
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == Routes.HOTELS } == true,
            onClick = { 
                navController.navigate(Routes.HOTELS) {
                    // Pop up to home and save its state
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Home, contentDescription = "Hotels")
                    Text("Hotels", fontSize = 12.sp)
                }
            },
            label = null
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == Routes.PROFILE } == true,
            onClick = { 
                navController.navigate(Routes.PROFILE) {
                    // Pop up to home and save its state
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                    Text("Profile", fontSize = 12.sp)
                }
            },
            label = null
        )
    }
} 