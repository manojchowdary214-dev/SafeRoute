package com.example.saferoute

import androidx.compose.runtime.Composable
<<<<<<< HEAD
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saferoute.screens.HomeScreen
=======
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saferoute.ViewModels.HomeViewModel
import com.example.saferoute.data.RouteDao
import com.example.saferoute.repo.FirebaseRepository
import com.example.saferoute.screens.HomeScreen
import com.example.saferoute.screens.JourneyPlannerScreen
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
import com.example.saferoute.screens.LoginScreen
import com.example.saferoute.screens.SplashScreen

@Composable
<<<<<<< HEAD
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
=======
fun AppNavigation(
    routeDao: RouteDao,
    firebaseRepository: FirebaseRepository
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("home") {
            // Create HomeViewModel using the Factory with routeDao
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(routeDao)
            )
            HomeScreen(navController = navController, homeViewModel = homeViewModel)
        }

        composable("journeyPlanner") {
            JourneyPlannerScreen(
                navController = navController,
                routeDao = routeDao,
                firebaseRepository = firebaseRepository
            )
        }
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
    }
}
