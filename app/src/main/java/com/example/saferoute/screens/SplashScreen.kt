package com.example.saferoute.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saferoute.AuthManager
import com.example.saferoute.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFB3E5FC), Color(0xFFFCE4EC))
    )

    LaunchedEffect(Unit) {
        delay(2000) // Splash delay

        // Safely get current user
        val currentUser = try {
            AuthManager.getCurrentUser()
        } catch (e: Exception) {
            null
        }

        if (currentUser != null) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {

            // App Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Safe Route",
                fontSize = 32.sp,
                color = Color.Black
            )

            // Subtitle
            Text(
                text = "Navigate Safely, Arrive Confidently",
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Features row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureItem(R.drawable.safe_routes, "Safe Routes")
                FeatureItem(R.drawable.shield, "Live Tracking")
                FeatureItem(R.drawable.contact, "Family Connect")
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Footer text
            Text(
                text = "Your safety is our priority",
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun FeatureItem(icon: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Black)
    }
}