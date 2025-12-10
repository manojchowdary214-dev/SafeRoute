package com.example.saferoute.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.saferoute.data.SosRecord

@Dao
interface SosDao {

    // Insert SOS}
    @Insert
    suspend fun insertSos(sos: SosRecord)

    // All SOS
    @Query("SELECT * FROM sos_records ORDER BY timestamp DESC")
    fun getAllSos(): Flow<List<SosRecord>>

    // Update SOS
    @Update
    suspend fun updateSos(sos: SosRecord)

    // Mark SOS record as synced by ID
    @Query("UPDATE sos_records SET synced = 1 WHERE id = :id")
    suspend fun markSosAsSynced(id: Long)
}