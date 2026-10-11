package com.daragly.daraglyhair.feature.home.domain.model

data class ServiceCategory(
    val id: String,
    val name: String,
    val description: String?,
    val sortOrder: Int,
    val isActive: Boolean,
)
