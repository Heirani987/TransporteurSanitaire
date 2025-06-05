package com.transporteursanitaire.ui.home

// Représente un item sur l'écran HOME
data class HomeItem(
    val title: String,
    val iconRes: Int,
    val destination: HomeDestination
)

enum class HomeDestination {
    PATIENTS,
    PLANNING,
    RDV,
    SYNC
    // Ajoutez d'autres destinations si nécessaire
}