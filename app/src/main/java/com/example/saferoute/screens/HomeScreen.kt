package com.example.saferoute.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saferoute.ViewModels.HomeViewModel
import com.example.saferoute.data.RouteEntity

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    // Recent list
    val recentJourneys by homeViewModel.recentJourneys.collectAsState()
    // Username
    val username = homeViewModel.username ?: "User"

    val gradient = Brush.horizontalGradient(
        listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC))
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) homeViewModel.fetchLocation()
        else homeViewModel.setLocationStatus("Location Denied")
    }

    // Ask permission
    LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }

    Scaffold(bottomBar = { HomeBottomBar(navController) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            GreetingCard(username, homeViewModel, navController, gradient)

            Spacer(Modifier.height(20.dp))

            FeatureRow(navController)

            Spacer(Modifier.height(16.dp))

            // Title
            Text("Recent Journeys", fontSize = 20.sp)
            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recentJourneys) { route ->
                    RecentJourneyItem(route) {
                        // Navigate feedback
                        navController.navigate("feedback/${route.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingCard(
    username: String,
    homeViewModel: HomeViewModel,
    navController: NavController,
    gradient: Brush
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        // Small text
                        text = "Location: ${homeViewModel.currentLocation ?: "Unknown"}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Logout",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            // Logout user
                            homeViewModel.signOut()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }

                // Greeting
                Text("Hi $username!", color = Color.White, fontSize = 26.sp)

                Text(
                    "Ready to travel safely today?",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun FeatureRow(navController: NavController) {
    Row(
        // Space cards
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FeatureCard(
            modifier = Modifier.weight(1f),
            title = "Plan Journey",
            subtitle = "Safe Routes",
            onClick = { navController.navigate("journeyPlanner") },
            color = Color(0xFF8E24AA)
        )

        FeatureCard(
            modifier = Modifier.weight(1f),
            title = "Emergency SOS",
            subtitle = "Quick Help",
            onClick = { navController.navigate("sos") },
            color = Color(0xFFD32F2F)
        )
    }
}

@Composable
fun FeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color) // Card color
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Title
                Text(title, color = Color.White, fontSize = 16.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RecentJourneyItem(route: RouteEntity, onClick: () -> Unit) {

    val scoreColor = when {
        route.safetyScore >= 80 -> Color(0xFF4CAF50) // Safe
        route.safetyScore >= 50 -> Color(0xFFFFC107) // Medium
        else -> Color(0xFFF44336) // Danger
    }

    Card(
        // Clickable card
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // Route text
                Text("${route.start} → ${route.end}", fontSize = 16.sp)
                Text(
                    "Distance: ${route.distance} km | Duration: ${route.duration} min",
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(scoreColor, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                // Score text
                Text("${route.safetyScore}/100", color = Color.White)
            }
        }
    }
}

@Composable
fun HomeBottomBar(navController: NavController) {

    // Home nav
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        // SOS history
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("sosHistory") },
            icon = { Icon(Icons.Default.History, contentDescription = "SOS History") },
            label = { Text("SOS History") }
        )

        // Restore list
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("feedbackList") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Star, contentDescription = "Feedback") },
            label = { Text("Feedback") }
        )

        // Reports nav
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("reports") },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reports") },
            label = { Text("Reports") }
        )
    }
}