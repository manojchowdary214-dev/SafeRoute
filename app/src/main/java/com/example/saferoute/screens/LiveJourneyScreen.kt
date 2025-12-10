package com.example.saferoute.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saferoute.ViewModels.LiveJourneyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveJourneyScreen(
    navController: NavController,
    viewModel: LiveJourneyViewModel,
    modifier: Modifier = Modifier
) {
    val routeProgress by viewModel.routeProgress.collectAsState()   // observe progress

    Scaffold(
        topBar = {
            // Top Bar
            TopAppBar(
                // top title
                title = { Text("Live Journey") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopJourney()
                        navController.popBackStack()
                    }) {
                        // back arrow
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                verticalArrangement = Arrangement.Center,             // center content
                horizontalAlignment = Alignment.CenterHorizontally    // center alignment
            ) {
                Text(
                    // progress text
                    "Current Progress: $routeProgress%",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))           // spacing

                // progress bar
                LinearProgressIndicator(
                    progress = routeProgress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )

                Spacer(modifier = Modifier.height(24.dp))           // spacing

                Button(
                    onClick = {
                        viewModel.stopJourney()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // button text
                    Text("Stop Journey")
                }
            }
        }
    )
}