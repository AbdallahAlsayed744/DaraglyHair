package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    @SerialName("data")
    val data: List<T>? = null,
    @SerialName("items")
    val items: List<T>? = null,
    @SerialName("currentPage")
    val currentPage: Int = 1,
    @SerialName("totalPages")
    val totalPages: Int = 0,
    @SerialName("totalCount")
    val totalCount: Int = 0,
    @SerialName("pageSize")
    val pageSize: Int = 10,
    @SerialName("hasPreviousPage")
    val hasPreviousPage: Boolean = false,
    @SerialName("hasNextPage")
    val hasNextPage: Boolean = false,
) {
    // Helper property to get the actual list regardless of field name
    fun getItemsList(): List<T> = data ?: items ?: emptyList()
}
