package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceCategoryDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("nameAr")
    val nameAr: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("sortOrder")
    val sortOrder: Int = 0,
    @SerialName("isActive")
    val isActive: Boolean = true,
)
