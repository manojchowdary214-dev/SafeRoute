package com.example.saferoute.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.Route
import com.example.saferoute.data.RouteDao
import com.example.saferoute.data.RouteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Simple User wrapper
data class User(
    val uid: String,
    val name: String
)

class HomeViewModel(private val routeDao: RouteDao) : ViewModel() {

    // Recent journeys as StateFlow
    private val _recentJourneys = MutableStateFlow<List<Route>>(emptyList())
    val recentJourneys: StateFlow<List<Route>> = _recentJourneys

    // User info
    var username by mutableStateOf("User")
        private set

    // Current location
    var currentLocation by mutableStateOf("Unknown")
        private set

    init {
        // Load recent journeys from DAO and map RouteEntity -> Route
        viewModelScope.launch {
            routeDao.getRecentRoutes().collect { entities: List<RouteEntity> ->
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

    // Load user info
    fun loadUser(user: User) {
        username = user.name
    }

    // Update current location
    fun updateLocation(location: String) {
        currentLocation = location
    }

    // ViewModel Factory for dependency injection
    class Factory(private val routeDao: RouteDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(routeDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}