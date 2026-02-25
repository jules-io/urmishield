package com.urmilabs.shield.ui.screens

import com.urmilabs.shield.db.CallLogDao
import com.urmilabs.shield.db.CallLogEntity
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
class HistoryViewModelTest {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var mockCallLogDao: CallLogDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockCallLogDao = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `logs emits list from DAO`() = runTest {
        val testLogs = listOf(
            CallLogEntity(1, "1234567890", "HIGH", "IRS", System.currentTimeMillis(), false),
            CallLogEntity(2, "0987654321", "LOW", "Clean", System.currentTimeMillis(), false)
        )
        every { mockCallLogDao.getAllLogs() } returns flowOf(testLogs)
        viewModel = HistoryViewModel(mockCallLogDao)
        val job = launch { viewModel.logs.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.logs.value.size)
        assertEquals("1234567890", viewModel.logs.value[0].callerNumber)
        job.cancel()
    }

    @Test
    fun `initial value is empty list`() = runTest {
        every { mockCallLogDao.getAllLogs() } returns flowOf(emptyList())
        viewModel = HistoryViewModel(mockCallLogDao)
        val job = launch { viewModel.logs.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(emptyList<CallLogEntity>(), viewModel.logs.value)
        job.cancel()
    }
}
