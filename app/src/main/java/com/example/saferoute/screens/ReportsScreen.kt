package com.example.saferoute.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saferoute.ViewModels.ReportsViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val sosCount by viewModel.sosCount.collectAsState(initial = 0L)
    val feedbacks by viewModel.feedbacks.collectAsState(initial = emptyList())

    // Refresh reports
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModel.refreshReports()
        }
    }

    val feedbackCount = feedbacks.size

    Scaffold(
        topBar = {
            // Topbar
            TopAppBar(
                title = { Text("Reports") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back") // back arrow
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Total SOS Sent
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("sosHistory") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total SOS Sent", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("$sosCount", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Feedback Submitted
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("feedbackList") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Feedback Submitted", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("$feedbackCount feedback(s)", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Feedback Details
                Text("Feedback Details", style = MaterialTheme.typography.titleLarge)
                if (feedbacks.isEmpty()) {
                    Text("No feedbacks submitted yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(feedbacks) { feedback ->
                            val routeId = feedback.routeId
                            if (!routeId.isNullOrEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("feedback/$routeId")
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        // Route ID
                                        Text("Route ID: $routeId", style = MaterialTheme.typography.bodyMedium)

                                        // Feedback rating
                                        Text("Rating: ${feedback.rating}", style = MaterialTheme.typography.bodyMedium)

                                        // Feedback notes
                                        Text("Notes: ${feedback.notes ?: "-"}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}