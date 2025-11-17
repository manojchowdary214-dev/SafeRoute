package com.example.saferoute.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RouteEntity::class], version = 1)
abstract class RouteDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao

    companion object {
        @Volatile private var INSTANCE: RouteDatabase? = null
        fun getDatabase(context: Context): RouteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, RouteDatabase::class.java, "route_database").build()
                INSTANCE = instance
                instance
            }
        }
    }
}