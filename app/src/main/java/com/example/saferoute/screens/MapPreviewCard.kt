package com.example.saferoute.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.*

@Composable
fun MapPreviewCard(startLatLng: LatLng, endLatLng: LatLng) {

    // Camera state
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLatLng, 12f)
    }

    // Remember marker states
    val startMarkerState = remember { MarkerState(position = startLatLng) }
    val endMarkerState = remember { MarkerState(position = endLatLng) }

    // Remember polyline points
    val routePoints = remember { listOf(startLatLng, endLatLng) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Markers
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState
        ) {
            Marker(
                state = startMarkerState,
                title = "Start"
            )

            Marker(
                state = endMarkerState,
                title = "End"
            )

            // Polyline
            Polyline(
                points = routePoints,
                color = androidx.compose.ui.graphics.Color.Blue,
                width = 8f
            )
        }
    }
}