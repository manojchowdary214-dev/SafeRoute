package com.example.saferoute.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {

    // Insert new feedback
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity)

    // Get feedback for specific route
    @Query("SELECT * FROM feedback WHERE routeId = :routeId ORDER BY createdAt DESC")
    fun getFeedbackForRoute(routeId: String): Flow<List<FeedbackEntity>>

    // Get all feedback
    @Query("SELECT * FROM feedback ORDER BY createdAt DESC")
    fun getAllFeedback(): Flow<List<FeedbackEntity>>
}