package com.urmilabs.shield.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CallLogDao {
    @Insert
    suspend fun insert(log: CallLogEntity)

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    suspend fun getAll(): List<CallLogEntity>
}
