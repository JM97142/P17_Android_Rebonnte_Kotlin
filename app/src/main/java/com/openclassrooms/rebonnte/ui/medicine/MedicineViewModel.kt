package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.models.Aisle
import com.openclassrooms.rebonnte.models.Medicine
import com.openclassrooms.rebonnte.repository.MedicineRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random

/**
 * ViewModel responsable de la gestion des médicaments.
 * Il sert d’intermédiaire entre l’UI (Compose) et le Repository (Firestore).
 */
class MedicineViewModel(
    private val repository: MedicineRepository
) : ViewModel() {

    /**
     * Flux (StateFlow) exposant la liste des médicaments en temps réel.
     * Le repository met à jour cette valeur automatiquement via Firestore.
     */
    val medicines: StateFlow<List<Medicine>> = repository.medicines

    /**
     *   Ajoute un médicament aléatoire pour tester rapidement l’app.
     * - Sélectionne un rayon au hasard
     * - Assigne un stock et un nom aléatoire
     * - Attribue l’utilisateur Firebase courant (ou "anonymous" si non connecté)
     */
    fun addRandomMedicine(aisles: List<Aisle>) {
        if (aisles.isEmpty()) return

        val addingUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "anonymous"

        val newMedicine = Medicine(
            name = "Medicine ${Random().nextInt(1000)}",
            stock = Random().nextInt(100),
            nameAisle = aisles.random().name, // version plus Kotlin-friendly
            histories = emptyList(),
            addedByEmail = addingUserEmail
        )

        repository.addNewMedicine(newMedicine)
    }

    /**
     * Ajoute un nouveau médicament renseigné manuellement par l’utilisateur.
     * On insère l’adresse mail de l’utilisateur connecté.
     */
    fun addNewMedicine(medicine: Medicine) {
        val addingUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "anonymous"
        val updatedMedicine = medicine.copy(addedByEmail = addingUserEmail)
        repository.addNewMedicine(updatedMedicine)
    }

    /**
     * Trouve le rayon correspondant à un médicament à partir de son ID.
     * Cette méthode est utilisée par l’UI pour afficher le nom du rayon dans la liste.
     */
    fun getAisleForMedicine(medicineId: String): Aisle {
        val medicine = medicines.value.find { it.id == medicineId }
        return Aisle(name = medicine?.nameAisle ?: "Unknown")
    }

    /**
     * Met à jour les informations d’un médicament existant (nom, stock, etc.).
     */
    fun updateMedicine(medicine: Medicine) {
        repository.updateMedicine(medicine)
    }

    /**
     * Supprime un médicament à partir de son ID Firestore.
     */
    fun deleteMedicine(medicineId: String) {
        repository.deleteMedicine(medicineId)
    }

    /**
     * Filtre les médicaments par leur nom (préfixe).
     * Exemple : taper "Par" retournera "Paracetamol", "Paro...", etc.
     */
    fun filterByName(name: String) {
        repository.filterByName(name)
    }

    /**
     * Trie les médicaments par nom (ordre alphabétique croissant).
     */
    fun sortByName() {
        repository.sortByName()
    }

    /**
     * Trie les médicaments par leur stock (quantité croissante).
     */
    fun sortByStock() {
        repository.sortByStock()
    }

    /**
     * Supprime tout tri ou filtre actif et recharge la liste complète.
     */
    fun sortByNone() {
        repository.sortByNone()
    }

    /**
     * Recharge manuellement les médicaments depuis Firestore.
     */
    fun reloadMedicines() {
        repository.sortByNone()
    }

    /**
     * Nettoie le listener Firestore quand le ViewModel est détruit.
     * Cela évite les fuites mémoire et les mises à jour inutiles après fermeture d’écran.
     */
    public override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.clearListener()
            println("Medicine listener detached (ViewModel cleared)")
        }
    }
}