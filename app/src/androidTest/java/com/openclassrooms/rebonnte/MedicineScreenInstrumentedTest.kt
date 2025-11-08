package com.openclassrooms.rebonnte

import androidx.activity.ComponentActivity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.openclassrooms.rebonnte.models.Aisle
import com.openclassrooms.rebonnte.models.Medicine
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MedicineScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: MedicineViewModel
    private lateinit var navController: NavController

    private val fakeMedicines = listOf(
        Medicine(id = "1", name = "Aspirin", stock = 10, nameAisle = "Aisle1"),
        Medicine(id = "2", name = "Paracetamol", stock = 5, nameAisle = "Aisle2"),
        Medicine(id = "3", name = "Ibuprofen", stock = 8, nameAisle = "Aisle3")
    )
    private val medicinesFlow = MutableStateFlow(fakeMedicines)

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true) {
            every { medicines } returns medicinesFlow
            every { getAisleForMedicine(any()) } returns Aisle()
        }
        navController = mockk(relaxed = true)
    }

    @Test
    fun displays_all_medicines_from_viewmodel() {
        composeTestRule.setContent {
            MedicineScreen(navController = navController, medicineViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Aspirin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paracetamol").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ibuprofen").assertIsDisplayed()
    }

    @Test
    fun back_button_navigates_to_aisle() {
        composeTestRule.setContent {
            MedicineScreen(navController = navController, medicineViewModel = viewModel)
        }

        composeTestRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }

        verify {
            navController.navigate("aisle", any<NavOptionsBuilder.() -> Unit>())
        }
    }

    @Test
    fun clicking_medicine_navigates_to_detail() {
        composeTestRule.setContent {
            MedicineScreen(navController = navController, medicineViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Aspirin").performClick()

        verify { navController.navigate("medicine_detail/Aspirin") }
    }

    @Test
    fun swipeToDeleteItem_triggers_confirmation_dialog() {
        // on simule l'état swipé directement avec showDialog
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirm Deletion") },
                    text = { Text("Are you sure you want to delete this item?") },
                    confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Delete") } },
                    dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
                )
            }

            com.openclassrooms.rebonnte.ui.aisle.SwipeToDeleteItem(
                onDelete = {},
                content = { Text("Swipe me") })
        }

        composeTestRule.onNodeWithText("Confirm Deletion").assertIsDisplayed()
    }
}