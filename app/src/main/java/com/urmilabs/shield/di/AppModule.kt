package com.urmilabs.shield.di

import android.content.Context
import com.urmilabs.shield.ai.ScamDetector
import com.urmilabs.shield.config.ShieldRemoteConfig
import com.urmilabs.shield.db.AppDatabase
import com.urmilabs.shield.db.CallLogDao
import com.urmilabs.shield.db.NumberListDao
import com.urmilabs.shield.db.SettingsRepository
import com.urmilabs.shield.service.GuardianAlerter
import com.urmilabs.shield.service.OverlayManager
import com.urmilabs.shield.service.RuleEngine
import com.urmilabs.shield.service.ScamBaiter
import com.urmilabs.shield.analytics.ShieldAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCallLogDao(db: AppDatabase): CallLogDao {
        return db.callLogDao()
    }

    @Provides
    @Singleton
    fun provideNumberListDao(db: AppDatabase): NumberListDao {
        return db.numberListDao()
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideScamDetector(@ApplicationContext context: Context): ScamDetector {
        return ScamDetector(context)
    }

    @Provides
    @Singleton
    fun provideOverlayManager(@ApplicationContext context: Context): OverlayManager {
        return OverlayManager(context)
    }

    @Provides
    @Singleton
    fun provideGuardianAlerter(
        @ApplicationContext context: Context,
        analytics: ShieldAnalytics
    ): GuardianAlerter {
        return GuardianAlerter(context, analytics)
    }

    @Provides
    @Singleton
    fun provideRuleEngine(
        numberListDao: NumberListDao,
        callLogDao: CallLogDao,
        remoteConfig: ShieldRemoteConfig
    ): RuleEngine {
        return RuleEngine(numberListDao, callLogDao, remoteConfig)
    }

    @Provides
    @Singleton
    fun provideScamBaiter(@ApplicationContext context: Context): ScamBaiter {
        return ScamBaiter(context)
    }
}
