package com.daragly.daraglyhair.feature.user.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Mirrors the API FileUploadRequest — image bytes travel as a base64 string.
 * name, extension and data are all required when an image is sent.
 */
@Serializable
data class FileUploadRequestDto(
    @SerialName("name")
    val name: String,
    @SerialName("extension")
    val extension: String,
    @SerialName("data")
    val data: String,
)

/**
 * Body of PUT /api/personal/profile (UpdateUserRequest).
 * id, firstName, lastName and email are required by the API; first/last name max 75 chars.
 */
@Serializable
data class UpdateUserRequestDto(
    @SerialName("id")
    val id: String,
    @SerialName("firstName")
    val firstName: String,
    @SerialName("lastName")
    val lastName: String,
    @SerialName("email")
    val email: String,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,
    @SerialName("image")
    val image: FileUploadRequestDto? = null,
    @SerialName("deleteCurrentImage")
    val deleteCurrentImage: Boolean = false,
)
