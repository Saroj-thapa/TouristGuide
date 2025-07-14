package com.example.touristguide.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.touristguide.ui.auth.ForgotPasswordScreen
import com.example.touristguide.ui.auth.LoginScreen
import com.example.touristguide.ui.auth.SignupScreen
import com.example.touristguide.ui.home.HomeScreen
import com.example.touristguide.ui.profile.ProfileScreen
import com.example.touristguide.ui.screen.*
import com.example.touristguide.ui.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val PLACES = "places"
    const val TREKKING = "trekking"
    const val HOTELS = "hotels"
    const val FOOD = "food"
    const val WEATHER = "weather"
    const val TRANSPORT = "transport"
    const val HOSPITAL = "hospital"
    const val BUDGET = "budget"
    const val EMERGENCY = "emergency"
}

@Composable
fun NavigationGraph(navController: NavHostController, startDestination: String = Routes.SPLASH) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash screen
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        // Auth Flow
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.SIGNUP) {
            SignupScreen(navController)
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController)
        }

        // Main Flow
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }

        // Category Screens
        composable(Routes.PLACES) {
            PlacesScreen(navController)
        }
        composable(Routes.TREKKING) {
            TrekkingScreen(navController)
        }
        composable(Routes.HOTELS) {
            HotelsScreen(navController)
        }
        composable(Routes.FOOD) {
            FoodScreen(navController)
        }
        composable(Routes.WEATHER) {
            WeatherScreen(navController)
        }
        composable(Routes.TRANSPORT) {
            TransportScreen(navController)
        }
        composable(Routes.HOSPITAL) {
            HospitalScreen(navController)
        }
        composable(Routes.BUDGET) {
            BudgetScreen(navController)
        }
        composable(Routes.EMERGENCY) {
            HelpScreen(navController)
        }
        composable("restaurantDetails/{restaurantName}", arguments = listOf(navArgument("restaurantName") { type = NavType.StringType })) { backStackEntry ->
            val restaurantName = backStackEntry.arguments?.getString("restaurantName") ?: "Restaurant"
            com.example.touristguide.ui.screen.RestaurantDetailScreen(restaurantName, navController)
        }
        composable("hotelDetails/{hotelId}", arguments = listOf(navArgument("hotelId") { type = NavType.StringType })) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: "1"
            com.example.touristguide.ui.screen.HotelDetailsScreen(hotelId, navController)
        }
    }
}
