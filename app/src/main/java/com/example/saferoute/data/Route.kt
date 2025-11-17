package com.example.saferoute.data

data class Route(
    val id: String = "",
    val start: String = "",
    val end: String = "",
    val distance: Double = 0.0,
    val duration: Int = 0,
    val safetyScore: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)