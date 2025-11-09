package com.openclassrooms.rebonnte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.activity.ComponentActivity
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
}