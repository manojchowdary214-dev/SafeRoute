package com.example.saferoute.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.saferoute.ViewModels.SosViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreen(
    viewModel: SosViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.ui.collectAsState()
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Location permission is required for SOS", Toast.LENGTH_SHORT).show() // denied
        }
    }

    // Request permission on first load
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency SOS", fontWeight = FontWeight.Bold) }, // title
                navigationIcon = {
                    // back button
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFB71C1C))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // heading
            Text(
                "Send an Emergency Message",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // update message
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Type your message here") },
                placeholder = { Text("Enter your SOS message...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (message.isBlank()) {
                        Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Check location permission
                    val permissionStatus = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )

                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) { // permission granted
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                        try {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location -> // get location
                                val lat = location?.latitude                        // latitude
                                val lon = location?.longitude                       // longitude

                                // Reverse geocode to get place name
                                var placeName: String? = null
                                if (lat != null && lon != null) {
                                    placeName = try {
                                        val geocoder = Geocoder(context, Locale.getDefault())
                                        val addresses = geocoder.getFromLocation(lat, lon, 1)
                                        // get address
                                        addresses?.get(0)?.getAddressLine(0)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        null
                                    }
                                }

                                viewModel.sendTextSos(message, lat, lon)             // send sos
                                message = ""                                          // reset message

                                if (placeName != null) {
                                    // show location
                                    Toast.makeText(context, "Location: $placeName", Toast.LENGTH_SHORT).show()
                                }

                            }.addOnFailureListener {
                                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                            // denied
                            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // request again
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show()
                    }

                },
                // button state
                enabled = !uiState.sending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                // button shape
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text(
                    // button text
                    if (uiState.sending) "Sending..." else "Send SOS",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.message.isNotBlank()) {
                Text(
                    // status message
                    text = uiState.message,
                    color = if (uiState.error) Color.Red else Color(0xFF1B5E20),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .background(
                            color = if (uiState.error) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                )
            }
        }
    }
}