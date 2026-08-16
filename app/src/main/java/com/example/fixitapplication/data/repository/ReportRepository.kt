package com.example.fixitapplication.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.fixitapplication.model.Report
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ReportRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getStudentReports(): Flow<List<Report>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        val listener = firestore.collection("reports")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val reports = snapshot?.toObjects(Report::class.java) ?: emptyList()
                // Urutkan secara aman: nullsLast agar aduan yang baru (timestamp masih null) tetap muncul di bawah atau atas secara konsisten tanpa crash
                val sortedReports = reports.sortedWith { r1, r2 ->
                    val t1 = r1.createdAt
                    val t2 = r2.createdAt
                    if (t1 == null && t2 == null) 0
                    else if (t1 == null) -1 // Aduan baru tanpa timestamp taruh di paling atas
                    else if (t2 == null) 1
                    else t2.compareTo(t1) // Descending
                }
                trySend(sortedReports)
            }
        awaitClose { listener.remove() }
    }

    fun getAllReports(): Flow<List<Report>> = callbackFlow {
        val listener = firestore.collection("reports")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val reports = snapshot?.toObjects(Report::class.java) ?: emptyList()
                
                // Urutkan berdasarkan status priority, lalu berdasarkan waktu
                val sortedReports = reports.sortedWith { r1, r2 ->
                    val p1 = when(r1.status) {
                        "Pending" -> 0
                        "In Progress" -> 1
                        "Resolved" -> 3
                        "Rejected" -> 4
                        else -> 2
                    }
                    val p2 = when(r2.status) {
                        "Pending" -> 0
                        "In Progress" -> 1
                        "Resolved" -> 3
                        "Rejected" -> 4
                        else -> 2
                    }
                    
                    if (p1 != p2) {
                        p1.compareTo(p2)
                    } else {
                        val t1 = r1.createdAt
                        val t2 = r2.createdAt
                        if (t1 == null && t2 == null) 0
                        else if (t1 == null) -1
                        else if (t2 == null) 1
                        else t2.compareTo(t1)
                    }
                }
                
                trySend(sortedReports)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateReportStatus(reportId: String, newStatus: String): Result<Unit> {
        return try {
            firestore.collection("reports").document(reportId)
                .update("status", newStatus)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitReport(title: String, description: String, imageUri: Uri?, location: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val userDoc = firestore.collection("users").document(userId).get().await()
            val userName = userDoc.getString("name") ?: "Student"
            
            var base64Image = ""
            if (imageUri != null) {
                base64Image = uriToBase64(imageUri)
            }

            val reportId = firestore.collection("reports").document().id
            val report = Report(
                id = reportId,
                userId = userId,
                userName = userName,
                title = title,
                description = description,
                imageUrl = base64Image,
                location = location,
                status = "Pending",
                createdAt = Timestamp.now()
            )

            firestore.collection("reports").document(reportId).set(report).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uriToBase64(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Resize: Max 800px agar aman di Firestore
            val scale = Math.min(800f / originalBitmap.width, 800f / originalBitmap.height).coerceAtMost(1f)
            val bitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (originalBitmap.width * scale).toInt(),
                    (originalBitmap.height * scale).toInt(),
                    true
                )
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            
            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64String"
        } catch (e: Exception) {
            ""
        }
    }
}
