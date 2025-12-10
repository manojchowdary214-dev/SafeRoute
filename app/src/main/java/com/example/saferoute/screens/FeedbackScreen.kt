package com.example.saferoute.screens

import android.location.Geocoder
import androidx.compose.foundation.layout.*
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
                startLatLng = geocodeAddress(context, route.start) // geocode start
                endLatLng = geocodeAddress(context, route.end)     // geocode end
            }
        }
    }

    Scaffold(
        topBar = {
            // Top Bar
            TopAppBar(
                title = { Text("Route Feedback") },
                navigationIcon = {
                    // back arrow icon
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (route != null) {
                        // delete icon
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Route")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)       // spacing
        ) {

            // Route Details Card
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
                        // map start
                        startLatLng = startLatLng!!,
                        // map end
                        endLatLng = endLatLng!!
                    )
                }

                // start journey
                Button(
                    onClick = { navController.navigate("liveJourney/${it.id}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Journey")
                }
            }

            // update rating
            Text("Rating: ${rating.toInt()}", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = rating,
                onValueChange = { rating = it },
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            // update notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    isSubmitting = true
                    // submitting feedback
                    viewModel.submitFeedback(rating.toInt(), notes)
                    isSubmitting = false
                    navController.popBackStack()
                },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSubmitting) "Submitting..." else "Submit")        // button text
            }
        }
    }

    // DELETE DIALOG
    if (showDeleteDialog && route != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Route?") },
            text = { Text("Are you sure you want to delete this route? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    // delete route
                    viewModel.deleteRoute(route.id)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    // confirm text
                    Text("Delete")
                }
            },
            dismissButton = {
                // cancel text
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}