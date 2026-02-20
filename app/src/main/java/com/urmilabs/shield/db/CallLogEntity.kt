package com.urmilabs.shield.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: String,
    val timestamp: Long,
    val riskLevel: String
)
