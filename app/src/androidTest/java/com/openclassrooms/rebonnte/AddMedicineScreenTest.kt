package com.openclassrooms.rebonnte

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.models.Aisle
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.medicine.AddNewMedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddNewMedicineScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var medicineViewModel: MedicineViewModel
    private lateinit var aisleViewModel: AisleViewModel
    private lateinit var navController: NavController

    private val fakeAisles = listOf(
        Aisle(name = "Aisle1"),
        Aisle(name = "Aisle2")
    )
    private val aislesFlow = MutableStateFlow(fakeAisles)

    @Before
    fun setup() {
        medicineViewModel = mockk(relaxed = true)
        aisleViewModel = mockk(relaxed = true) {
            every { aisles } returns aislesFlow
        }
        navController = mockk(relaxed = true)
    }

    @Test
    fun shows_all_input_fields() {
        composeTestRule.setContent {
            AddNewMedicineScreen(navController, medicineViewModel, aisleViewModel)
        }

        composeTestRule.onNodeWithTag("addNameMedicineField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("addStockMedicineField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("aisleDropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("addMedicineButton").assertIsDisplayed()
    }

    @Test
    fun shows_snackbar_if_fields_empty() {
        composeTestRule.setContent {
            AddNewMedicineScreen(navController, medicineViewModel, aisleViewModel)
        }

        composeTestRule.onNodeWithTag("addMedicineButton").performClick()

        composeTestRule.onNodeWithText("Please fill all fields").assertIsDisplayed()
        verify(exactly = 0) { medicineViewModel.addNewMedicine(any()) }
    }

    @Test
    fun back_button_pops_navController() {
        composeTestRule.setContent {
            AddNewMedicineScreen(
                navController = navController,
                medicineViewModel = medicineViewModel,
                aisleViewModel = aisleViewModel
            )
        }

        composeTestRule.onNodeWithContentDescription("Retour").performClick()

        verify { navController.popBackStack() }
    }
}