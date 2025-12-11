package com.example.saferoute.screens

import android.location.Geocoder
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saferoute.data.RouteDao
import com.example.saferoute.data.RouteEntity
import com.example.saferoute.repo.FirebaseRepository
import com.example.saferoute.utils.calculateDistanceKm
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyPlannerScreen(
    navController: NavController,
    routeDao: RouteDao,
    firebaseRepository: FirebaseRepository,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var generatedRoutes by remember { mutableStateOf(listOf<RouteEntity>()) }
    var routeLatLng by remember { mutableStateOf<RouteLatLng?>(null) }

    Scaffold(
        // Topbar
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Journey Planner", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                // topbar colors
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Start Field
            item {
                OutlinedTextField(
                    value = start,
                    onValueChange = { start = it },
                    label = { Text("Start Point") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // End Field
            item {
                OutlinedTextField(
                    value = end,
                    onValueChange = { end = it },
                    label = { Text("Destination") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Generate Button
            item {
                Button(
                    onClick = {
                        scope.launch {
                            val fromLatLng = geocodeAddress(context, start)
                            val toLatLng = geocodeAddress(context, end)

                            if (fromLatLng != null && toLatLng != null) {

                                routeLatLng = RouteLatLng(fromLatLng, toLatLng)

                                val kmDistance = calculateDistanceKm(fromLatLng, toLatLng)
                                val durationMinutes = ((kmDistance / 5.0) * 60).toInt()

                                val route = RouteEntity(
                                    id = System.currentTimeMillis().toString(),
                                    start = start,
                                    end = end,
                                    distance = kmDistance,
                                    duration = durationMinutes,
                                    safetyScore = (1..5).random(),
                                    timestamp = System.currentTimeMillis()
                                )

                                routeDao.insertRoute(route)
                                generatedRoutes = listOf(route)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Generate Route")
                }
            }

            // Map Preview
            routeLatLng?.let { latData ->
                item {
                    MapPreviewCard(
                        startLatLng = latData.startLatLng,
                        endLatLng = latData.endLatLng
                    )
                }
            }

            // Generated Routes List
            items(generatedRoutes) { route ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        // From
                        Text("From: ${route.start}", style = MaterialTheme.typography.bodyLarge)
                        // to
                        Text("To: ${route.end}", style = MaterialTheme.typography.bodyLarge)

                        Spacer(Modifier.height(6.dp)) // spacer

                        // distance
                        Text(
                            "Distance: ${"%.2f".format(route.distance)} km",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        // duration
                        Text("Duration: ${route.duration} mins", style = MaterialTheme.typography.bodyMedium)
                        // safety score
                        Text("Safety Score: ${route.safetyScore}", style = MaterialTheme.typography.bodyMedium)

                        Spacer(Modifier.height(12.dp))

                        // start journey button
                        Button(
                            onClick = { navController.navigate("liveJourney/${route.id}") },
                            modifier = Modifier.align(Alignment.End),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Start Journey")
                        }
                    }
                }
            }
        }
    }
}

data class RouteLatLng(
    val startLatLng: LatLng,
    val endLatLng: LatLng
)