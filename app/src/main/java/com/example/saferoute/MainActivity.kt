package com.example.saferoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
<<<<<<< HEAD
import com.google.firebase.FirebaseApp
=======
import com.example.saferoute.data.RouteDatabase
import com.google.firebase.FirebaseApp
import com.example.saferoute.repo.FirebaseRepository
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

<<<<<<< HEAD
        setContent {
            // Call AppNavigation function
            AppNavigation()
        }
    }
}
=======
        val routeDao = RouteDatabase.getDatabase(applicationContext).routeDao()
        val firebaseRepository = FirebaseRepository()

        setContent {
            AppNavigation(routeDao, firebaseRepository)
        }
    }
}
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
