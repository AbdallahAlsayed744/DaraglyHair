package com.daragly.daraglyhair.feature.home.domain.model

data class UserProfile(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val imageUrl: String?,
) {
    val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { "User" }
}
