package com.daragly.daraglyhair.feature.profile.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.authentication.domain.interactors.FetchAndSaveUserProfileUseCase
import com.daragly.daraglyhair.feature.user.domain.model.User
import com.daragly.daraglyhair.feature.user.domain.usecase.GetUserUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.SaveUserUseCase
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val fetchAndSaveUserProfileUseCase: FetchAndSaveUserProfileUseCase,
) : BaseViewModel<EditProfileState, EditProfileAction, EditProfileEvent>(EditProfileState()) {

    init { onAction(EditProfileAction.Load) }

    override fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.Load         -> loadUser()
            is EditProfileAction.NameChanged  -> updateState { copy(name = action.value, nameError = null) }
            is EditProfileAction.PhoneChanged -> updateState { copy(phone = action.value) }
            is EditProfileAction.EmailChanged -> updateState { copy(email = action.value) }
            is EditProfileAction.ImagePicked  -> updateState {
                copy(imageUri = action.uriString, imagePainter = action.painter)
            }
            is EditProfileAction.SaveClicked  -> save()
            is EditProfileAction.BackClicked  -> emitEvent(EditProfileEvent.NavigateBack)
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            // 1. Show local data immediately if it has a name
            val localResult = getUserUseCase.invoke(Unit)
            val localUser   = (localResult as? Resource.Success)?.model

            if (localUser != null && localUser.name.isNotBlank()) {
                populateState(localUser)
                updateState { copy(isLoading = false) }
            } else {
                // 2. Local is empty — fetch from API, persist, then populate
                runCatching { fetchAndSaveUserProfileUseCase.invoke(Unit) }
                when (val fresh = getUserUseCase.invoke(Unit)) {
                    is Resource.Success -> populateState(fresh.model)
                    else                -> Unit
                }
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun populateState(user: User) {
        updateState {
            copy(
                name     = user.name,
                phone    = user.phone,
                email    = user.email,
                imageUri = user.image,
            )
        }
    }

    private fun save() {
        val current = state.value
        if (current.name.isBlank()) {
            updateState { copy(nameError = "الاسم مطلوب") }
            return
        }

        updateState { copy(isSaving = true, nameError = null) }
        viewModelScope.launch {
            try {
                // Preserve the existing id
                val existingId = when (val r = getUserUseCase.invoke(Unit)) {
                    is Resource.Success -> r.model.id
                    else                -> ""
                }

                val userToSave = User(
                    id    = existingId,
                    name  = current.name.trim(),
                    phone = current.phone.trim(),
                    email = current.email.trim(),
                    image = current.imageUri,
                )

                saveUserUseCase.invoke(userToSave)

                updateState { copy(isSaving = false) }
                emitEvent(EditProfileEvent.SavedSuccessfully)
            } catch (e: Exception) {
                updateState { copy(isSaving = false) }
                onGlobalError(e.message ?: "فشل الحفظ")
            }
        }
    }
}
