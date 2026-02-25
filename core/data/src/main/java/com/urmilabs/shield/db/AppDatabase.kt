package com.urmilabs.shield.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [CallLogEntity::class, NumberListEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callLogDao(): CallLogDao
    abstract fun numberListDao(): NumberListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = PassphraseManager.getPassphrase(context)
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "urmi_shield_database"
                )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration() // For development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
