package com.example.saferoute.ViewModels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.RouteDao
import com.example.saferoute.repo.FirebaseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LiveJourneyViewModel(
    application: Application,
    private val routeDao: RouteDao,
    private val firebaseRepository: FirebaseRepository,
    private val routeId: String
) : AndroidViewModel(application) {

    private val _routeProgress = MutableStateFlow(0)
    val routeProgress: StateFlow<Int> = _routeProgress

    private var isJourneyActive = true

    init {
        simulateProgress()
    }

    // Journey progress
    /** ---------------- Journey progress ---------------- **/
    private fun simulateProgress() {
        viewModelScope.launch {
            while (isJourneyActive && _routeProgress.value < 100) {
                delay(1500)
                _routeProgress.value += 5
            }
        }
    }

    // Location update
    /** ----------------Location Update ---------------- **/
    fun updateLocation(location: Location) {
        viewModelScope.launch {
            firebaseRepository.updateUserLocation(routeId, location)
        }
    }

    // Stop Journey
    /** ---------------- Stop Journey---------------- **/
    fun stopJourney() {
        isJourneyActive = false
        _routeProgress.value = 0
    }

    // Factory
    /** ---------------- Factory ---------------- **/
    class Factory(
        private val app: Application,
        private val routeDao: RouteDao,
        private val firebaseRepository: FirebaseRepository,
        private val routeId: String
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return LiveJourneyViewModel(app, routeDao, firebaseRepository, routeId) as T
        }
    }
}