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
        listOf(Color(0xFFB3E5FC), Color(0xFFFCE4EC))
    )

    LaunchedEffect(Unit) {
        delay(2000)

        val currentUser = AuthManager.getCurrentUser()

        if (currentUser != null) {
            // User already logged in Go Home (remove splash)
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // Not logged in Go Login (DON'T remove splash)
            navController.navigate("login")
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
                .padding(24.dp)
        ) {

            // APP Logo
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

            // Sub-title
            Text(
                text = "Navigate Safely, Arrive Confidently",
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureItem(R.drawable.safe_routes, "Safe Routes")
                FeatureItem(R.drawable.shield, "Live Tracking")
                FeatureItem(R.drawable.contact, "Family Connect")
            }

            Spacer(modifier = Modifier.height(62.dp))

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