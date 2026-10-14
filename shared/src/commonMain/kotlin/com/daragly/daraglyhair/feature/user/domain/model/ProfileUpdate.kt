package com.daragly.daraglyhair.feature.user.domain.model

/** A freshly picked image, ready for upload as a FileUploadRequest. */
data class ProfileImage(
    val fileName: String,
    /** Including the leading dot, e.g. ".jpg". */
    val extension: String,
    val dataBase64: String,
)

/** Payload for PUT /api/personal/profile. */
data class ProfileUpdate(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val image: ProfileImage? = null,
    val deleteCurrentImage: Boolean = false,
)
