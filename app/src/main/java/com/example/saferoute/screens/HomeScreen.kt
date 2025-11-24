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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saferoute.ViewModels.HomeViewModel
import com.example.saferoute.data.Route

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val recentJourneys by homeViewModel.recentJourneys.collectAsState()
    val gradient = Brush.horizontalGradient(
        listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC))
    )

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            homeViewModel.fetchLocation()
        } else {
            homeViewModel.setLocationStatus("Location Permission Denied")
        }
    }

    // Request permission on first composition
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Greeting Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Hi ${homeViewModel.username}!",
                        color = Color.White,
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    Text(
                        text = "Ready to travel safely today?",
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.align(Alignment.BottomStart)
                    )

                    Text(
                        text = "Current Location: ${homeViewModel.currentLocation}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Cards
            Row(
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
                    onClick = { /* TODO */ },
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Recent Journeys", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // List of recent journeys
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recentJourneys) { route ->
                    RecentJourneyItem(route)
                }
            }
        }
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
                .background(color)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = Color.White, fontSize = 16.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RecentJourneyItem(route: Route) {
    val scoreColor = when {
        route.safetyScore >= 80 -> Color(0xFF4CAF50)
        route.safetyScore >= 50 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Text("${route.start} → ${route.end}", fontSize = 16.sp)
                Text(
                    "Distance: ${route.distance} km  |  Duration: ${route.duration} min",
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .background(scoreColor, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Text("${route.safetyScore}/100", color = Color.White)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("journeyPlanner") },
            icon = { Icon(Icons.Default.Place, contentDescription = "Map") },
            label = { Text("Map") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: navigate to alert */ },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Alert") },
            label = { Text("Alert") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: navigate to profile */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}