package com.openclassrooms.rebonnte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.LifecycleCoroutineScope
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.composables.UiApp
import com.openclassrooms.rebonnte.ui.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class UiAppInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mock des ViewModels
    private val loginViewModel = mockk<LoginViewModel>(relaxed = true)
    private val aisleViewModel = mockk<AisleViewModel>(relaxed = true)
    private val medicineViewModel = mockk<MedicineViewModel>(relaxed = true)

    // Mock des clients d’authentification
    private val googleAuthClient = mockk<GoogleAuthClient>(relaxed = true)
    private val emailAuthClient = mockk<EmailAuthClient>(relaxed = true)

    // Mock du lifecycleScope
    private val testLifecycleScope = mockk<LifecycleCoroutineScope>(relaxed = true)

    @Test
    fun searchBar_updatesQueryAndCallsViewModel() {
        // Mock flow pour medicines
        every { medicineViewModel.medicines } returns MutableStateFlow(emptyList())
        every { medicineViewModel.filterByName(any()) } returns Unit

        // Lance le composable UiApp
        composeTestRule.setContent {
            UiApp(
                googleAuthUiClient = googleAuthClient,
                emailAuthClient = emailAuthClient,
                lifecycleScope = testLifecycleScope,
                loginViewModel = loginViewModel,
                aisleViewModel = aisleViewModel,
                medicineViewModel = medicineViewModel
            )
        }

        // Simule la recherche : tape dans le TextField
        val searchNode = composeTestRule.onNode(
            hasSetTextAction() and hasText("Search"),
            useUnmergedTree = true
        )
        searchNode.assertExists()
        searchNode.performTextInput("Aspirin")

        // Vérifie que le ViewModel a été appelé
        verify { medicineViewModel.filterByName("Aspirin") }
    }

    @Test
    fun fab_callsViewModelOrNavigates() {
        // Mock pour aisleViewModel
        every { aisleViewModel.addRandomAisle() } returns Unit

        // Lance le composable UiApp
        composeTestRule.setContent {
            UiApp(
                googleAuthUiClient = googleAuthClient,
                emailAuthClient = emailAuthClient,
                lifecycleScope = testLifecycleScope,
                loginViewModel = loginViewModel,
                aisleViewModel = aisleViewModel,
                medicineViewModel = medicineViewModel
            )
        }

        // Clique sur le FAB (FloatingActionButton)
        composeTestRule.onNodeWithContentDescription("Add").performClick()

        // Vérifie que la méthode addRandomAisle du ViewModel a été appelée
        verify { aisleViewModel.addRandomAisle() }
    }
}