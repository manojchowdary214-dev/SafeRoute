package com.example.saferoute.repo

import com.example.saferoute.data.Route
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val routesCollection = firestore.collection("routes")

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
}
