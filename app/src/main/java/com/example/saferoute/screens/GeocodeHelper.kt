package com.example.saferoute.screens

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng

fun geocodeAddress(context: Context, location: String): LatLng? {
    return try {
        val geocoder = Geocoder(context)
        val result = geocoder.getFromLocationName(location, 1)
        val address = result?.firstOrNull()

        if (address != null) {
            LatLng(address.latitude, address.longitude)
        } else null

    } catch (e: Exception) {
        null
    }
}