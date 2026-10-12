package com.daragly.daraglyhair.feature.profile.ui

import androidx.compose.ui.graphics.painter.Painter

// ── UI State ──────────────────────────────────────────────────────────────────

data class EditProfileState(
    val name: String  = "",
    val phone: String = "",
    val email: String = "",
    // URI string of the newly picked image (null = no change / no image yet)
    val imageUri: String? = null,
    // Painter resolved from imageUri for preview — null uses the placeholder icon
    val imagePainter: Painter? = null,
    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
    val nameError: String? = null,
)

// ── User intents ──────────────────────────────────────────────────────────────

sealed interface EditProfileAction {
    data object Load : EditProfileAction
    data class NameChanged(val value: String) : EditProfileAction
    data class PhoneChanged(val value: String) : EditProfileAction
    data class EmailChanged(val value: String) : EditProfileAction
    /** Called by the platform-specific gallery picker with the chosen URI string */
    data class ImagePicked(val uriString: String, val painter: Painter) : EditProfileAction
    data object SaveClicked : EditProfileAction
    data object BackClicked : EditProfileAction
}

// ── One-shot side effects ─────────────────────────────────────────────────────

sealed interface EditProfileEvent {
    data object NavigateBack : EditProfileEvent
    data object SavedSuccessfully : EditProfileEvent
}
