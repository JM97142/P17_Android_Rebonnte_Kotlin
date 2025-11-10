package com.openclassrooms.rebonnte.ui.aisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.models.Aisle
import com.openclassrooms.rebonnte.repository.AisleRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de la gestion des rayons (Aisles).
 * Sert d’intermédiaire entre l’interface utilisateur et le AisleRepository (Firestore).
 */
class AisleViewModel(
    private val repository: AisleRepository
) : ViewModel() {

    val aisles: StateFlow<List<Aisle>> = repository.aisles

    init {
        viewModelScope.launch {
            repository.loadAisles()
        }
    }

    // Ajoute un nouveau rayon de manière automatique (utile pour les tests).
    //Exemple : si tu as 3 rayons, cela créera "Aisle 4".
    fun addRandomAisle() {
        val currentAisles = aisles.value
        val newAisle = Aisle(name = "Aisle ${currentAisles.size + 1}")
        repository.addAisle(newAisle)
    }

    // Recharge manuellement la liste des rayons depuis Firestore.
    // En pratique, ce n’est presque jamais nécessaire car le listener est temps réel.
    fun reloadAisles() {
        repository.loadAisles()
    }

    // Supprime un rayon de Firestore.
    fun deleteAisle(aisle: Aisle) {
        repository.deleteAisle(aisle)
    }

    // Nettoie le listener Firestore lorsque le ViewModel est détruit.
    // Cela évite les fuites mémoire et les mises à jour après fermeture d’écran.
    public override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.clearListener()
            println("Aisle listener detached (ViewModel cleared)")
        }
    }
}