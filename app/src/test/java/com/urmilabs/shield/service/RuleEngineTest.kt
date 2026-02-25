package com.urmilabs.shield.service

import com.urmilabs.shield.config.ShieldRemoteConfig
import com.urmilabs.shield.db.CallLogDao
import com.urmilabs.shield.db.NumberListDao
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RuleEngineTest {

    private lateinit var ruleEngine: RuleEngine
    private lateinit var mockCallLogDao: CallLogDao
    private lateinit var mockNumberListDao: NumberListDao
    private lateinit var mockRemoteConfig: ShieldRemoteConfig

    @Before
    fun setup() {
        mockCallLogDao = mockk(relaxed = true)
        mockNumberListDao = mockk(relaxed = true)
        mockRemoteConfig = mockk(relaxed = true)
        every { mockRemoteConfig.emergencyBypassTimeoutMs } returns 60000L
        ruleEngine = RuleEngine(mockNumberListDao, mockCallLogDao, mockRemoteConfig)
    }

    @Test
    fun `returns EMERGENCY_BYPASS when called within timeout`() = runTest {
        val recentTimestamp = System.currentTimeMillis() - 30_000 // 30 seconds ago
        coEvery { mockCallLogDao.getLastCallTime("1234567890") } returns recentTimestamp

        val result = ruleEngine.checkStatus("1234567890")

        assertEquals(RuleEngine.RuleResult.EMERGENCY_BYPASS, result)
    }

    @Test
    fun `returns WHITELISTED when number is in whitelist`() = runTest {
        coEvery { mockCallLogDao.getLastCallTime("1234567890") } returns null
        coEvery { mockNumberListDao.getNumberType("1234567890") } returns "WHITELIST"

        val result = ruleEngine.checkStatus("1234567890")

        assertEquals(RuleEngine.RuleResult.WHITELISTED, result)
    }

    @Test
    fun `returns BLACKLISTED when number is in blacklist`() = runTest {
        coEvery { mockCallLogDao.getLastCallTime("1234567890") } returns null
        coEvery { mockNumberListDao.getNumberType("1234567890") } returns "BLACKLIST"

        val result = ruleEngine.checkStatus("1234567890")

        assertEquals(RuleEngine.RuleResult.BLACKLISTED, result)
    }

    @Test
    fun `returns UNKNOWN when number is not in any list`() = runTest {
        coEvery { mockCallLogDao.getLastCallTime("1234567890") } returns null
        coEvery { mockNumberListDao.getNumberType("1234567890") } returns null

        val result = ruleEngine.checkStatus("1234567890")

        assertEquals(RuleEngine.RuleResult.UNKNOWN, result)
    }

    @Test
    fun `does not trigger EMERGENCY_BYPASS when last call exceeds timeout`() = runTest {
        val oldTimestamp = System.currentTimeMillis() - 120_000
        coEvery { mockCallLogDao.getLastCallTime("1234567890") } returns oldTimestamp
        coEvery { mockNumberListDao.getNumberType("1234567890") } returns null

        val result = ruleEngine.checkStatus("1234567890")

        assertEquals(RuleEngine.RuleResult.UNKNOWN, result)
    }

    @Test
    fun `EMERGENCY_BYPASS takes priority over BLACKLIST`() = runTest {
        val recentTimestamp = System.currentTimeMillis() - 10_000
        coEvery { mockCallLogDao.getLastCallTime("1234567890") } returns recentTimestamp
        coEvery { mockNumberListDao.getNumberType("1234567890") } returns "BLACKLIST"

        val result = ruleEngine.checkStatus("1234567890")

        assertEquals(RuleEngine.RuleResult.EMERGENCY_BYPASS, result)
    }

    @Test
    fun `respects custom timeout from remote config`() = runTest {
        every { mockRemoteConfig.emergencyBypassTimeoutMs } returns 30_000L
        val ruleEngineCustom = RuleEngine(mockNumberListDao, mockCallLogDao, mockRemoteConfig)

        val timestamp45sAgo = System.currentTimeMillis() - 45_000
        coEvery { mockCallLogDao.getLastCallTime("1234567890") } returns timestamp45sAgo
        coEvery { mockNumberListDao.getNumberType("1234567890") } returns null

        val result = ruleEngineCustom.checkStatus("1234567890")
        assertEquals(RuleEngine.RuleResult.UNKNOWN, result) // 45s > 30s timeout
    }
}
