package com.example.fixitapplication.model

import com.google.firebase.Timestamp

data class Report(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val status: String = "Pending", // Pending, In Progress, Resolved
    val createdAt: Timestamp? = null
)
