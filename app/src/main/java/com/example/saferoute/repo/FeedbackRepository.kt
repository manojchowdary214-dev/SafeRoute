package com.example.saferoute.repo

import com.example.saferoute.data.FeedbackDao
import com.example.saferoute.data.FeedbackEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FeedbackRepository(private val feedbackDao: FeedbackDao) {

    // Insert feedback
    suspend fun addFeedback(feedback: FeedbackEntity) {
        feedbackDao.insertFeedback(feedback)
    }

    // Get all feedback for specific user
    suspend fun getAllFeedbacksForUser(userId: String): List<FeedbackEntity> {
        return feedbackDao.getAllFeedback()
            // filter by user
            .map { list -> list.filter { it.userId == userId } }
            .first()
    }
}