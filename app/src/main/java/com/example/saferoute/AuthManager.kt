package com.example.saferoute

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*

object AuthManager {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Auth state helpers
    fun isUserSignedIn(): Boolean = auth.currentUser != null
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signOut() {
        auth.signOut()
    }

    // Email & Password Auth
    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Email and password cannot be empty")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.localizedMessage ?: "Login failed")
            }
    }

    fun registerWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Email and password cannot be empty")
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.localizedMessage ?: "Registration failed")
            }
    }

    // Google Sign-in
    fun getGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Force Google account picker every time
     */
    fun launchGoogleSignInWithPicker(
        client: GoogleSignInClient,
        onSignInIntentReady: (Intent) -> Unit
    ) {
        client.revokeAccess().addOnCompleteListener {
            onSignInIntentReady(client.signInIntent)
        }
    }

    fun handleGoogleSignInResult(data: Intent?, onResult: (Boolean, String?) -> Unit) {
        if (data == null) {
            onResult(false, "Google sign-in failed: no data returned")
            return
        }

        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account, onResult)
            } else {
                onResult(false, "Google sign-in failed: account is null")
            }
        } catch (e: ApiException) {
            onResult(false, e.localizedMessage ?: "Google sign-in failed")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.localizedMessage ?: "Firebase authentication failed")
            }
    }
}