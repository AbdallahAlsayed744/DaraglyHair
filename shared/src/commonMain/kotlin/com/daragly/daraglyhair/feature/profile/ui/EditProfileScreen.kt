package com.daragly.daraglyhair.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.components.textfield.HallaakTextField
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.settings.ui.UserAvatar
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.back
import daraglyhair.shared.generated.resources.cd_pick_image
import daraglyhair.shared.generated.resources.edit_profile_save
import daraglyhair.shared.generated.resources.edit_profile_title
import daraglyhair.shared.generated.resources.field_email
import daraglyhair.shared.generated.resources.field_full_name
import daraglyhair.shared.generated.resources.field_phone
import daraglyhair.shared.generated.resources.user_fallback
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = koinViewModel(),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Resolve stored image URI → Painter so the avatar shows the saved image immediately
    val storedPainter = rememberUriPainter(state.imageUri)
    val displayPainter = state.imagePainter ?: storedPainter

    // Launch image picker — called when the user taps the camera badge
    val launchImagePicker = rememberImagePickerLauncher { uriString, painter ->
        viewModel.onAction(EditProfileAction.ImagePicked(uriString, painter))
    }

    // ── Side effects ──────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                EditProfileEvent.NavigateBack      -> navigator.navigateUp()
                EditProfileEvent.SavedSuccessfully -> navigator.navigateUp()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        // ── Hero header (replaces HallaakTopBar) ─────────────────────────
        EditProfileHeroHeader(
            state            = state,
            displayPainter   = displayPainter,
            onBack           = { viewModel.onAction(EditProfileAction.BackClicked) },
            onPickImage      = { launchImagePicker() },
        )

        // ── Form fields ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(HallaakTheme.dimensions.space16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
        ) {
            Spacer(Modifier.height(HallaakTheme.dimensions.space8))

            // ── Name field ────────────────────────────────────────────────
            HallaakTextField(
                value         = state.name,
                onValueChange = { viewModel.onAction(EditProfileAction.NameChanged(it)) },
                placeholder   = stringResource(Res.string.field_full_name),
                leadingIcon   = Icons.Outlined.Person,
                isError       = state.nameError != null,
                supportingText = state.nameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction    = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            // ── Phone field ───────────────────────────────────────────────
            HallaakTextField(
                value         = state.phone,
                onValueChange = { viewModel.onAction(EditProfileAction.PhoneChanged(it)) },
                placeholder   = stringResource(Res.string.field_phone),
                leadingIcon   = Icons.Outlined.Phone,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction    = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            // ── Email field ───────────────────────────────────────────────
            HallaakTextField(
                value         = state.email,
                onValueChange = { viewModel.onAction(EditProfileAction.EmailChanged(it)) },
                placeholder   = stringResource(Res.string.field_email),
                leadingIcon   = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(HallaakTheme.dimensions.space8))

            // ── Save button ───────────────────────────────────────────────
            HallaakButtonPrimary(
                label     = stringResource(Res.string.edit_profile_save),
                onClick   = { viewModel.onAction(EditProfileAction.SaveClicked) },
                isEnabled = !state.isSaving,
                modifier  = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Hero header for Edit Profile
//  Matches the SettingsHeroHeader pattern: primary-blue block, status-bar inset,
//  back arrow on the start side, title + subtitle in the centre, avatar with
//  camera badge on the end side.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditProfileHeroHeader(
    state: EditProfileState,
    displayPainter: androidx.compose.ui.graphics.painter.Painter?,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.primary)
            .padding(
                top    = 48.dp,
                start  = HallaakTheme.dimensions.space8,
                end    = HallaakTheme.dimensions.space20,
                bottom = HallaakTheme.dimensions.space24,
            ),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
        ) {
            // ── Back button ───────────────────────────────────────────────
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.back),
                    tint               = HallaakTheme.colors.background.canvas,
                )
            }

            // ── Title + current name ──────────────────────────────────────
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
            ) {
                Text(
                    text  = stringResource(Res.string.edit_profile_title),
                    style = HallaakTheme.typography.headline.large,
                    color = HallaakTheme.colors.background.canvas,
                )
                val displayName = state.name.trim()
                    .ifBlank { stringResource(Res.string.user_fallback) }
                Text(
                    text     = displayName,
                    style    = HallaakTheme.typography.body.medium,
                    color    = HallaakTheme.colors.background.canvas.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (state.email.isNotBlank()) {
                    Text(
                        text     = state.email,
                        style    = HallaakTheme.typography.label.large,
                        color    = HallaakTheme.colors.background.canvas.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // ── Avatar with camera badge ──────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                UserAvatar(
                    imagePainter = displayPainter,
                    isLoading    = state.isLoading,
                    size         = HallaakTheme.dimensions.avatarMedium.value.toInt(),
                    borderColor  = HallaakTheme.colors.background.canvas.copy(alpha = 0.4f),
                )

                // Camera badge
                IconButton(
                    onClick  = onPickImage,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(HallaakTheme.colors.background.canvas),
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.AddAPhoto,
                        contentDescription = stringResource(Res.string.cd_pick_image),
                        tint               = HallaakTheme.colors.primary,
                        modifier           = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}
