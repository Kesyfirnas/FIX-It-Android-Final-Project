package com.example.fixitapplication.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixitapplication.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class ReportViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReportRepository = ReportRepository(application)

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun submitReport(title: String, description: String, imageUri: Uri?, location: String) {
        if (title.isBlank() || description.isBlank() || location.isBlank()) {
            _uiState.value = ReportUiState(error = "Semua field harus diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.submitReport(title, description, imageUri, location)
            if (result.isSuccess) {
                _uiState.value = ReportUiState(isSuccess = true)
            } else {
                _uiState.value = ReportUiState(error = result.exceptionOrNull()?.localizedMessage ?: "Gagal mengirim aduan")
            }
        }
    }

    fun resetState() {
        _uiState.value = ReportUiState()
    }
}
