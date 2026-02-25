package com.urmilabs.shield.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NumberListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(number: NumberListEntity)

    @Query("SELECT * FROM number_list WHERE type = :type")
    fun getNumbersByType(type: String): Flow<List<NumberListEntity>>

    @Query("SELECT type FROM number_list WHERE number = :number LIMIT 1")
    suspend fun getNumberType(number: String): String?
    
    @Query("DELETE FROM number_list WHERE number = :number")
    suspend fun delete(number: String)
}
