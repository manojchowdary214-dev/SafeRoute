package com.example.saferoute.ViewModels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.RouteDao
import com.example.saferoute.repo.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LiveJourneyViewModel(
    application: Application,
    private val routeDao: RouteDao,
    private val firebaseRepository: FirebaseRepository,
    private val routeId: String
) : AndroidViewModel(application) {

    // Holds the current location of the user
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> get() = _currentLocation

    /**
     * Updates the current location and sends it to Firebase.
     */
    fun updateLocation(location: Location) {
        _currentLocation.value = location
        viewModelScope.launch {
            try {
                firebaseRepository.updateUserLocation(routeId, location)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Factory class for creating LiveJourneyViewModel with parameters.
     */
    class Factory(
        private val application: Application,
        private val routeDao: RouteDao,
        private val firebaseRepository: FirebaseRepository,
        private val routeId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LiveJourneyViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LiveJourneyViewModel(application, routeDao, firebaseRepository, routeId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
