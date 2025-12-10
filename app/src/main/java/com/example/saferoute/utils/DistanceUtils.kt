package com.example.saferoute.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

fun calculateDistanceKm(start: LatLng, end: LatLng): Double {
    // Earth radius
    val R = 6371

    val lat1 = Math.toRadians(start.latitude)   // start lat
    val lng1 = Math.toRadians(start.longitude)  // start lng
    val lat2 = Math.toRadians(end.latitude)     // end lat
    val lng2 = Math.toRadians(end.longitude)    // end lng

    // delta lat
    val dLat = lat2 - lat1
    // delta lng
    val dLng = lng2 - lng1

    val a = sin(dLat / 2).pow(2) +
            cos(lat1) * cos(lat2) *
            sin(dLng / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    // distance km
    return R * c
}