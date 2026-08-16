package com.example.fixitapplication.data.repository

import com.example.fixitapplication.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(name: String, email: String, pass: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID not found")
            
            val user = User(
                uid = uid,
                name = name,
                email = email,
                role = "student",
                createdAt = Timestamp.now()
            )
            
            firestore.collection("users").document(uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(uid: String): Result<String> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                val role = document.getString("role")
                if (role != null && (role == "admin" || role == "student")) {
                    Result.success(role)
                } else {
                    Result.failure(Exception("Invalid or missing user role"))
                }
            } else {
                // Automatically create document if it doesn't exist (e.g., first time Google Sign-In)
                val currentUser = auth.currentUser
                if (currentUser != null && currentUser.uid == uid) {
                    val newUser = User(
                        uid = uid,
                        name = currentUser.displayName ?: "User",
                        email = currentUser.email ?: "",
                        role = "student",
                        createdAt = Timestamp.now()
                    )
                    firestore.collection("users").document(uid).set(newUser).await()
                    Result.success("student")
                } else {
                    Result.failure(Exception("User document does not exist"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
