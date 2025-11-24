package com.example.saferoute

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saferoute.ViewModels.HomeViewModel
import com.example.saferoute.ViewModels.LiveJourneyViewModel
import com.example.saferoute.data.RouteDao
import com.example.saferoute.repo.FirebaseRepository
import com.example.saferoute.screens.HomeScreen
import com.example.saferoute.screens.JourneyPlannerScreen
import com.example.saferoute.screens.LoginScreen
import com.example.saferoute.screens.SplashScreen
import com.example.saferoute.screens.LiveJourneyScreen

@Composable
fun AppNavigation(
    routeDao: RouteDao,
    firebaseRepository: FirebaseRepository
) {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as Application

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(
                    routeDao = routeDao,
                    application = application
                )
            )

            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel
            )
        }

        composable("journeyPlanner") {
            JourneyPlannerScreen(
                navController = navController,
                routeDao = routeDao,
                firebaseRepository = firebaseRepository
            )
        }

        // NEW: Live Journey Tracking Screen
        composable("liveJourney/{routeId}") { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: return@composable

            val liveJourneyViewModel: LiveJourneyViewModel = viewModel(
                factory = LiveJourneyViewModel.Factory(
                    routeDao = routeDao,
                    firebaseRepository = firebaseRepository,
                    routeId = routeId,
                    application = application
                )
            )

            LiveJourneyScreen(
                navController = navController,
                liveJourneyViewModel = liveJourneyViewModel
            )
        }
    }
}
