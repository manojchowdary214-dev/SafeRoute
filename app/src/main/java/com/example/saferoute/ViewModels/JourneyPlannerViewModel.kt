package com.example.saferoute.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.RouteDao
import com.example.saferoute.data.RouteEntity
import com.example.saferoute.repo.FirebaseRepository
import kotlinx.coroutines.launch

class JourneyPlannerViewModel(
    application: Application,
    private val routeDao: RouteDao,                     // local dao
    private val firebaseRepository: FirebaseRepository  // remote repo
) : AndroidViewModel(application) {

    fun saveRoute(route: RouteEntity) {
        viewModelScope.launch {
            // save locally
            routeDao.insertRoute(route)
            // save remotely
            firebaseRepository.saveRoute(route)
        }
    }

    class Factory(
        private val routeDao: RouteDao,
        private val firebaseRepository: FirebaseRepository,
        private val app: Application
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            // create viewmodel
            return JourneyPlannerViewModel(app, routeDao, firebaseRepository) as T
        }
    }
}