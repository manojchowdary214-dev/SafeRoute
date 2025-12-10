package com.example.saferoute.repo

import android.content.Context
import android.location.Geocoder
import com.example.saferoute.data.SosDao
import com.example.saferoute.data.SosRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class SosRepository(
    private val context: Context,
    private val sosDao: SosDao
) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Send SOS with optional coordinates.
     * Converts coordinates to human-readable place name before saving and sending.
     */
    suspend fun triggerSos(message: String?, lat: Double? = null, lon: Double? = null) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

        // Reverse geocode coordinates to get place name
        val locationName: String? = if (lat != null && lon != null) {
            try {
                withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    addresses?.get(0)?.getAddressLine(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else null

        val record = SosRecord(
            userId = userId,
            message = message ?: "",
            latitude = lat,
            longitude = lon,
            locationName = locationName,
            synced = false
        )

        // Save locally first
        sosDao.insertSos(record)

        try {
            // Send to Firestore
            firestore.collection("users")
                .document(userId)
                .collection("sos_history")
                .add(
                    mapOf(
                        "message" to (message ?: ""),
                        "lat" to lat,
                        "lon" to lon,
                        "location_name" to locationName,
                        "timestamp" to System.currentTimeMillis()
                    )
                ).await()

            // Mark synced locally
            sosDao.markSosAsSynced(record.id)

        } catch (e: Exception) {
            e.printStackTrace()
            // Leave record as unsynced
        }
    }

    fun getLocalSosFlow() = sosDao.getAllSos()

    /**
     * Fetch SOS history from Firestore
     */
    suspend fun getSosHistory(): List<SosRecord> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("sos_history")
                .orderBy("timestamp")
                .get()
                .await()

            snapshot.documents.map { doc ->
                SosRecord(
                    id = 0,
                    firestoreId = doc.id,
                    userId = userId,
                    message = doc.getString("message") ?: "",
                    latitude = doc.getDouble("lat"),
                    longitude = doc.getDouble("lon"),
                    locationName = doc.getString("location_name"),
                    audioPath = null,
                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                    synced = true
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}