package com.example.saferoute.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val routeId: String?,
    val rating: Int,
    val notes: String?,
    val createdAt: Long,
    val synced: Boolean = false
)