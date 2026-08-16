package com.example.fixitapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixitapplication.data.repository.ReportRepository
import com.example.fixitapplication.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val reports: List<Report> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ReportRepository(application)
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        fetchReports()
    }

    fun fetchReports() {
        viewModelScope.launch {
            if (_uiState.value.reports.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
            }
            
            repository.getAllReports().collect { reports ->
                _uiState.value = _uiState.value.copy(
                    reports = reports,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    fun updateStatus(reportId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, newStatus)
        }
    }
}
