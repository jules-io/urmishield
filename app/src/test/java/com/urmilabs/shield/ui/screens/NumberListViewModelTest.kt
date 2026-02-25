package com.urmilabs.shield.ui.screens

import androidx.lifecycle.SavedStateHandle
import com.urmilabs.shield.db.NumberListDao
import com.urmilabs.shield.db.NumberListEntity
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
class NumberListViewModelTest {

    private lateinit var viewModel: NumberListViewModel
    private lateinit var mockDao: NumberListDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockDao = mockk(relaxed = true)
        val savedStateHandle = SavedStateHandle(mapOf("type" to "WHITELIST"))
        every { mockDao.getNumbersByType("WHITELIST") } returns flowOf(
            listOf(NumberListEntity("1234567890", "WHITELIST", "Doctor"))
        )
        viewModel = NumberListViewModel(mockDao, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `type is extracted from SavedStateHandle`() {
        assertEquals("WHITELIST", viewModel.type)
    }

    @Test
    fun `numbers emits list from DAO`() = runTest {
        val job = launch { viewModel.numbers.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.numbers.value.size)
        assertEquals("1234567890", viewModel.numbers.value[0].number)
        job.cancel()
    }

    @Test
    fun `addNumber inserts into DAO`() = runTest {
        viewModel.addNumber("0987654321")
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { mockDao.insert(NumberListEntity("0987654321", "WHITELIST", "Manual")) }
    }

    @Test
    fun `addNumber ignores blank input`() = runTest {
        viewModel.addNumber("   ")
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 0) { mockDao.insert(any()) }
    }

    @Test
    fun `removeNumber deletes from DAO`() = runTest {
        viewModel.removeNumber("1234567890")
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { mockDao.delete("1234567890") }
    }
}
