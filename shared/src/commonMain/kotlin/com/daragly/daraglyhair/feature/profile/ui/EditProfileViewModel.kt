package com.daragly.daraglyhair.feature.profile.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.authentication.domain.interactors.FetchAndSaveUserProfileUseCase
import com.daragly.daraglyhair.feature.user.domain.model.ProfileImage
import com.daragly.daraglyhair.feature.user.domain.model.ProfileUpdate
import com.daragly.daraglyhair.feature.user.domain.model.User
import com.daragly.daraglyhair.feature.user.domain.usecase.GetUserUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.SaveUserUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.UpdateUserProfileUseCase
import kotlinx.coroutines.launch

private const val NAME_MAX_LENGTH = 75

class EditProfileViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val fetchAndSaveUserProfileUseCase: FetchAndSaveUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : BaseViewModel<EditProfileState, EditProfileAction, EditProfileEvent>(EditProfileState()) {

    private var userId: String = ""

    init { onAction(EditProfileAction.Load) }

    override fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.Load -> loadUser()
            is EditProfileAction.FirstNameChanged -> updateState {
                copy(firstName = action.value.take(NAME_MAX_LENGTH), firstNameError = null)
            }
            is EditProfileAction.LastNameChanged -> updateState {
                copy(lastName = action.value.take(NAME_MAX_LENGTH), lastNameError = null)
            }
            is EditProfileAction.PhoneChanged -> updateState { copy(phone = action.value) }
            is EditProfileAction.EmailChanged -> updateState {
                copy(email = action.value, emailError = null)
            }
            is EditProfileAction.ImagePicked -> updateState {
                copy(imageUri = action.uriString, imagePainter = action.painter, pickedImage = action.image)
            }
            is EditProfileAction.SaveClicked -> save()
            is EditProfileAction.BackClicked -> emitEvent(EditProfileEvent.NavigateBack)
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            // 1. Show local data immediately if it has a name
            val localUser = (getUserUseCase(Unit) as? Resource.Success)?.model

            if (localUser != null && localUser.name.isNotBlank()) {
                userId = localUser.id
                populateState(localUser)
                updateState { copy(isLoading = false) }
            } else {
                // 2. Local is empty — fetch from API, persist, then populate
                fetchAndSaveUserProfileUseCase(Unit)
                when (val fresh = getUserUseCase(Unit)) {
                    is Resource.Success -> {
                        userId = fresh.model.id
                        populateState(fresh.model)
                    }
                    else -> Unit
                }
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun populateState(user: User) {
        // Older cache entries may not carry the split names — derive them from the full name.
        val cachedFirst = user.firstName.ifBlank { user.name.substringBefore(' ').trim() }
        val cachedLast = user.lastName.ifBlank {
            user.name.substringAfter(' ', missingDelimiterValue = "").trim()
        }
        updateState {
            copy(
                firstName = cachedFirst,
                lastName = cachedLast,
                phone = user.phone,
                email = user.email,
                imageUri = user.image,
            )
        }
    }

    private fun save() {
        val current = state.value
        if (!validate(current)) return

        updateState { copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            if (userId.isBlank()) {
                // Cached user had no id — refresh the profile so the update targets the right user.
                fetchAndSaveUserProfileUseCase(Unit)
                userId = (getUserUseCase(Unit) as? Resource.Success)?.model?.id.orEmpty()
            }
            if (userId.isBlank()) {
                updateState { copy(isSaving = false, saveError = ERROR_SAVE_FAILED) }
                return@launch
            }

            val update = ProfileUpdate(
                userId = userId,
                firstName = current.firstName.trim(),
                lastName = current.lastName.trim(),
                email = current.email.trim(),
                phoneNumber = current.phone.trim().takeIf { it.isNotEmpty() },
                image = current.pickedImage,
            )

            when (updateUserProfileUseCase(update)) {
                is Resource.Success -> {
                    saveUserUseCase(
                        User(
                            id = userId,
                            name = "${update.firstName} ${update.lastName}".trim(),
                            firstName = update.firstName,
                            lastName = update.lastName,
                            email = update.email,
                            phone = update.phoneNumber.orEmpty(),
                            image = current.imageUri,
                        ),
                    )
                    updateState { copy(isSaving = false, pickedImage = null) }
                    emitEvent(EditProfileEvent.SavedSuccessfully)
                }
                is Resource.Failure -> {
                    updateState { copy(isSaving = false, saveError = ERROR_SAVE_FAILED) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun validate(current: EditProfileState): Boolean {
        val firstNameError = when {
            current.firstName.isBlank() -> ERROR_FIRST_NAME_REQUIRED
            else -> null
        }
        val lastNameError = when {
            current.lastName.isBlank() -> ERROR_LAST_NAME_REQUIRED
            else -> null
        }
        val emailError = when {
            current.email.isBlank() -> ERROR_EMAIL_REQUIRED
            !current.email.contains("@") -> ERROR_EMAIL_INVALID
            else -> null
        }
        updateState {
            copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError,
            )
        }
        return firstNameError == null && lastNameError == null && emailError == null
    }

    companion object {
        // Sentinels resolved to localized text by the screen
        const val ERROR_FIRST_NAME_REQUIRED = "first_name_required"
        const val ERROR_LAST_NAME_REQUIRED = "last_name_required"
        const val ERROR_EMAIL_REQUIRED = "email_required"
        const val ERROR_EMAIL_INVALID = "email_invalid"
        const val ERROR_SAVE_FAILED = "save_failed"
    }
}
