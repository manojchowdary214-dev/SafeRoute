package com.example.saferoute.repo

import android.location.Location
import com.example.saferoute.data.FeedbackEntity
import com.example.saferoute.data.RouteEntity
import com.example.saferoute.data.SosRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val routesCollection = firestore.collection("routes")
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val realtimeDatabase = FirebaseDatabase.getInstance()

    // Routes
    fun saveRoute(route: RouteEntity) {
        routesCollection.document(route.id).set(route)
    }

    // Live Location
    fun updateUserLocation(routeId: String, location: Location) {
        val uid = auth.currentUser?.uid ?: return
        val locationRef = realtimeDatabase.reference
            .child("users")
            .child(uid)
            .child("journeys")
            .child(routeId)
            .child("location")

        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )
        locationRef.setValue(locationData)
    }

    // SOS Events
    suspend fun saveSosEvent(
        message: String? = null,
        lat: Double? = null,
        lon: Double? = null,
        audioUrl: String? = null
    ) {
        val user = auth.currentUser ?: return
        val data = hashMapOf(
            "userId" to user.uid,
            "message" to (message ?: ""),
            "lat" to lat,
            "lon" to lon,
            "audioUrl" to audioUrl,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("sos_events").add(data).await()
    }

    suspend fun fetchSosHistory(): List<SosRecord> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val snapshot = firestore.collection("sos_events")
            .whereEqualTo("userId", uid)
            .orderBy("timestamp")
            .get()
            .await()

        // Room auto-generated
        return snapshot.documents.map { doc ->
            SosRecord(
                id = 0,
                firestoreId = doc.id,
                userId = doc.getString("userId") ?: "",
                message = doc.getString("message") ?: "",
                latitude = doc.getDouble("lat"),
                longitude = doc.getDouble("lon"),
                audioPath = doc.getString("audioUrl"),
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                synced = true
            )
        }
    }

    // FeedBack
    suspend fun sendFeedbackToFirestore(feedback: FeedbackEntity) {
        val data = hashMapOf(
            "userId" to feedback.userId,
            "routeId" to feedback.routeId,
            "rating" to feedback.rating,
            "notes" to feedback.notes,
            "createdAt" to FieldValue.serverTimestamp()
        )
        firestore.collection("routeFeedback").add(data).await()
    }

    // Reports
    suspend fun fetchSosCountForUser(uid: String): Long {
        val snap = firestore.collection("sos_events")
            .whereEqualTo("userId", uid)
            .get()
            .await()
        return snap.size().toLong()
    }

    suspend fun fetchAverageSafetyRating(uid: String): Double {
        val snap = firestore.collection("routeFeedback")
            .whereEqualTo("userId", uid)
            .get()
            .await()
        val ratings = snap.documents.mapNotNull { it.getLong("rating")?.toInt() }
        return ratings.average().takeIf { it.isFinite() } ?: 0.0
    }

    // Get All Feedbacks
    suspend fun fetchAllFeedbacks(uid: String): List<FeedbackEntity> {
        return try {
            val snapshot = firestore.collection("routeFeedback")
                .whereEqualTo("userId", uid)
                .orderBy("createdAt")
                .get()
                .await()

            snapshot.documents.map { doc ->
                FeedbackEntity(
                    id = 0,
                    userId = doc.getString("userId") ?: "",
                    routeId = doc.getString("routeId"),
                    rating = doc.getLong("rating")?.toInt() ?: 0,
                    notes = doc.getString("notes"),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                    synced = true
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Get AllFeedbacksForUser
    suspend fun getAllFeedbacksForUser(uid: String): List<FeedbackEntity> {
        val snapshot = firestore.collection("routeFeedback")
            .whereEqualTo("userId", uid)
            .orderBy("createdAt")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            FeedbackEntity(
                id = 0,
                userId = doc.getString("userId") ?: "",
                routeId = doc.getString("routeId"),
                rating = doc.getLong("rating")?.toInt() ?: 0,
                notes = doc.getString("notes"),
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis(),
                synced = true
            )
        }
    }
}