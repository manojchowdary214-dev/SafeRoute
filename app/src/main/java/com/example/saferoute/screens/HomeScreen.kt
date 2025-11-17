package com.example.saferoute.screens

import androidx.compose.foundation.background
<<<<<<< HEAD
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
=======
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
<<<<<<< HEAD
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saferoute.AuthManager

@Composable
fun HomeScreen(navController: NavController) {
    val gradient = Brush.horizontalGradient(listOf(Color(0xFFFF80AB), Color(0xFF7C4DFF)))

    // Extract username from email
    val userEmail = AuthManager.getCurrentUser()?.email
    val userName = userEmail?.substringBefore("@") ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Hello, $userName!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Ready to travel safely?", fontSize = 16.sp, color = Color.Gray)
            }

            Button(
                onClick = {
                    AuthManager.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Logout", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Current Location: Banjara Hills, Hyderabad",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        FeatureCard("Plan Journey", "Safe routes ahead")
        Spacer(modifier = Modifier.height(16.dp))
        FeatureCard("Emergency SOS", "Quick help available")
=======
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
    val gradient = Brush.horizontalGradient(listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC)))

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
                        "Hi ${homeViewModel.username}!",
                        color = Color.White,
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        "Ready to travel safely today?",
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                    Text(
                        "Current Location: ${homeViewModel.currentLocation}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Cards Row
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
                    onClick = {},
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Recent Journeys", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // Recent Journeys List
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recentJourneys) { route ->
                    RecentJourneyItem(route)
                }
            }
        }
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
    }
}

@Composable
<<<<<<< HEAD
fun FeatureCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$title\n$subtitle",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 16.sp
            )
        }
    }
}
=======
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
                    "Distance: ${route.distance} km, Duration: ${route.duration} min",
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
            icon = {},
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("journeyPlanner") },
            icon = {},
            label = { Text("Map") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Alert */ },
            icon = {},
            label = { Text("Alert") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Profile */ },
            icon = {},
            label = { Text("Profile") }
        )
    }
}
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
