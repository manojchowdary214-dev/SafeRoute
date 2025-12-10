package com.example.saferoute.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database with entities and version
@Database(
    entities = [RouteEntity::class, FeedbackEntity::class, SosRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // Route DAO
    abstract fun routeDao(): RouteDao
    // Feedback DAO
    abstract fun feedbackDao(): FeedbackDao
    // SOS DAO
    abstract fun sosDao(): SosDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null // Singleton instance

        // Get or create the database instance
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "saferoute_db" // Database name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}