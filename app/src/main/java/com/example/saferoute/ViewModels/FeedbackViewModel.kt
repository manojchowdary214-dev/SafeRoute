package com.example.saferoute.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.FeedbackEntity
import com.example.saferoute.data.RouteEntity
import com.example.saferoute.repo.FeedbackRepository
import com.example.saferoute.repo.FirebaseRepository
import com.example.saferoute.repo.RouteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel(
    application: Application,
    private val feedbackRepo: FeedbackRepository,
    private val firebaseRepo: FirebaseRepository,
    private val routeRepo: RouteRepository,
    private val routeId: String
) : AndroidViewModel(application) {

    private val _routeDetails = MutableStateFlow<RouteEntity?>(null)   // route state
    val routeDetails: StateFlow<RouteEntity?> = _routeDetails         // public route

    // initial load
    init {
        loadRouteDetails()
    }

    private fun loadRouteDetails() {
        viewModelScope.launch {
            // fetch route
            val route = routeRepo.getRouteById(routeId)
            // update state
            _routeDetails.value = route
        }
    }

    fun submitFeedback(rating: Int, notes: String?) {
        viewModelScope.launch {
            val userId = firebaseRepo.auth.currentUser?.uid ?: return@launch  // get uid

            val feedback = FeedbackEntity(
                userId = userId,
                routeId = routeId,
                rating = rating,
                notes = notes,
                createdAt = System.currentTimeMillis()   // timestamp
            )

            // save locally
            feedbackRepo.addFeedback(feedback)

            // save firestore
            firebaseRepo.sendFeedbackToFirestore(feedback)
        }
    }

    fun deleteRoute(routeId: String) {
        viewModelScope.launch {
            // delete route
            routeRepo.deleteRouteById(routeId)
            _routeDetails.value = null // clear state
        }
    }

    class Factory(
        private val app: Application,
        private val feedbackRepo: FeedbackRepository,
        private val firebaseRepo: FirebaseRepository,
        private val routeRepo: RouteRepository,
        private val routeId: String
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            // create vm
            return FeedbackViewModel(app, feedbackRepo, firebaseRepo, routeRepo, routeId) as T
        }
    }
}