package com.example.saferoute

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthManager {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    /** Check if user is signed in **/
    fun isUserSignedIn(): Boolean = auth.currentUser != null
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    fun signOut() { auth.signOut() }

    // Email login
    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Email and password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful, task.exception?.localizedMessage)
            }
    }

    // Email register
    fun registerWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Email and password cannot be empty")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful, task.exception?.localizedMessage)
            }
    }
}