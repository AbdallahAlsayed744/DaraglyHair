package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    @SerialName("items")
    val items: List<T> = emptyList(),
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
)
