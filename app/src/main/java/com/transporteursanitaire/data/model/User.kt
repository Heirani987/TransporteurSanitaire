package com.transporteursanitaire.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String? = null,
    val role: UserRole = UserRole.CHAUFFEUR,
    val isActive: Boolean = true // Pour désactiver un user sans le supprimer
)

enum class UserRole {
    ADMIN,
    CHAUFFEUR,
    ASSISTANT
}