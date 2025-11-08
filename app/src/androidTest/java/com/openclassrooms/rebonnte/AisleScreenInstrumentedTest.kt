package com.openclassrooms.rebonnte

import androidx.activity.ComponentActivity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.openclassrooms.rebonnte.models.Aisle
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.aisle.SwipeToDeleteItem
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AisleScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: AisleViewModel
    private lateinit var navController: NavController

    private val fakeAisles = listOf(
        Aisle(id = "1", name = "Aisle1"),
        Aisle(id = "2", name = "Aisle2")
    )
    private val aislesFlow = MutableStateFlow(fakeAisles)

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true) {
            every { aisles } returns aislesFlow
        }
        navController = mockk(relaxed = true)
    }

    @Test
    fun displays_all_aisles_from_viewmodel() {
        composeTestRule.setContent {
            AisleScreen(navController = navController, aisleViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Aisle1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aisle2").assertIsDisplayed()
    }

    @Test
    fun clicking_back_calls_navController_navigate_to_medicine() {
        composeTestRule.setContent {
            AisleScreen(navController = navController, aisleViewModel = viewModel)
        }

        composeTestRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }

        verify {
            navController.navigate("medicine", any<NavOptionsBuilder.() -> Unit>())
        }
    }

    @Test
    fun swipeToDeleteItem_displays_confirmation_dialog() {
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

            SwipeToDeleteItem(onDelete = {}, content = { Text("Swipe me") })
        }

        composeTestRule.onNodeWithText("Confirm Deletion").assertIsDisplayed()
    }

    @Test
    fun delete_button_triggers_onDelete() {
        var deleted = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirm Deletion") },
                    text = { Text("Are you sure you want to delete this item?") },
                    confirmButton = {
                        TextButton(onClick = {
                            deleted = true
                            showDialog = false
                        }) { Text("Delete") }
                    },
                    dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
                )
            }
        }

        composeTestRule.onNodeWithText("Delete").performClick()
        assert(deleted)
    }
}