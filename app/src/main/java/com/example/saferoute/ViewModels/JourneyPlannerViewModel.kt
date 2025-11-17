package com.example.saferoute.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.Route
import com.example.saferoute.data.RouteDao
import com.example.saferoute.data.RouteEntity
import com.example.saferoute.repo.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class JourneyPlannerViewModel(private val routeDao: RouteDao, private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes

    fun fetchRoutes(start: String, end: String) {
        val safeRoute = Route(UUID.randomUUID().toString(), start, end, 5.0, 10, 90, System.currentTimeMillis())
        val easyRoute = Route(UUID.randomUUID().toString(), start, end, 6.0, 8, 70, System.currentTimeMillis())
        val list = listOf(safeRoute, easyRoute).sortedByDescending { it.safetyScore }

        viewModelScope.launch {
            list.forEach {
                routeDao.insertRoute(RouteEntity(it.id, it.start, it.end, it.distance, it.duration, it.safetyScore, it.timestamp))
                firebaseRepository.saveRoute(it)
            }
            _routes.value = list
        }
    }

    class Factory(private val routeDao: RouteDao, private val firebaseRepository: FirebaseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return JourneyPlannerViewModel(routeDao, firebaseRepository) as T
        }
    }
}