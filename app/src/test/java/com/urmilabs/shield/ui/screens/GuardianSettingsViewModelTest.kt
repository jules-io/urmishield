package com.urmilabs.shield.ui.screens

import com.urmilabs.shield.db.SettingsRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GuardianSettingsViewModelTest {

    private lateinit var viewModel: GuardianSettingsViewModel
    private lateinit var mockSettings: SettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockSettings = mockk(relaxed = true)
        every { mockSettings.guardianNumber } returns flowOf("+1234567890")
        viewModel = GuardianSettingsViewModel(mockSettings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guardianNumber emits value from repository`() = runTest {
        val job = launch { viewModel.guardianNumber.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("+1234567890", viewModel.guardianNumber.value)
        job.cancel()
    }

    @Test
    fun `saveGuardianNumber calls repository`() = runTest {
        viewModel.saveGuardianNumber("+0987654321")
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { mockSettings.setGuardianNumber("+0987654321") }
    }

    @Test
    fun `initial value is null before repository emits`() {
        val freshSettings = mockk<SettingsRepository>(relaxed = true)
        every { freshSettings.guardianNumber } returns flowOf()
        val freshVm = GuardianSettingsViewModel(freshSettings)
        assertEquals(null, freshVm.guardianNumber.value)
    }
}
