package com.example.saferoute.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saferoute.ViewModels.JourneyPlannerViewModel
import com.example.saferoute.data.Route
import com.example.saferoute.data.RouteDao
import com.example.saferoute.repo.FirebaseRepository
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyPlannerScreen(
    navController: NavController,
    routeDao: RouteDao,
    firebaseRepository: FirebaseRepository
) {
    val context = LocalContext.current

    // Check location permission
    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        locationPermissionGranted = granted
    }

    // Request permission if not granted
    LaunchedEffect(locationPermissionGranted) {
        if (!locationPermissionGranted) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val viewModel: JourneyPlannerViewModel = viewModel(
        factory = JourneyPlannerViewModel.Factory(routeDao, firebaseRepository)
    )
    val routes by viewModel.routes.collectAsState()

    var fromLocation by remember { mutableStateOf(TextFieldValue("")) }
    var toLocation by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan Your Journey") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF8E24AA),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                "Find the safest route to your destination",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = fromLocation,
                onValueChange = { fromLocation = it },
                label = { Text("From") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = toLocation,
                onValueChange = { toLocation = it },
                label = { Text("To") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (fromLocation.text.isNotBlank() && toLocation.text.isNotBlank()) {
                        viewModel.fetchRoutes(fromLocation.text, toLocation.text)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA))
            ) {
                Text("Find Safe Route", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (locationPermissionGranted) {
                // Map
                val defaultLocation = LatLng(1.3521, 103.8198)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                    cameraPositionState = cameraPositionState
                ) {
                    // TODO: Add polylines for each route
                }
            } else {
                Text(
                    "Location permission required to show map",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show routes
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(routes) { route ->
                    RouteCard(route)
                }
            }
        }
    }
}

@Composable
fun RouteCard(route: Route) {
    val scoreColor = when {
        route.safetyScore >= 80 -> Color(0xFF4CAF50)
        route.safetyScore >= 50 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navigate to RouteDetails */ },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${route.start} → ${route.end}", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Distance: ${route.distance} km | Duration: ${route.duration} min",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Box(
                modifier = Modifier
                    .background(scoreColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("${route.safetyScore}/100", color = Color.White)
            }
        }
    }
}