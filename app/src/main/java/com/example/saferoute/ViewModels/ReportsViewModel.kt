package com.example.saferoute.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.FeedbackEntity
import com.example.saferoute.repo.FirebaseRepository
import com.example.saferoute.repo.SosRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val sosRepository: SosRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _sosCount = MutableStateFlow(0L)
    val sosCount: StateFlow<Long> get() = _sosCount

    private val _feedbacks = MutableStateFlow<List<FeedbackEntity>>(emptyList())
    val feedbacks: StateFlow<List<FeedbackEntity>> get() = _feedbacks

    init {
        refreshReports()
    }

    /**
     * Refresh SOS count and feedbacks for the current logged-in user.
     * Safe: does nothing if no user is logged in.
     */
    fun refreshReports() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                // Get local SOS count
                _sosCount.value = sosRepository.getLocalSosFlow().first().size.toLong()

                // Fetch all feedbacks
                _feedbacks.value = firebaseRepository.fetchAllFeedbacks(uid)
            } catch (e: Exception) {
                // Fallback error
                _sosCount.value = 0
                _feedbacks.value = emptyList()
            }
        }
    }

    /**
     * Call after submitting a feedback to update the report immediately.
     */
    fun onFeedbackSubmitted() {
        refreshReports()
    }

    /**
     * Factory for creating ReportsViewModel with dependencies.
     */
    class Factory(
        private val firebaseRepository: FirebaseRepository,
        private val sosRepository: SosRepository,
        private val auth: FirebaseAuth
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
                return ReportsViewModel(firebaseRepository, sosRepository, auth) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}