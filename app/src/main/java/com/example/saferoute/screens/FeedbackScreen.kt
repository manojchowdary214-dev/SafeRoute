package com.example.saferoute.screens

import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saferoute.ViewModels.FeedbackViewModel
import com.example.saferoute.data.RouteEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    viewModel: FeedbackViewModel,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val route: RouteEntity? = viewModel.routeDetails.collectAsState(initial = null).value

    var rating by remember { mutableStateOf(0f) }
    var notes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var startLatLng by remember { mutableStateOf<LatLng?>(null) }
    var endLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Load geocode for map
    LaunchedEffect(route) {
        if (route != null) {
            scope.launch {
                startLatLng = geocodeAddress(context, route.start)
                endLatLng = geocodeAddress(context, route.end)
            }
        }
    }

    Scaffold(
        // TopApp Bar
        topBar = {
            TopAppBar(
                title = { Text("Route Feedback") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (route != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Route")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        // Scroll State
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Route Details
            route?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Route Details", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("From: ${it.start}")
                        Text("To: ${it.end}")
                        Text("Distance: ${it.distance} km")
                        Text("Duration: ${it.duration} mins")
                        Text("Safety Score: ${it.safetyScore}/100")
                    }
                }

                // Mini Map Preview
                if (startLatLng != null && endLatLng != null) {
                    MapPreviewCard(
                        startLatLng = startLatLng!!,
                        endLatLng = endLatLng!!
                    )
                }

                // Start Journey Button
                Button(
                    onClick = { navController.navigate("liveJourney/${it.id}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Journey")
                }
            }

            // Rating
            Text("Rating: ${rating.toInt()}", style = MaterialTheme.typography.bodyLarge)

            Slider(
                value = rating,
                onValueChange = { rating = it },
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            // Submit Button
            Button(
                onClick = {
                    isSubmitting = true
                    viewModel.submitFeedback(rating.toInt(), notes)
                    isSubmitting = false
                    navController.popBackStack()
                },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSubmitting) "Submitting..." else "Submit")
            }
        }
    }

    // Delete Dialog
    if (showDeleteDialog && route != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Route?") },
            text = { Text("Are you sure you want to delete this route? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRoute(route.id)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}