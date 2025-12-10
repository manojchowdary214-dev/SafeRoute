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
    val feedbacks = viewModel.feedbacks.collectAsState().value  // observe feedbacks

    Scaffold(
        // Top bar
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("All Feedbacks", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // back nav
                        // back arrow
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            if (feedbacks.isEmpty()) {
                Text(
                    "No feedbacks submitted yet.",                      // empty text
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(feedbacks) { feedback ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("feedback/${feedback.routeId}") // navigate
                                },
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    // route id
                                    "Route ID: ${feedback.routeId}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                // rating
                                Text(
                                    "Rating: ${feedback.rating}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                // notes
                                Text(
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
}