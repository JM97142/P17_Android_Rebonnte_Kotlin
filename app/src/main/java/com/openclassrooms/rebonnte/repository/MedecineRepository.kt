package com.openclassrooms.rebonnte.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.models.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicineRepository(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val MEDICINES_COLLECTION = "medicines"
    }

    private var listenerRegistration: ListenerRegistration? = null

    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    init {
        observeMedicines()
    }

    /**
     * Observe toutes les modifications Firestore en temps réel.
     */
    private fun observeMedicines(
        query: Query = firestore.collection(MEDICINES_COLLECTION)
    ) {
        // Évite les doublons de listeners
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
                    _medicines.value = medList
                    println("Medicines updated: ${medList.size} items")
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

        // Ajout dans le StateFlow pour réactivité UI
        _medicines.value = _medicines.value + medicineWithId

        // Envoi vers Firestore
        docRef.set(medicineWithId)
            .addOnSuccessListener {
                println("Medicine added: ${medicineWithId.name} (ID: ${medicineWithId.id})")
            }
            .addOnFailureListener { e ->
                println("Failed to add medicine: ${e.message}")
                // En cas d'échec Firestore, on retire l'élément ajouté localement
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
     * Supprime un médicament.
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
     * Filtre les médicaments par nom (préfixe)
     */
    fun filterByName(name: String) {
        if (name.isEmpty()) {
            observeMedicines()
        } else {
            val query = firestore.collection(MEDICINES_COLLECTION)
                .orderBy("name")
                .startAt(name)
                .endAt(name + "\uf8ff")

            observeMedicines(query)
        }
    }

    /**
     * Trie par nom (A → Z)
     */
    fun sortByName() {
        val query = firestore.collection(MEDICINES_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING)
        observeMedicines(query)
    }

    /**
     * Trie par stock (croissant)
     */
    fun sortByStock() {
        val query = firestore.collection(MEDICINES_COLLECTION)
            .orderBy("stock", Query.Direction.ASCENDING)
        observeMedicines(query)
    }

    /**
     * Supprime tout tri ou filtre actif.
     */
    fun sortByNone() = observeMedicines()

    /**
     * Détache le listener Firestore pour éviter les fuites.
     */
    fun clearListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}