package com.example.saferoute.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saferoute.ViewModels.FeedbackListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackListScreen(
    navController: NavController,
    viewModel: FeedbackListViewModel
) {
    val feedbacks = viewModel.feedbacks.collectAsState().value

    Scaffold(
        // Top App Bar
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("All Feedbacks") },
                navigationIcon = {
                    // Back Arrow
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (feedbacks.isEmpty()) {
                item {
                    Text(
                        "No feedbacks submitted yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {

                items(feedbacks) { feedback ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Navigate to feedback
                                navController.navigate("feedback/${feedback.routeId}")
                            },
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                // Route ID
                                "Route ID: ${feedback.routeId}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                // FeedBack Rating
                                "Rating: ${feedback.rating}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                // Feedback Notes
                                "Notes: ${feedback.notes ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}