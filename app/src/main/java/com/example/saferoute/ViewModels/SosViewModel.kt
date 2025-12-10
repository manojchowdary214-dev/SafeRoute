package com.example.saferoute.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saferoute.repo.SosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SosUiState(
    val message: String = "",     // ui message
    val sending: Boolean = false, // sending status
    val error: Boolean = false    // error state
)

class SosViewModel(
    app: Application,
    private val repository: SosRepository    // repo dependency
) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(SosUiState())  // internal state
    val ui: StateFlow<SosUiState> = _ui             // public state

    /**
     * Send SOS message with optional coordinates and optional location name
     */
    fun sendTextSos(message: String, lat: Double? = null, lon: Double? = null) {
        if (message.isBlank()) {
            _ui.value = SosUiState(message = "Message cannot be empty", error = true)  // empty check
            return
        }

        viewModelScope.launch {
            // sending status
            _ui.value = SosUiState(message = "Sending SOS...", sending = true, error = false)
            try {
                repository.triggerSos(message, lat, lon)
                _ui.value = SosUiState(message = "SOS sent successfully!", sending = false, error = false)  // success
            } catch (e: Exception) {
                e.printStackTrace()
                _ui.value = SosUiState(
                    message = "Failed to send SOS: ${e.message}",  // error message
                    sending = false,
                    error = true
                )
            }
        }
    }

    /**
     * Factory for creating ViewModel with repository dependency
     */
    class Factory(
        private val app: Application,
        private val repository: SosRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SosViewModel::class.java)) {
                // create VM
                return SosViewModel(app, repository) as T
            }
            // error
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}