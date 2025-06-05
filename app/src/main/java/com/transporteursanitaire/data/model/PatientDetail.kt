package com.transporteursanitaire.data.model

/**
 * Représente les détails d'un patient extraits depuis une autre source (par exemple, une deuxième feuille Excel).
 */
data class PatientDetail(
    val id: Int,             // Doit correspondre à l'identifiant du patient
    val name: String,        // Nom du patient
    val typeTraitement: String,      // Chimio, Radio, Dialyse, Pedopsy
    val otherInfo: String    // Autres informations complémentaires
)