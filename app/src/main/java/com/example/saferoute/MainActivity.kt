package com.example.saferoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.firebase.FirebaseApp
import com.example.saferoute.data.RouteDatabase
import com.example.saferoute.repo.FirebaseRepository

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Room DAO
        val routeDao = RouteDatabase.getDatabase(applicationContext).routeDao()

        // Initialize Firebase Repository
        val firebaseRepository = FirebaseRepository()

        // Set Compose content
        setContent {
            AppNavigation(routeDao, firebaseRepository)
        }
    }
}