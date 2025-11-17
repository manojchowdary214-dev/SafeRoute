package com.example.saferoute

import android.app.Application
import androidx.room.Room
import com.example.saferoute.data.RouteDao
import com.example.saferoute.data.RouteDatabase


class MyApplication : Application() {
    lateinit var db: RouteDatabase
    lateinit var routeDao: RouteDao

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, RouteDatabase::class.java, "routes.db")
            .fallbackToDestructiveMigration()
            .build()
        routeDao = db.routeDao()
    }
}
