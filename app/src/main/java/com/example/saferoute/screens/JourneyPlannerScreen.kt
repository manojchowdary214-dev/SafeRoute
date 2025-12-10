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
import kotlin.math.*

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

    // start input
    var start by remember { mutableStateOf("") }

    // end input
    var end by remember { mutableStateOf("") }

    // routes list
    var generatedRoutes by remember { mutableStateOf(listOf<RouteEntity>()) }

    // start/end latlng
    var routeLatLng by remember { mutableStateOf<RouteLatLng?>(null) }

    Scaffold(
        // TopBar
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Journey Planner", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    // back arrow
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // update start
            OutlinedTextField(
                value = start,
                onValueChange = { start = it },
                label = { Text("Start Point") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))                       // spacing

            // update end
            OutlinedTextField(
                value = end,
                onValueChange = { end = it },
                label = { Text("Destination") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {

                        // start geocode
                        val fromLatLng = geocodeAddress(context, start)
                        // end geocode
                        val toLatLng = geocodeAddress(context, end)

                        if (fromLatLng != null && toLatLng != null) {

                            routeLatLng = RouteLatLng(fromLatLng, toLatLng)

                            // calc distance
                            val kmDistance = calculateDistanceKm(fromLatLng, toLatLng)
                            // calc duration
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

                            // save route
                            routeDao.insertRoute(route)
                            // update list
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

            Spacer(Modifier.height(24.dp))

            // Show mini map
            routeLatLng?.let {
                MapPreviewCard(
                    // map start
                    startLatLng = it.startLatLng,
                    // map end
                    endLatLng = it.endLatLng
                )
                Spacer(Modifier.height(24.dp))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
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

                            Text("From: ${route.start}", style = MaterialTheme.typography.bodyLarge) // from
                            Text("To: ${route.end}", style = MaterialTheme.typography.bodyLarge)      // to
                            Spacer(Modifier.height(6.dp))

                            Text("Distance: ${"%.2f".format(route.distance)} km", style = MaterialTheme.typography.bodyMedium)
                            Text("Duration: ${route.duration} mins", style = MaterialTheme.typography.bodyMedium)
                            Text("Safety Score: ${route.safetyScore}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = { navController.navigate("liveJourney/${route.id}") }, // start journey
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
}

data class RouteLatLng(
    val startLatLng: LatLng,
    val endLatLng: LatLng
)