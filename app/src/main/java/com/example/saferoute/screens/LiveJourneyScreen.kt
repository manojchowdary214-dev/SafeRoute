package com.example.saferoute.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.saferoute.ViewModels.LiveJourneyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@Composable
fun LiveJourneyScreen(
    navController: NavController,
    liveJourneyViewModel: LiveJourneyViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    val location by liveJourneyViewModel.currentLocation.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                loc?.let { liveJourneyViewModel.updateLocation(it) }
            }
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (location != null) {
            val userLatLng = LatLng(location!!.latitude, location!!.longitude)
            val cameraState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(userLatLng, 16f)
            }
            val markerState = remember { MarkerState(position = userLatLng) }

            LaunchedEffect(userLatLng) {
                markerState.position = userLatLng
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState
            ) {
                Marker(
                    state = markerState,
                    title = "You are here"
                )
            }
        } else {
            Text(text = "Fetching your location...")
        }
    }
}
