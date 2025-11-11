package com.openclassrooms.rebonnte.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.models.Aisle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository responsable de la gestion des rayons (Aisles) dans Firestore.
 * Fournit un flux (StateFlow) réactif de la liste des rayons,
 * et maintient un listener Firestore en temps réel.
 */
class AisleRepository(private val firestore: FirebaseFirestore) {

    private val _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> = _aisles.asStateFlow()

    private val AISLES_COLLECTION = "aisles"

    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadAisles()
    }

    /**
     * Attache (ou ré-attache) le listener Firestore pour la collection "aisles".
     * Écoute en temps réel toutes les modifications.
     *
     * Si la collection est vide au premier lancement, crée et affiche un rayon par défaut.
     */
    fun loadAisles() {
        listenerRegistration?.remove()

        val collectionRef = firestore.collection(AISLES_COLLECTION)

        // Vérifie si la collection est vide avant d'attacher le listener
        collectionRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                val defaultAisle = Aisle("Aisle 1")
                addAisle(defaultAisle)
            }

            listenerRegistration = collectionRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshotListener, e ->
                    if (e != null) {
                        println("Snapshot listener error: $e")
                        return@addSnapshotListener
                    }

                    val aisleList = snapshotListener?.documents?.mapNotNull {
                        it.toObject(Aisle::class.java)
                    } ?: emptyList()

                    _aisles.value = aisleList
                }
        }
    }

    /**
     * Ajoute un rayon (Aisle) dans Firestore.
     * Si l’ajout réussit, Firestore déclenchera automatiquement une mise à jour via le listener.
     */
    fun addAisle(aisle: Aisle) {
        firestore.collection(AISLES_COLLECTION)
            .document(aisle.id)
            .set(aisle)
            .addOnSuccessListener {
                println("Aisle added successfully: ${aisle.name}")
            }
            .addOnFailureListener { e ->
                println("Failed to add aisle: $e")
            }
    }

    /**
     * Supprime un rayon existant dans Firestore.
     * La suppression est automatiquement répercutée sur l’UI via le listener.
     */
    fun deleteAisle(aisle: Aisle) {
        firestore.collection(AISLES_COLLECTION)
            .document(aisle.id)
            .delete()
            .addOnSuccessListener {
                println("Aisle deleted: ${aisle.id}")
            }
            .addOnFailureListener { e ->
                println("Failed to delete aisle: ${e.message}")
            }
    }

    /**
     * Détache proprement le listener Firestore.
     * À appeler lors de la destruction du ViewModel pour éviter les fuites mémoire.
     */
    fun clearListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
        println("AisleRepository: Firestore listener detached")
    }
}