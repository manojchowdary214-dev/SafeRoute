package com.example.saferoute.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sos_records")
data class SosRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firestoreId: String? = null,
    val userId: String,
    val message: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val audioPath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false,
    val locationName: String? = null,
)