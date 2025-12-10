package com.example.saferoute.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.FeedbackEntity
import com.example.saferoute.repo.FeedbackRepository
import com.example.saferoute.repo.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedbackListViewModel(
    application: Application,
    private val feedbackRepo: FeedbackRepository,
    private val firebaseRepo: FirebaseRepository
) : AndroidViewModel(application) {

    private val _feedbacks = MutableStateFlow<List<FeedbackEntity>>(emptyList())  // internal state
    val feedbacks: StateFlow<List<FeedbackEntity>> get() = _feedbacks           // public state

    init {
        // initial fetch
        fetchAllFeedbacks()
    }

    private fun fetchAllFeedbacks() {
        viewModelScope.launch {
            // get uid
            val userId = firebaseRepo.auth.currentUser?.uid ?: return@launch
            // fetch feedbacks
            _feedbacks.value = feedbackRepo.getAllFeedbacksForUser(userId)
        }
    }

    class Factory(
        private val app: Application,
        private val feedbackRepo: FeedbackRepository,
        private val firebaseRepo: FirebaseRepository
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            // create vm
            return FeedbackListViewModel(app, feedbackRepo, firebaseRepo) as T
        }
    }
}