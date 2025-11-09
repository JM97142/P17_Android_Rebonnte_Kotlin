package com.openclassrooms.rebonnte

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.models.Medicine
import com.openclassrooms.rebonnte.ui.aisle.AisleDetailScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AisleDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: MedicineViewModel
    private lateinit var navController: NavController

    private val fakeMedicines = listOf(
        Medicine(name = "Aspirin", stock = 10, nameAisle = "Aisle1"),
        Medicine(name = "Paracetamol", stock = 5, nameAisle = "Aisle1"),
        Medicine(name = "Ibuprofen", stock = 8, nameAisle = "Aisle2")
    )
    private val medicinesFlow = MutableStateFlow(fakeMedicines)

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true) {
            every { medicines } returns medicinesFlow
        }
        navController = mockk(relaxed = true)
    }

    @Test
    fun displays_medicines_for_given_aisle() {
        composeTestRule.setContent {
            AisleDetailScreen(name = "Aisle1", viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithText("Aisle: Aisle1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aspirin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paracetamol").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ibuprofen").assertDoesNotExist()
    }

    @Test
    fun shows_no_medicines_text_when_list_empty() {
        composeTestRule.setContent {
            AisleDetailScreen(name = "EmptyAisle", viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithText("No medicines in this aisle").assertIsDisplayed()
    }

    @Test
    fun clicking_medicine_navigates_to_detail() {
        composeTestRule.setContent {
            AisleDetailScreen(name = "Aisle1", viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithText("Aspirin").performClick()

        verify { navController.navigate("medicine_detail/Aspirin") }
    }

    @Test
    fun back_button_pops_back_stack() {
        composeTestRule.setContent {
            AisleDetailScreen(name = "Aisle2", viewModel = viewModel, navController = navController)
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        verify { navController.popBackStack() }
    }

    @Test
    fun unknown_aisle_pops_back_stack_immediately() {
        composeTestRule.setContent {
            AisleDetailScreen(name = "Unknown", viewModel = viewModel, navController = navController)
        }

        // Vérifie que popBackStack a été appelé automatiquement
        verify { navController.popBackStack() }
    }
}