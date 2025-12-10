package com.example.saferoute.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RecordPermissionRequest(
    onPermissionGranted: () -> Unit
) {
    // get context
    val context = LocalContext.current

    // permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) onPermissionGranted()
            else Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    )

    // check permission
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // already granted
            onPermissionGranted()
        } else {
            // request permission
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}