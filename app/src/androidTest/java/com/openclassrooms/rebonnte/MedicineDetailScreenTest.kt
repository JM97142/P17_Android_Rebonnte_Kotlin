package com.openclassrooms.rebonnte

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.openclassrooms.rebonnte.models.History
import com.openclassrooms.rebonnte.models.Medicine
import com.openclassrooms.rebonnte.ui.medicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MedicineDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: MedicineViewModel
    private var backCalled = false

    private val fakeMedicine = Medicine(
        id = "1",
        name = "Aspirin",
        stock = 10,
        nameAisle = "Aisle1",
        histories = listOf(
            History("Aspirin", "user@example.com", "2025-11-08", "Created")
        )
    )

    private val medicinesFlow = MutableStateFlow(listOf(fakeMedicine))

    @Before
    fun setup() {
        // Mock détendu du ViewModel
        viewModel = mockk(relaxed = true) {
            every { medicines } returns medicinesFlow
        }
        backCalled = false
    }

    @Test
    fun displays_medicine_details() {
        composeTestRule.setContent {
            MedicineDetailScreen(name = "Aspirin", viewModel = viewModel) { backCalled = true }
        }

        composeTestRule.onNodeWithText("Medicine: Aspirin").assertExists()

        // Vérifie nom + allée + stock visibles
        composeTestRule.onAllNodes(hasText("Aspirin", substring = true))
            .assertAny(hasText("Aspirin", substring = true))

        composeTestRule.onAllNodes(hasText("Aisle1", substring = true))
            .assertAny(hasText("Aisle1", substring = true))

        composeTestRule.onAllNodes(hasText("10", substring = true))
            .assertAny(hasText("10", substring = true))

        // Section historique
        composeTestRule.onNodeWithText("History").assertExists()

        // Entrée d'historique — chercher "Created" partiellement
        composeTestRule.onAllNodes(hasText("Created", substring = true))
            .assertAny(hasText("Created", substring = true))
    }

    @Test
    fun back_button_calls_onBack() {
        composeTestRule.setContent {
            MedicineDetailScreen(name = "Aspirin", viewModel = viewModel) { backCalled = true }
        }

        composeTestRule.onNodeWithContentDescription("Retour à la liste des médicaments").performClick()
        assert(backCalled)
    }

    @Test
    fun unknown_name_calls_onBack_immediately() {
        composeTestRule.setContent {
            MedicineDetailScreen(name = "Unknown", viewModel = viewModel) { backCalled = true }
        }

        assert(backCalled)
    }

    @Test
    fun displays_loading_text_when_medicine_not_found() {
        composeTestRule.setContent {
            MedicineDetailScreen(name = "NonExistent", viewModel = viewModel) { backCalled = true }
        }

        composeTestRule.onNodeWithText("Loading medicine details...").assertIsDisplayed()
    }

    @Test
    fun clicking_plus_button_increases_stock_and_calls_updateMedicine() {
        val slot = slot<com.openclassrooms.rebonnte.models.Medicine>()
        every { viewModel.updateMedicine(capture(slot)) } just Runs

        composeTestRule.setContent {
            MedicineDetailScreen(name = "Aspirin", viewModel = viewModel) { backCalled = true }
        }

        // Clique sur le bouton "+"
        composeTestRule.onNodeWithContentDescription("Augmenter le stock de 1").performClick()

        verify(exactly = 1) { viewModel.updateMedicine(any()) }

        assert(slot.captured.stock == fakeMedicine.stock + 1)
    }
}