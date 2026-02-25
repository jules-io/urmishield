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
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var mockSettings: SettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockSettings = mockk(relaxed = true)
        every { mockSettings.stallingEnabled } returns flowOf(true)
        every { mockSettings.deepfakeEnabled } returns flowOf(false)
        viewModel = SettingsViewModel(mockSettings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `stallingEnabled reflects repository value`() = runTest {
        val job = launch { viewModel.stallingEnabled.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.stallingEnabled.value)
        job.cancel()
    }

    @Test
    fun `deepfakeEnabled reflects repository value`() = runTest {
        val job = launch { viewModel.deepfakeEnabled.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(false, viewModel.deepfakeEnabled.value)
        job.cancel()
    }

    @Test
    fun `setStalling calls repository`() = runTest {
        viewModel.setStalling(false)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { mockSettings.setStalling(false) }
    }

    @Test
    fun `setDeepfake calls repository`() = runTest {
        viewModel.setDeepfake(true)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { mockSettings.setDeepfake(true) }
    }
}
