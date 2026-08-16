package com.example.fixitapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixitapplication.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val role: String? = null,
    val isNotLoggedIn: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val auth = FirebaseAuth.getInstance()
    private val repository = AuthRepository()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = LoginUiState(error = "Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid
                if (uid != null) {
                    checkUserRole(uid)
                } else {
                    _uiState.value = LoginUiState(error = "Login failed: UID not found")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val uid = result.user?.uid
                if (uid != null) {
                    checkUserRole(uid)
                } else {
                    _uiState.value = LoginUiState(error = "Google Login failed: UID not found")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = e.localizedMessage ?: "Google Sign-In failed")
            }
        }
    }

    fun checkAutoLogin() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                _uiState.value = LoginUiState(isLoading = true)
                checkUserRole(currentUser.uid)
            }
        } else {
            _uiState.value = LoginUiState(isNotLoggedIn = true)
        }
    }

    private suspend fun checkUserRole(uid: String) {
        val roleResult = repository.getUserRole(uid)
        roleResult.onSuccess { role ->
            _uiState.value = LoginUiState(isSuccess = true, role = role)
        }.onFailure { e ->
            auth.signOut()
            _uiState.value = LoginUiState(error = e.localizedMessage ?: "Failed to verify user role")
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
