package com.example.fixitapplication.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixitapplication.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, pass: String, confirmPass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _uiState.value = RegisterUiState(error = "All fields are required")
            return
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = RegisterUiState(error = "Invalid email format")
            return
        }

        if (pass.length < 8) {
            _uiState.value = RegisterUiState(error = "Password must be at least 8 characters")
            return
        }

        if (pass != confirmPass) {
            _uiState.value = RegisterUiState(error = "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            val result = repository.registerUser(name, email, pass)
            if (result.isSuccess) {
                _uiState.value = RegisterUiState(isSuccess = true)
            } else {
                _uiState.value = RegisterUiState(error = result.exceptionOrNull()?.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
    }
}
