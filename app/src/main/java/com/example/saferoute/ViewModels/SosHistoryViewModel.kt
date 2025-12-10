package com.example.saferoute.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saferoute.data.SosRecord
import com.example.saferoute.repo.SosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SosHistoryViewModel(
    private val sosRepository: SosRepository   // repo dependency
) : ViewModel() {

    private val _sosHistory = MutableStateFlow<List<SosRecord>>(emptyList())  // internal state
    val sosHistory: StateFlow<List<SosRecord>> get() = _sosHistory         // public state

    init { loadHistory() }    // init fetch

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                // fetch from repo
                val history = sosRepository.getSosHistory()
                // latest first
                _sosHistory.value = history.reversed()
            } catch (e: Exception) {
                _sosHistory.value = emptyList()
            }
        }
    }

    class Factory(
        private val repository: SosRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SosHistoryViewModel::class.java)) {
                // create VM
                return SosHistoryViewModel(repository) as T
            }
            // error
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}