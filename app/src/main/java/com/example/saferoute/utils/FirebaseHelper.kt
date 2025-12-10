package com.example.saferoute.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseHelper {

    // firebase auth
    private val auth = FirebaseAuth.getInstance()
    // firestore instance
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun updateFcmToken(token: String) {
        val uid = auth.currentUser?.uid ?: return   // get uid
        firestore.collection("users").document(uid)
            .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
            .await()                                 // update token
    }

    suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid                 // return uid
    }
}