package com.openclassrooms.rebonnte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.rebonnte.models.Aisle
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.medicine.AddNewMedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests instrumentés pour vérifier le bon fonctionnement de l’écran AddNewMedicineScreen.
 * Les ViewModels et la navigation sont mockés afin de tester uniquement l’UI Compose.
 */
@RunWith(AndroidJUnit4::class)
class AddNewMedicineScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var medicineViewModel: MedicineViewModel
    private lateinit var aisleViewModel: AisleViewModel
    private lateinit var navController: NavController

    private val fakeAislesFlow = MutableStateFlow(
        listOf(
            Aisle(name = "Aisle 1"),
            Aisle(name = "Aisle 2")
        )
    )

    @Before
    fun setup() {
        // Mock des dépendances
        medicineViewModel = mockk(relaxed = true)
        aisleViewModel = mockk(relaxed = true)
        navController = mockk(relaxed = true)

        every { aisleViewModel.aisles } returns fakeAislesFlow as StateFlow<List<Aisle>>

        // Lancement du composable à tester
        composeTestRule.setContent {
            AddNewMedicineScreen(
                navController = navController,
                medicineViewModel = medicineViewModel,
                aisleViewModel = aisleViewModel
            )
        }
    }

    @Test
    fun displays_all_basic_elements() {
        // Vérifie la présence du titre, des champs et du bouton
        composeTestRule.onNodeWithTag("topBarTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("addNameMedicineField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("addStockMedicineField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("aisleDropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("addMedicineButton").assertIsDisplayed()
    }

    @Test
    fun shows_error_message_when_fields_are_empty() {
        // Clique sur le bouton sans remplir les champs
        composeTestRule.onNodeWithTag("addMedicineButton").performClick()

        // Vérifie que le message d’erreur apparaît
        composeTestRule.onNodeWithText("Please fill all fields")
            .assertIsDisplayed()
    }

    @Test
    fun selects_aisle_correctly_from_dropdown() {
        // Ouvre le menu déroulant
        composeTestRule.onNodeWithTag("aisleDropdown").performClick()

        // Sélectionne un élément du menu
        composeTestRule.onNodeWithTag("aisleItem_Aisle 1").performClick()

        // Vérifie que le champ affiche bien l’allée sélectionnée
        composeTestRule.onNodeWithText("Aisle 1").assertIsDisplayed()
    }

    @Test
    fun calls_addNewMedicine_in_viewmodel_when_button_clicked() {
        // Remplis les champs
        composeTestRule.onNodeWithTag("addNameMedicineField").performTextInput("Doliprane")
        composeTestRule.onNodeWithTag("addStockMedicineField").performTextInput("50")

        // Sélectionne une allée
        composeTestRule.onNodeWithTag("aisleDropdown").performClick()
        composeTestRule.onNodeWithTag("aisleItem_Aisle 2").performClick()

        // Clique sur le bouton d’ajout
        composeTestRule.onNodeWithTag("addMedicineButton").performClick()

        // Vérifie que addNewMedicine() a bien été appelé
        verify(exactly = 1) {
            medicineViewModel.addNewMedicine(
                match {
                    it.name == "Doliprane" &&
                            it.stock == 50 &&
                            it.nameAisle == "Aisle 2"
                }
            )
        }
    }

    @Test
    fun shows_loading_indicator_during_add_process() {
        // On simule un état de chargement en remplissant tous les champs
        composeTestRule.onNodeWithTag("addNameMedicineField").performTextInput("Ibuprofen")
        composeTestRule.onNodeWithTag("addStockMedicineField").performTextInput("30")
        composeTestRule.onNodeWithTag("aisleDropdown").performClick()
        composeTestRule.onNodeWithTag("aisleItem_Aisle 1").performClick()

        // Clique sur le bouton pour lancer le chargement
        composeTestRule.onNodeWithTag("addMedicineButton").performClick()

        // Pendant l’exécution, le texte "Adding..." devrait s’afficher
        composeTestRule.onNodeWithText("Adding...").assertExists()
    }
}