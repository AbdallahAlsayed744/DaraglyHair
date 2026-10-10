package com.daragly.daraglyhair.feature.authentication.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daragly.daraglyhair.common.ui.extension.ObserveAsEvents
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.components.dialogs.HallaakLoadingDialog
import com.daragly.daraglyhair.designsystem.components.textfield.HallaakTextField
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.designsystem.util.clickableNoRipple
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) HallaakLoadingDialog()

    RegisterContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun RegisterContent(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
) {
    val primary   = HallaakTheme.colors.primary
    val dark      = Color(0xFF0A1520)
    val bg        = HallaakTheme.colors.background.canvas
    val secondary = HallaakTheme.colors.onSurface

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Brush.verticalGradient(listOf(dark, primary))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text  = "Create Account",
                style = HallaakTheme.typography.headline.large,
                color = Color.White,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text  = "Join Halla'ak today",
                style = HallaakTheme.typography.body.small,
                color = Color.White.copy(alpha = 0.72f),
            )
        }

        // ── Scrollable form ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(bg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Text(
                text  = "Your Details",
                style = HallaakTheme.typography.headline.small,
                color = secondary,
            )

            Spacer(Modifier.height(16.dp))

            // First & Last name row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HallaakTextField(
                    modifier       = Modifier.weight(1f),
                    value          = state.firstName,
                    onValueChange  = { onAction(RegisterAction.FirstNameChanged(it)) },
                    placeholder    = "First name",
                    leadingIcon    = Icons.Outlined.Badge,
                    supportingText = state.firstNameError,
                    isError        = state.firstNameError != null,
                )
                HallaakTextField(
                    modifier       = Modifier.weight(1f),
                    value          = state.lastName,
                    onValueChange  = { onAction(RegisterAction.LastNameChanged(it)) },
                    placeholder    = "Last name",
                    leadingIcon    = Icons.Outlined.Badge,
                    supportingText = state.lastNameError,
                    isError        = state.lastNameError != null,
                )
            }

            Spacer(Modifier.height(4.dp))

            // Username
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.userName,
                onValueChange  = { onAction(RegisterAction.UserNameChanged(it)) },
                placeholder    = "Username",
                leadingIcon    = Icons.Outlined.AccountCircle,
                supportingText = state.userNameError,
                isError        = state.userNameError != null,
            )

            // Email
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.email,
                onValueChange  = { onAction(RegisterAction.EmailChanged(it)) },
                placeholder    = "Email address",
                leadingIcon    = Icons.Outlined.Email,
                supportingText = state.emailError,
                isError        = state.emailError != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Next,
                ),
            )

            // Phone
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.phone,
                onValueChange  = { onAction(RegisterAction.PhoneChanged(it)) },
                placeholder    = "Phone number",
                leadingIcon    = Icons.Outlined.Phone,
                supportingText = state.phoneError,
                isError        = state.phoneError != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction    = ImeAction.Next,
                ),
            )

            // Password
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.password,
                onValueChange  = { onAction(RegisterAction.PasswordChanged(it)) },
                placeholder    = "Password",
                leadingIcon    = Icons.Outlined.Lock,
                trailingIcon   = if (state.isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                onTrailingIconClicked = { onAction(RegisterAction.TogglePasswordVisibility) },
                visualTransformation  = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                supportingText = state.passwordError,
                isError        = state.passwordError != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Next,
                ),
            )

            // Confirm password
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.confirmPassword,
                onValueChange  = { onAction(RegisterAction.ConfirmPasswordChanged(it)) },
                placeholder    = "Confirm password",
                leadingIcon    = Icons.Outlined.Lock,
                trailingIcon   = if (state.isConfirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                onTrailingIconClicked = { onAction(RegisterAction.ToggleConfirmPasswordVisibility) },
                visualTransformation  = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                supportingText = state.confirmPasswordError,
                isError        = state.confirmPasswordError != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done,
                ),
            )

            Spacer(Modifier.height(24.dp))

            HallaakButtonPrimary(
                modifier  = Modifier.fillMaxWidth(),
                label     = "Create Account",
                isEnabled = state.firstName.isNotBlank() && state.email.isNotBlank() && state.password.isNotBlank(),
                onClick   = { onAction(RegisterAction.RegisterClicked) },
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text  = buildAnnotatedString {
                        withStyle(SpanStyle(color = HallaakTheme.colors.onSurfaceVariant)) {
                            append("Already have an account? ")
                        }
                        withStyle(SpanStyle(color = primary)) { append("Sign in") }
                    },
                    style     = HallaakTheme.typography.body.small,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.clickableNoRipple { onAction(RegisterAction.LoginClicked) },
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
