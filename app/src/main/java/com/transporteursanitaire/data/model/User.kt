package com.transporteursanitaire.data.model

data class User(
    val id: String,
    val name: String,
    val role: UserRole
)

enum class UserRole {
    ADMIN,
    CHAUFFEUR,
    ASSISTANT
}