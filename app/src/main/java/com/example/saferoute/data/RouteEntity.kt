package com.example.saferoute.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: String,
    val start: String,
    val end: String,
    val distance: Double,
    val duration: Int,
    val safetyScore: Int,
    val timestamp: Long
)
