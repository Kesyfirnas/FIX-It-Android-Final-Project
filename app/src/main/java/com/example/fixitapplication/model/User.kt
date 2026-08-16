package com.example.fixitapplication.model

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "student",
    val createdAt: Timestamp? = null
)
