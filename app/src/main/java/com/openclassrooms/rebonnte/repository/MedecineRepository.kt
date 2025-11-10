package com.openclassrooms.rebonnte.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.models.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository responsable de la gestion des médicaments (Medicines).
 * Observe Firestore en temps réel.
 * Permet le tri distant (Firestore).
 */
class MedicineRepository(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val MEDICINES_COLLECTION = "medicines"
    }

    private var listenerRegistration: ListenerRegistration? = null

    // Flux exposé vers l’UI
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    private var allMedicines: List<Medicine> = emptyList()

    init {
        observeMedicines()
    }

    /**
     * Observe toutes les modifications Firestore en temps réel.
     * Peut être rappelée avec une requête triée.
     */
    private fun observeMedicines(
        query: Query = firestore.collection(MEDICINES_COLLECTION)
    ) {
        // Retire tout listener précédent (évite doublons)
        listenerRegistration?.remove()

        listenerRegistration = query.addSnapshotListener { snapshot, error ->
            when {
                error != null -> {
                    println("Firestore listener error: ${error.message}")
                }
                snapshot != null -> {
                    val medList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Medicine::class.java)?.copy(id = doc.id)
                    }
                    allMedicines = medList
                    _medicines.value = medList
                }
                else -> println("Snapshot null without error.")
            }
        }
    }

    /**
     * Ajoute un nouveau médicament dans Firestore.
     */
    fun addNewMedicine(medicine: Medicine) {
        val docRef = firestore.collection(MEDICINES_COLLECTION).document()
        val medicineWithId = medicine.copy(id = docRef.id)

        // Ajout optimiste local
        _medicines.value = _medicines.value + medicineWithId

        // Envoi vers Firestore
        docRef.set(medicineWithId)
            .addOnSuccessListener {
                println("Medicine added: ${medicineWithId.name} (ID: ${medicineWithId.id})")
            }
            .addOnFailureListener { e ->
                println("Failed to add medicine: ${e.message}")
                _medicines.value = _medicines.value.filterNot { it.id == medicineWithId.id }
            }
    }

    /**
     * Met à jour un médicament existant.
     */
    fun updateMedicine(medicine: Medicine) {
        if (medicine.id.isEmpty()) {
            println("Cannot update medicine with empty ID.")
            return
        }

        firestore.collection(MEDICINES_COLLECTION)
            .document(medicine.id)
            .set(medicine)
            .addOnSuccessListener {
                println("Medicine updated: ${medicine.name} (${medicine.stock})")
            }
            .addOnFailureListener { e ->
                println("Failed to update medicine: ${e.message}")
            }
    }

    /**
     * Supprime un médicament à partir de son ID.
     */
    fun deleteMedicine(medicineId: String) {
        firestore.collection(MEDICINES_COLLECTION)
            .document(medicineId)
            .delete()
            .addOnSuccessListener {
                println("Medicine deleted: $medicineId")
            }
            .addOnFailureListener { e ->
                println("Failed to delete medicine: ${e.message}")
            }
    }

    /**
     * Filtre les médicaments par nom.
     */
    fun filterByName(name: String) {
        _medicines.value = if (name.isBlank()) {
            allMedicines
        } else {
            allMedicines.filter {
                it.name.contains(name, ignoreCase = true)
            }
        }
    }

    /**
     * Trie par nom (ordre alphabétique croissant) via Firestore.
     */
    fun sortByName() {
        val query = firestore.collection(MEDICINES_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
        observeMedicines(query)
    }

    /**
     * Trie par stock (ordre croissant) via Firestore.
     */
    fun sortByStock() {
        val query = firestore.collection(MEDICINES_COLLECTION)
            .orderBy("stock", Query.Direction.ASCENDING)
        observeMedicines(query)
    }

    /**
     * Supprime tout tri ou filtre actif et recharge la liste complète.
     */
    fun sortByNone() {
        observeMedicines()
    }

    /**
     * Détache le listener Firestore (appelé lors du onCleared() du ViewModel).
     */
    fun clearListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}