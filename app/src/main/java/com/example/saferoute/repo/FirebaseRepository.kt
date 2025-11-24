package com.example.saferoute.repo

import android.location.Location
import com.example.saferoute.data.Route
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

class FirebaseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val routesCollection = firestore.collection("routes")

    private val auth = FirebaseAuth.getInstance()
    private val realtimeDatabase = FirebaseDatabase.getInstance()

    /** Firestore route operations **/
    fun saveRoute(route: Route) {
        routesCollection.document(route.id).set(route)
    }

    fun fetchRoutes(): Flow<List<Route>> = callbackFlow {
        val listener = routesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val routes = snapshot?.toObjects(Route::class.java) ?: emptyList()
            trySend(routes)
        }
        awaitClose { listener.remove() }
    }

    /** Realtime Database: Update live location **/
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
}
