package com.daragly.daraglyhair.feature.profile.ui

import androidx.compose.ui.graphics.painter.Painter
import com.daragly.daraglyhair.feature.user.domain.model.ProfileImage

// ── UI State ──────────────────────────────────────────────────────────────────

data class EditProfileState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    // URI string of the newly picked image (null = no change / no image yet)
    val imageUri: String? = null,
    // Painter resolved from imageUri for preview — null uses the placeholder icon
    val imagePainter: Painter? = null,
    // Raw data of the newly picked image, sent to the API on save
    val pickedImage: ProfileImage? = null,
    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val saveError: String? = null,
)

// ── User intents ──────────────────────────────────────────────────────────────

sealed interface EditProfileAction {
    data object Load : EditProfileAction
    data class FirstNameChanged(val value: String) : EditProfileAction
    data class LastNameChanged(val value: String) : EditProfileAction
    data class PhoneChanged(val value: String) : EditProfileAction
    data class EmailChanged(val value: String) : EditProfileAction
    /** Called by the platform-specific gallery picker with the chosen image */
    data class ImagePicked(
        val uriString: String,
        val painter: Painter,
        val image: ProfileImage?,
    ) : EditProfileAction
    data object SaveClicked : EditProfileAction
    data object BackClicked : EditProfileAction
}

// ── One-shot side effects ─────────────────────────────────────────────────────

sealed interface EditProfileEvent {
    data object NavigateBack : EditProfileEvent
    data object SavedSuccessfully : EditProfileEvent
}
