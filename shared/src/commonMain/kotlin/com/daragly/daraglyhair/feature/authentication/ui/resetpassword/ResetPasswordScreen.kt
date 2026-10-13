package com.daragly.daraglyhair.feature.authentication.ui.resetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daragly.daraglyhair.common.ui.extension.ObserveAsEvents
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.components.dialogs.HallaakLoadingDialog
import com.daragly.daraglyhair.designsystem.components.textfield.HallaakTextField
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.back
import daraglyhair.shared.generated.resources.field_new_password
import daraglyhair.shared.generated.resources.field_reset_token
import daraglyhair.shared.generated.resources.new_password
import daraglyhair.shared.generated.resources.new_password_body
import daraglyhair.shared.generated.resources.new_password_section
import daraglyhair.shared.generated.resources.reset_password
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ResetPasswordScreen(
    email: String,
    viewModel: ResetPasswordViewModel = koinViewModel(parameters = { parametersOf(email) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) HallaakLoadingDialog()

    ResetPasswordContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun ResetPasswordContent(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
) {
    val primary = HallaakTheme.colors.primary
    val dark    = Color(0xFF0A1520)
    val bg      = HallaakTheme.colors.background.canvas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(listOf(dark, primary))),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Key,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(32.dp),
                    )
                }
                Text(
                    text  = stringResource(Res.string.new_password),
                    style = HallaakTheme.typography.headline.large,
                    color = Color.White,
                )
            }
        }

        // ── Form ─────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(bg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            IconButton(
                onClick  = { onAction(ResetPasswordAction.BackClicked) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(HallaakTheme.colors.background.surface),
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.back),
                    tint               = HallaakTheme.colors.onSurface,
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text  = stringResource(Res.string.new_password_section),
                style = HallaakTheme.typography.headline.small,
                color = HallaakTheme.colors.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text  = stringResource(Res.string.new_password_body),
                style = HallaakTheme.typography.body.small,
                color = HallaakTheme.colors.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))

            // Reset token field
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.token,
                onValueChange  = { onAction(ResetPasswordAction.TokenChanged(it)) },
                placeholder    = stringResource(Res.string.field_reset_token),
                leadingIcon    = Icons.Outlined.Key,
                supportingText = state.tokenError,
                isError        = state.tokenError != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(Modifier.height(4.dp))

            // New password
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.password,
                onValueChange  = { onAction(ResetPasswordAction.PasswordChanged(it)) },
                placeholder    = stringResource(Res.string.field_new_password),
                leadingIcon    = Icons.Outlined.Lock,
                trailingIcon   = if (state.isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                onTrailingIconClicked = { onAction(ResetPasswordAction.TogglePasswordVisibility) },
                visualTransformation  = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                supportingText = state.passwordError,
                isError        = state.passwordError != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done,
                ),
            )

            Spacer(Modifier.height(28.dp))

            HallaakButtonPrimary(
                modifier  = Modifier.fillMaxWidth(),
                label     = stringResource(Res.string.reset_password),
                isEnabled = state.token.isNotBlank() && state.password.isNotBlank(),
                onClick   = { onAction(ResetPasswordAction.ConfirmClicked) },
            )
        }
    }
}
