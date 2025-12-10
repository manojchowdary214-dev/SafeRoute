package com.example.saferoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.saferoute.data.AppDatabase
import com.example.saferoute.repo.FirebaseRepository
import com.example.saferoute.ui.theme.SafeRouteTheme
import com.example.saferoute.AppNavigation

class MainActivity : ComponentActivity() {

    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        firebaseRepository = FirebaseRepository()

        setContent {
            SafeRouteTheme {
                val db = AppDatabase.getDatabase(applicationContext)
                val routeDao = db.routeDao()
                val feedbackDao = db.feedbackDao()
                val sosDao = db.sosDao()

                // App Navigation
                AppNavigation(
                    routeDao = routeDao,
                    feedbackDao = feedbackDao,
                    firebaseRepository = firebaseRepository,
                    sosDao = sosDao
                )
            }
        }
    }
}