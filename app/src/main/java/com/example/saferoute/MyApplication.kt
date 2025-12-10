package com.example.saferoute

import android.app.Application
import com.google.firebase.FirebaseApp

class SafeRouteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}