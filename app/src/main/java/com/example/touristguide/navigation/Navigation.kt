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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.touristguide.viewmodel.HotelsViewModel
import com.example.touristguide.viewmodel.BookmarksViewModel
import androidx.compose.runtime.collectAsState

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
    const val BOOKMARKS = "bookmarks"
}

@Composable
fun NavigationGraph(navController: NavHostController, startDestination: String = Routes.SPLASH) {
    val hotelsViewModel: HotelsViewModel = viewModel()
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
        composable(
            "places?placeName={placeName}",
            arguments = listOf(navArgument("placeName") { nullable = true })
        ) { backStackEntry ->
            val placeName = backStackEntry.arguments?.getString("placeName")
            PlacesScreen(navController, placeName = placeName)
        }
        composable(Routes.TREKKING) {
            TrekkingScreen(navController)
        }
        composable(Routes.HOTELS) {
            HotelsScreen(navController, hotelsViewModel = hotelsViewModel)
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
        composable(Routes.BOOKMARKS) {
            val bookmarksViewModel: BookmarksViewModel = viewModel()
            val bookmarks = bookmarksViewModel.bookmarks.collectAsState().value
            com.example.touristguide.ui.screen.BookMarkScreen(bookmarks = bookmarks, navController = navController)
        }
        composable("hotelDetails/{hotelId}", arguments = listOf(navArgument("hotelId") { type = NavType.StringType })) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: "1"
            com.example.touristguide.ui.screen.HotelDetailsScreen(hotelId, navController, hotelsViewModel = hotelsViewModel)
        }
    }
}
