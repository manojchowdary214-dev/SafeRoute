package com.example.saferoute.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    // Insert route
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity)

    // Update route
    @Update
    suspend fun updateRoute(route: RouteEntity)

    // Delete route
    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRouteById(routeId: String)

    // Get route
    @Query("SELECT * FROM routes WHERE id = :routeId LIMIT 1")
    suspend fun getRouteById(routeId: String): RouteEntity?

    // Recent routes
    @Query("SELECT * FROM routes ORDER BY timestamp DESC LIMIT 20")
    fun getRecentRoutes(): Flow<List<RouteEntity>> 

    //Get all routes
    @Query("SELECT * FROM routes")
    suspend fun getAllRoutes(): List<RouteEntity> 
}