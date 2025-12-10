package com.example.saferoute.ViewModels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.RouteDao
import com.example.saferoute.data.RouteEntity
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(
    private val routeDao: RouteDao,
    application: Application
) : AndroidViewModel(application) {

    private val fusedLocation = LocationServices.getFusedLocationProviderClient(application)
    private val auth = FirebaseAuth.getInstance()

    // Username
    var username by mutableStateOf("User")
        private set

    // Location
    var currentLocation by mutableStateOf("Fetching location…")
        private set

    // Recent journeys
    private val _recentJourneys = MutableStateFlow<List<RouteEntity>>(emptyList())
    val recentJourneys: StateFlow<List<RouteEntity>> = _recentJourneys

    init {
        // Load user
        loadUserName()
        // Load routes
        loadRecentRoutes()
    }

    // Load username from firebase
    /** Load username from Firebase */
    private fun loadUserName() {
        username = auth.currentUser?.displayName
            ?: auth.currentUser?.email?.substringBefore("@")
                    ?: "User"
    }

    // Load recent journeys
    /** Load recent journeys from Room */
    private fun loadRecentRoutes() {
        viewModelScope.launch {
            routeDao.getRecentRoutes().collect { list ->
                _recentJourneys.value = list
            }
        }
    }

    // get location saftey
    /** Fetch location safely */
    fun fetchLocation() {
        val context = getApplication<Application>()

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            currentLocation = "Location Denied"
            return
        }

        try {
            fusedLocation.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        getLocationName(location.latitude, location.longitude)
                    } else {
                        currentLocation = "Unable fetch"
                    }
                }
                .addOnFailureListener {
                    currentLocation = "Unable fetch"
                }
        } catch (e: SecurityException) {
            currentLocation = "Access error"
        }
    }

    // Convert lat/lon into address
    /** Convert lat/lon → address */
    private fun getLocationName(lat: Double, lon: Double) {
        val context = getApplication<Application>()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val result = geocoder.getFromLocation(lat, lon, 1)

                currentLocation = result?.firstOrNull()?.locality
                    ?: result?.firstOrNull()?.subLocality
                            ?: "Unknown"
            } catch (e: Exception) {
                currentLocation = "Unknown"
            }
        }
    }

    // Update status
    fun setLocationStatus(status: String) {
        currentLocation = status
    }

    // Logout function
    /** Logout user */
    fun signOut() {
        auth.signOut()
        username = "User"
        currentLocation = "Fetching location…"
    }

    /** Factory */
    class Factory(
        private val routeDao: RouteDao,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(routeDao, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}