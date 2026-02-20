package com.urmilabs.shield.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CallLogEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callLogDao(): CallLogDao
}
