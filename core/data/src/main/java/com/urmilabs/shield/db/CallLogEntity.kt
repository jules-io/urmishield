package com.urmilabs.shield.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val callerNumber: String,
    val riskLevel: String, // HIGH, MEDIUM, LOW
    val detectionReason: String, // e.g. "Keyword: IRS"
    val timestamp: Long,
    val wasBlocked: Boolean
)
