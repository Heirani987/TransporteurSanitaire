package com.transporteursanitaire.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Patient pour la base de données Room.
 * Chaque patient possède un identifiant unique, un nom, des informations médicales
 * et des détails de transport.
 */
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,              // Identifiant du patient (auto-généré par Room)
    val name: String,             // Nom du patient

    // Informations médicales et administratives
    val birthDate: String? = null,        // Date de naissance
    val dep: String? = null,              // DEP
    val da: String? = null,               // DA
    val validiteDaDebut: String? = null,  // Début de validité DA
    val validiteDaFin: String? = null,    // Fin de validité DA

    // Détails de transport
    val transportDu: String? = null,     // Date de début du transport
    val transportAu: String? = null,     // Date de fin du transport
    val adresseGeographique: String? = null, // Adresse géographique
    val trajet: String? = null,          // Type de trajet
    val pointPcArrivee: String? = null,  // Point PC d'arrivée du patient
    val complementAdresse: String? = null, // Complément d’adresse
    val tel: String? = null,             // Numéro de téléphone

    // Type de traitement du patient (chimio, radio, dialyse, etc.)
    val typeTraitement: String? = null   // Champ pour indiquer le type de traitement
)