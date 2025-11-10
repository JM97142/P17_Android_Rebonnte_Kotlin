package com.openclassrooms.rebonnte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.openclassrooms.rebonnte.ui.composables.EmbeddedSearchBar
import org.junit.Rule
import org.junit.Test

class EmbeddedSearchBarTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun searchBar_displaysPlaceholder_and_allowsTextInput() {
        var typed = ""

        composeRule.setContent {
            EmbeddedSearchBar(
                query = typed,
                onQueryChange = { typed = it },
                isSearchActive = false,
                onActiveChanged = {}
            )
        }

        // Vérifie que le champ existe
        composeRule.onNodeWithTag("SearchBarTextField").assertExists()
        // Placeholder visible
        composeRule.onNodeWithText("Search").assertExists()

        // Tape du texte
        composeRule.onNodeWithTag("SearchBarTextField").performTextReplacement("Paracetamol")

        composeRule.runOnIdle {
            assert(typed == "Paracetamol")
        }
    }

    @Test
    fun leadingIcon_togglesActiveState() {
        var isActive = false

        composeRule.setContent {
            EmbeddedSearchBar(
                query = "",
                onQueryChange = {},
                isSearchActive = isActive,
                onActiveChanged = { isActive = it }
            )
        }

        // Clic sur l’icône de recherche
        composeRule.onNodeWithTag("SearchBarLeadingIcon").assertExists().performClick()

        composeRule.runOnIdle {
            assert(isActive)
        }
    }

    @Test
    fun clearButton_clearsTextWhenActive() {
        var text = "Doliprane"
        var active = true

        composeRule.setContent {
            EmbeddedSearchBar(
                query = text,
                onQueryChange = { text = it },
                isSearchActive = active,
                onActiveChanged = { active = it }
            )
        }

        // Vérifie que le bouton de clear est visible
        composeRule.onNodeWithTag("SearchBarClearButton").assertExists().performClick()

        composeRule.runOnIdle {
            assert(text.isEmpty())
        }
    }

    @Test
    fun searchBar_filtersMedicineList_locally() {
        // 🔹 État du texte de recherche
        var query by mutableStateOf("")

        // 🔹 Liste simulée
        val medicines = listOf("Paracetamol", "Ibuprofen", "Aspirin")

        composeRule.setContent {
            // Barre de recherche
            EmbeddedSearchBar(
                query = query,
                onQueryChange = { query = it },
                isSearchActive = true,
                onActiveChanged = {}
            )

            // Liste filtrée en direct
            val filtered = medicines.filter { it.contains(query, ignoreCase = true) }

            LazyColumn {
                items(filtered) { med ->
                    Text(
                        text = med,
                        modifier = Modifier.testTag("MedicineItem_$med")
                    )
                }
            }
        }

        // 🔹 Vérifie que tous les médicaments apparaissent au départ
        composeRule.onNodeWithTag("MedicineItem_Paracetamol").assertExists()
        composeRule.onNodeWithTag("MedicineItem_Ibuprofen").assertExists()
        composeRule.onNodeWithTag("MedicineItem_Aspirin").assertExists()

        // 🔹 Tape "para" → doit ne garder que Paracetamol
        composeRule.onNodeWithTag("SearchBarTextField")
            .performTextReplacement("para")

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("MedicineItem_Paracetamol").assertExists()
        composeRule.onNodeWithTag("MedicineItem_Ibuprofen").assertDoesNotExist()
        composeRule.onNodeWithTag("MedicineItem_Aspirin").assertDoesNotExist()
    }
}