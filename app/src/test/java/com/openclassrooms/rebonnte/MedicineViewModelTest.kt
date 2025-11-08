package com.openclassrooms.rebonnte

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.rebonnte.models.Medicine
import com.openclassrooms.rebonnte.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class MedicineViewModelTest {

    private lateinit var repository: MedicineRepository
    private lateinit var viewModel: MedicineViewModel

    private val fakeMedicinesFlow = MutableStateFlow<List<Medicine>>(emptyList())

    // mocks Firebase uniquement pour le test qui en a besoin
    private lateinit var firebaseAuthMock: MockedStatic<FirebaseAuth>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock {
            on { medicines } doReturn fakeMedicinesFlow
        }
        viewModel = MedicineViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // on ferme le mock FirebaseAuth s’il a été initialisé (pour éviter d’impacter les autres tests)
        if (this::firebaseAuthMock.isInitialized) {
            firebaseAuthMock.close()
        }
    }

    @Test
    fun `addNewMedicine should inject current user email`() = runTest {
        // --- Mock FirebaseAuth uniquement ici ---
        firebaseAuth = mock()
        firebaseUser = mock()
        `when`(firebaseUser.email).thenReturn("mockuser@test.com")
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)

        firebaseAuthMock = mockStatic(FirebaseAuth::class.java)
        firebaseAuthMock.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(firebaseAuth)
        // ---------------------------------------

        val medicine = Medicine(name = "Paracetamol", stock = 10, nameAisle = "Aisle 1")

        viewModel.addNewMedicine(medicine)

        argumentCaptor<Medicine>().apply {
            verify(repository).addNewMedicine(capture())
            assertEquals("Paracetamol", firstValue.name)
            assertEquals("mockuser@test.com", firstValue.addedByEmail)
        }
    }

    @Test
    fun `getAisleForMedicine returns Aisle with correct name`() {
        val med = Medicine(id = "1", nameAisle = "Fridge")
        fakeMedicinesFlow.value = listOf(med)

        val aisle = viewModel.getAisleForMedicine("1")
        assertEquals("Fridge", aisle.name)
    }

    @Test
    fun `updateMedicine should call repository_updateMedicine`() {
        val med = Medicine(id = "1", name = "Ibuprofen")
        viewModel.updateMedicine(med)
        verify(repository).updateMedicine(med)
    }

    @Test
    fun `deleteMedicine should call repository_deleteMedicine`() {
        viewModel.deleteMedicine("123")
        verify(repository).deleteMedicine("123")
    }

    @Test
    fun `filterByName should call repository_filterByName`() {
        viewModel.filterByName("Par")
        verify(repository).filterByName("Par")
    }

    @Test
    fun `sortByStock should call repository_sortByStock`() {
        viewModel.sortByStock()
        verify(repository).sortByStock()
    }

    @Test
    fun `reloadMedicines should call repository_sortByNone`() {
        viewModel.reloadMedicines()
        verify(repository).sortByNone()
    }

    @Test
    fun `onCleared should clear listener`() = runTest {
        viewModel.onCleared()
        advanceUntilIdle()
        verify(repository, atLeastOnce()).clearListener()
    }
}