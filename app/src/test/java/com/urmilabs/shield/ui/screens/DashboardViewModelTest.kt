package com.urmilabs.shield.ui.screens

import com.urmilabs.shield.db.CallLogDao
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
class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var mockCallLogDao: CallLogDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockCallLogDao = mockk(relaxed = true)
        every { mockCallLogDao.getTotalScreenedCount() } returns flowOf(42)
        every { mockCallLogDao.getBlockedCount() } returns flowOf(7)
        viewModel = DashboardViewModel(mockCallLogDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `totalScreenedCount emits value from DAO`() = runTest {
        val job = launch { viewModel.totalScreenedCount.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(42, viewModel.totalScreenedCount.value)
        job.cancel()
    }

    @Test
    fun `blockedCount emits value from DAO`() = runTest {
        val job = launch { viewModel.blockedCount.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(7, viewModel.blockedCount.value)
        job.cancel()
    }

    @Test
    fun `initial values are zero before DAO emits`() {
        val freshDao = mockk<CallLogDao>(relaxed = true)
        every { freshDao.getTotalScreenedCount() } returns flowOf()
        every { freshDao.getBlockedCount() } returns flowOf()
        val freshVm = DashboardViewModel(freshDao)
        assertEquals(0, freshVm.totalScreenedCount.value)
        assertEquals(0, freshVm.blockedCount.value)
    }
}
