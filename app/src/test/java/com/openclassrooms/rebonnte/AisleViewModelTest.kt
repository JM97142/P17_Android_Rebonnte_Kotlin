package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.models.Aisle
import com.openclassrooms.rebonnte.repository.AisleRepository
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class AisleViewModelTest {

    private lateinit var repository: AisleRepository
    private lateinit var viewModel: AisleViewModel

    private val fakeAislesFlow = MutableStateFlow<List<Aisle>>(emptyList())

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        repository = mock {
            on { aisles } doReturn fakeAislesFlow
        }
        viewModel = AisleViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addRandomAisle should call repository_addAisle`() {
        fakeAislesFlow.value = listOf(Aisle("Aisle 1"), Aisle("Aisle 2"))
        viewModel.addRandomAisle()

        verify(repository).addAisle(check {
            assertEquals("Aisle 3", it.name)
        })
    }

    @Test
    fun `reloadAisles should call repository_loadAisles`() {
        viewModel.reloadAisles()
        verify(repository).loadAisles()
    }

    @Test
    fun `deleteAisle should call repository_deleteAisle`() {
        val aisle = Aisle("Aisle 7")
        viewModel.deleteAisle(aisle)

        verify(repository).deleteAisle(aisle)
    }

    @Test
    fun `onCleared should clear listener`() = runTest {
        viewModel.onCleared()
        advanceUntilIdle()
        verify(repository).clearListener()
    }
}