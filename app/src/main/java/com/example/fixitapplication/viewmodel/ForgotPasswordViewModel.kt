package com.example.fixitapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState(isLoading = true)
            // Firebase Auth reset password logic will go here
            // Mocking success for now
            kotlinx.coroutines.delay(1000)
            _uiState.value = ForgotPasswordUiState(isSuccess = true)
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}
