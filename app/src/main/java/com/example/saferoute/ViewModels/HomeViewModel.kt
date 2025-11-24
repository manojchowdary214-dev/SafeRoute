package com.example.saferoute.ViewModels

import android.Manifest
import android.app.Application
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.Route
import com.example.saferoute.data.RouteDao
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val routeDao: RouteDao, application: Application) : AndroidViewModel(application) {

    private val fusedLocation = LocationServices.getFusedLocationProviderClient(application)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var username by mutableStateOf("User")
        private set

    var currentLocation by mutableStateOf("Fetching location…")
        private set

    private val _recentJourneys = MutableStateFlow<List<Route>>(emptyList())
    val recentJourneys: StateFlow<List<Route>> = _recentJourneys

    init {
        loadUserName()
        loadRecentRoutes()
    }

    private fun loadUserName() {
        username = auth.currentUser?.displayName
            ?: auth.currentUser?.email?.substringBefore("@")
                    ?: "User"
    }

    private fun loadRecentRoutes() {
        viewModelScope.launch {
            routeDao.getRecentRoutes().collect { entities ->
                _recentJourneys.value = entities.map { entity ->
                    Route(
                        id = entity.id,
                        start = entity.start,
                        end = entity.end,
                        distance = entity.distance,
                        duration = entity.duration,
                        safetyScore = entity.safetyScore,
                        timestamp = entity.timestamp
                    )
                }
            }
        }
    }

    fun fetchLocation() {
        val context = getApplication<Application>()
        fusedLocation.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                getLocationName(location.latitude, location.longitude)
            } else {
                currentLocation = "Unable to fetch location"
            }
        }.addOnFailureListener {
            currentLocation = "Unable to fetch location"
        }
    }

    private fun getLocationName(lat: Double, lon: Double) {
        val context = getApplication<Application>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val result = geocoder.getFromLocation(lat, lon, 1)
                currentLocation = result?.firstOrNull()?.locality
                    ?: result?.firstOrNull()?.subLocality
                            ?: "Unknown Location"
            } catch (e: Exception) {
                currentLocation = "Unknown Location"
            }
        }
    }

    fun setLocationStatus(status: String) {
        currentLocation = status
    }

    class Factory(private val routeDao: RouteDao, private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(routeDao, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
