package com.openclassrooms.rebonnte.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.openclassrooms.rebonnte.models.Aisle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
     */
    fun loadAisles() {
        // Retirer l'ancien listener si présent pour éviter duplication
        listenerRegistration?.remove()

        listenerRegistration = firestore.collection(AISLES_COLLECTION)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Snapshot listener error: $e")
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    println("Snapshot is null")
                    return@addSnapshotListener
                }
                val aisleList = snapshot.documents.mapNotNull { it.toObject(Aisle::class.java) }
                println("Aisles updated: $aisleList")
                _aisles.value = aisleList

                // Si collection vide, ajouter un rayon par défaut
                if (aisleList.isEmpty()) {
                    addAisle(Aisle("Aisle 1"))
                }
            }
    }

    /**
     * Ajoute un aisle en Firestore.
     */
    fun addAisle(aisle: Aisle) {
        firestore.collection(AISLES_COLLECTION).document(aisle.id).set(aisle)
            .addOnSuccessListener {
                println("Aisle ${aisle.name} added successfully, ID: ${aisle.id}")
                // évite reload forcé si listener est correctement installé, mais tu peux le faire si nécessaire
                // loadAisles()
            }
            .addOnFailureListener { e -> println("Failed to add aisle: $e") }
    }

    /**
     * Supprime un aisle en Firestore.
     */
    fun deleteAisle(aisle: Aisle) {
        firestore.collection(AISLES_COLLECTION)
            .document(aisle.id)
            .delete()
            .addOnSuccessListener { println("Aisle deleted: ${aisle.id}") }
            .addOnFailureListener { e -> println("Failed to delete aisle: ${e.message}") }
    }

    /**
     * Détache le listener (évite fuites mémoire lors du clear du ViewModel / destroy).
     */
    fun clearListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
        println("AisleRepository: Firestore listener detached")
    }
}