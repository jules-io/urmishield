package com.urmilabs.shield.service

import com.urmilabs.shield.config.ShieldRemoteConfig
import com.urmilabs.shield.db.CallLogDao
import com.urmilabs.shield.db.NumberListDao
import javax.inject.Inject

class RuleEngine @Inject constructor(
    private val numberListDao: NumberListDao,
    private val callLogDao: CallLogDao,
    private val remoteConfig: ShieldRemoteConfig
) {
    suspend fun checkStatus(number: String): RuleResult {
        // 1. Emergency Bypass: If called within the configurable timeout (redial)
        val lastCallTime = callLogDao.getLastCallTime(number)
        val bypassTimeout = remoteConfig.emergencyBypassTimeoutMs
        if (lastCallTime != null && System.currentTimeMillis() - lastCallTime < bypassTimeout) {
            return RuleResult.EMERGENCY_BYPASS
        }

        // 2. Whitelist / Blacklist
        val type = numberListDao.getNumberType(number)
        return when (type) {
            "WHITELIST" -> RuleResult.WHITELISTED
            "BLACKLIST" -> RuleResult.BLACKLISTED
            else -> RuleResult.UNKNOWN
        }
    }

    enum class RuleResult {
        EMERGENCY_BYPASS,
        WHITELISTED,
        BLACKLISTED,
        UNKNOWN
    }
}
