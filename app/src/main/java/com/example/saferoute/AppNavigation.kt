package com.example.saferoute

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saferoute.ViewModels.*
import com.example.saferoute.data.*
import com.example.saferoute.repo.*
import com.example.saferoute.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    routeDao: RouteDao,
    feedbackDao: FeedbackDao,
    sosDao: SosDao,
    firebaseRepository: FirebaseRepository
) {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as Application

    val routeRepository = RouteRepository(routeDao) // Route repo
    val feedbackRepository = FeedbackRepository(feedbackDao) // Feedback repo
    val sosRepository = SosRepository(application, sosDao) // SOS repo

    NavHost(navController = navController, startDestination = "splash") {

        // Auth Screens
        composable("splash") { SplashScreen(navController) } // Splash
        composable("login") { LoginScreen(navController) } // Login
        composable("register") { RegisterScreen(navController) } // Register

        // HomeScreen
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(routeDao, application)
            )
            HomeScreen(navController = navController, homeViewModel = homeViewModel) // Home
        }

        // SOS History Screen
        composable("sosHistory") {
            val sosHistoryViewModel: SosHistoryViewModel = viewModel(
                factory = SosHistoryViewModel.Factory(sosRepository)
            )
            val sosHistory = sosHistoryViewModel.sosHistory.collectAsState(initial = emptyList())
            SosHistoryScreen(
                sosRecords = sosHistory.value,
                onAudioClick = { url -> /* TODO: play audio */ },
                modifier = Modifier
            )
        }

        // Journey Planner Screen
        composable("journeyPlanner") {
            JourneyPlannerScreen(
                navController = navController,
                routeDao = routeDao,
                firebaseRepository = firebaseRepository,
                modifier = Modifier
            )
        }

        // Live Journey Screen
        composable("liveJourney/{routeId}") { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId")
            if (!routeId.isNullOrEmpty()) {
                val liveJourneyViewModel: LiveJourneyViewModel = viewModel(
                    factory = LiveJourneyViewModel.Factory(application, routeDao, firebaseRepository, routeId)
                )
                LiveJourneyScreen(navController = navController, viewModel = liveJourneyViewModel)
            } else navController.popBackStack() // Invalid route
        }

        // SOS Screen
        composable("sos") {
            val sosViewModel: SosViewModel = viewModel(
                factory = SosViewModel.Factory(application, sosRepository)
            )
            SosScreen(
                viewModel = sosViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // Feedback Screen
        composable("feedback/{routeId}") { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId")
            if (routeId.isNullOrEmpty()) {
                Toast.makeText(application, "Invalid route ID", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Invalid
            } else {
                val feedbackViewModel: FeedbackViewModel = viewModel(
                    factory = FeedbackViewModel.Factory(
                        application,
                        feedbackRepository,
                        firebaseRepository,
                        routeRepository,
                        routeId
                    )
                )
                FeedbackScreen(
                    viewModel = feedbackViewModel,
                    navController = navController,
                    modifier = Modifier
                )
            }
        }

        // General Feedback
        composable("feedback/general") {
            val feedbackViewModel: FeedbackViewModel = viewModel(
                factory = FeedbackViewModel.Factory(
                    application,
                    feedbackRepository,
                    firebaseRepository,
                    routeRepository,
                    "general"
                )
            )
            FeedbackScreen(
                viewModel = feedbackViewModel,
                navController = navController,
                modifier = Modifier
            )
        }

        // Feedback list screen
        composable("feedbackList") {
            val feedbackListViewModel: FeedbackListViewModel = viewModel(
                factory = FeedbackListViewModel.Factory(application, feedbackRepository, firebaseRepository)
            )
            FeedbackListScreen(
                navController = navController,
                viewModel = feedbackListViewModel
            )
        }

        // Reports Screen
        composable("reports") {
            val auth = firebaseRepository.auth
            val reportsViewModel: ReportsViewModel = viewModel(
                factory = ReportsViewModel.Factory(firebaseRepository, sosRepository, auth)
            )

            LaunchedEffect(auth.currentUser?.uid) {
                reportsViewModel.refreshReports() // Refresh
            }

            ReportsScreen(
                viewModel = reportsViewModel,
                navController = navController,
                modifier = Modifier
            )
        }
    }
}