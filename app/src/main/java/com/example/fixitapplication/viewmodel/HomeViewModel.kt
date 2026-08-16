package com.example.fixitapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixitapplication.data.repository.ReportRepository
import com.example.fixitapplication.model.Report
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val reports: List<Report> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReportRepository = ReportRepository(application)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var reportsJob: kotlinx.coroutines.Job? = null

    init {
        fetchUserData()
        fetchReports()
    }

    fun fetchUserData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "Mahasiswa"
                _uiState.value = _uiState.value.copy(userName = name)
            }
    }

    fun fetchReports(isManualRefresh: Boolean = false) {
        reportsJob?.cancel()
        reportsJob = viewModelScope.launch {
            // Aktifkan isRefreshing jika dipicu manual atau jika sudah ada data
            if (isManualRefresh || _uiState.value.reports.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
            }
            
            repository.getStudentReports().collect { reports ->
                _uiState.value = _uiState.value.copy(
                    reports = reports, 
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    fun onRefresh() {
        fetchUserData()
        fetchReports(isManualRefresh = true)
    }
}
