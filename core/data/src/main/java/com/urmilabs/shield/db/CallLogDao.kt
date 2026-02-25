package com.urmilabs.shield.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao {
    @Insert
    suspend fun insert(callLog: CallLogEntity)

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<CallLogEntity>>

    @Query("SELECT COUNT(*) FROM call_logs WHERE wasBlocked = 1")
    fun getBlockedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM call_logs")
    fun getTotalScreenedCount(): Flow<Int>
    
    @Query("SELECT timestamp FROM call_logs WHERE callerNumber = :number ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastCallTime(number: String): Long?
}
