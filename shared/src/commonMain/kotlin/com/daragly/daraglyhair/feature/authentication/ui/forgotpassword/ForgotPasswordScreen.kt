package com.daragly.daraglyhair.feature.authentication.ui.forgotpassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daragly.daraglyhair.common.ui.extension.ObserveAsEvents
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.components.dialogs.HallaakLoadingDialog
import com.daragly.daraglyhair.designsystem.components.textfield.HallaakTextField
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.back
import daraglyhair.shared.generated.resources.back_to_sign_in
import daraglyhair.shared.generated.resources.field_email
import daraglyhair.shared.generated.resources.forgot_password_body
import daraglyhair.shared.generated.resources.forgot_password_heading
import daraglyhair.shared.generated.resources.forgot_password_sent_body
import daraglyhair.shared.generated.resources.forgot_password_sent_title
import daraglyhair.shared.generated.resources.reset_password
import daraglyhair.shared.generated.resources.send_reset_link
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) HallaakLoadingDialog()

    ForgotPasswordContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun ForgotPasswordContent(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
) {
    val primary   = HallaakTheme.colors.primary
    val dark      = Color(0xFF0A1520)
    val bg        = HallaakTheme.colors.background.canvas

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
                        imageVector        = Icons.Outlined.Email,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(32.dp),
                    )
                }
                Text(
                    text  = stringResource(Res.string.reset_password),
                    style = HallaakTheme.typography.headline.large,
                    color = Color.White,
                )
            }
        }

        // ── Body ─────────────────────────────────────────────────────────────
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
                onClick  = { onAction(ForgotPasswordAction.BackClicked) },
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

            // ── Success state ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.isSuccess,
                enter   = fadeIn() + slideInVertically(),
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint               = HallaakTheme.colors.status.success,
                        modifier           = Modifier.size(72.dp),
                    )
                    Text(
                        text  = stringResource(Res.string.forgot_password_sent_title),
                        style = HallaakTheme.typography.headline.medium,
                        color = HallaakTheme.colors.onSurface,
                    )
                    Text(
                        text      = stringResource(Res.string.forgot_password_sent_body),
                        style     = HallaakTheme.typography.body.small,
                        color     = HallaakTheme.colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    HallaakButtonPrimary(
                        modifier = Modifier.fillMaxWidth(),
                        label    = stringResource(Res.string.back_to_sign_in),
                        onClick  = { onAction(ForgotPasswordAction.BackClicked) },
                    )
                }
            }

            // ── Input state ───────────────────────────────────────────────────
            AnimatedVisibility(visible = !state.isSuccess) {
                Column {
                    Text(
                        text  = stringResource(Res.string.forgot_password_heading),
                        style = HallaakTheme.typography.headline.small,
                        color = HallaakTheme.colors.onSurface,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = stringResource(Res.string.forgot_password_body),
                        style = HallaakTheme.typography.body.small,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(24.dp))

                    HallaakTextField(
                        modifier       = Modifier.fillMaxWidth(),
                        value          = state.email,
                        onValueChange  = { onAction(ForgotPasswordAction.EmailChanged(it)) },
                        placeholder    = stringResource(Res.string.field_email),
                        leadingIcon    = Icons.Outlined.Email,
                        supportingText = state.emailError,
                        isError        = state.emailError != null,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                        ),
                    )

                    Spacer(Modifier.height(28.dp))

                    HallaakButtonPrimary(
                        modifier  = Modifier.fillMaxWidth(),
                        label     = stringResource(Res.string.send_reset_link),
                        isEnabled = state.email.isNotBlank(),
                        onClick   = { onAction(ForgotPasswordAction.SendClicked) },
                    )
                }
            }
        }
    }
}
