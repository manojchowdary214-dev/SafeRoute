package com.example.saferoute.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.saferoute.data.SosRecord
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosHistoryScreen(
    sosRecords: List<SosRecord>,
    modifier: Modifier = Modifier,
    onAudioClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        // top bar
        topBar = {
            TopAppBar(
                title = { Text("SOS History") },
                navigationIcon = {
                    // Back Arrow
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                // Top bar colors
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
                    .padding(16.dp)
            ) {
                if (sosRecords.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No SOS records found.")
                    }
                } else {
                    // scroll list
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(sosRecords) { record ->
                            Card(
                                // rounded card
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), // card color
                                modifier = Modifier
                                    .fillMaxWidth()
                                    // card clickable
                                    .clickable { /* Navigate to details if needed */ }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Message: ${record.message.ifBlank { "-" }}")
                                    Text(
                                        // timestamp
                                        "Time: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(record.timestamp))}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    // location
                                    Text("Location: ${record.latitude ?: "-"}, ${record.longitude ?: "-"}")
                                    record.audioPath?.let { url ->
                                        Text(
                                            text = "▶ Play Audio",
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clickable { onAudioClick(url) }
                                                .padding(vertical = 4.dp)
                                        )
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