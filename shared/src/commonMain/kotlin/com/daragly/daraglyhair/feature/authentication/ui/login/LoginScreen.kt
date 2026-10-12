package com.daragly.daraglyhair.feature.authentication.ui.login

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.outlined.Email
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daragly.daraglyhair.common.ui.extension.ObserveAsEvents
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.components.textfield.HallaakTextField
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.designsystem.util.clickableNoRipple
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun LoginContent(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
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
        // ── Hero header ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Brush.verticalGradient(listOf(dark, primary))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text  = "حلاقك",
                style = HallaakTheme.typography.headline.extraLarge,
                color = Color.White,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text  = "أهلاً بعودتك — سجّل الدخول للمتابعة",
                style = HallaakTheme.typography.body.small,
                color = Color.White.copy(alpha = 0.72f),
            )
        }

        // ── Form card ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(bg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            Text(
                text  = "تسجيل الدخول",
                style = HallaakTheme.typography.headline.medium,
                color = secondary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "أدخل بيانات حسابك للمتابعة",
                style = HallaakTheme.typography.body.small,
                color = HallaakTheme.colors.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))

            // Email
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.email,
                onValueChange  = { onAction(LoginAction.EmailChanged(it)) },
                placeholder    = "البريد الإلكتروني",
                leadingIcon    = Outlined.Email,
                supportingText = state.emailError,
                isError        = state.emailError != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = androidx.compose.ui.text.input.ImeAction.Next,
                ),
            )

            Spacer(Modifier.height(4.dp))

            // Password
            HallaakTextField(
                modifier       = Modifier.fillMaxWidth(),
                value          = state.password,
                onValueChange  = { onAction(LoginAction.PasswordChanged(it)) },
                placeholder    = "كلمة المرور",
                leadingIcon    = Outlined.Lock,
                trailingIcon   = if (state.isPasswordVisible) Outlined.Visibility else Outlined.VisibilityOff,
                onTrailingIconClicked = { onAction(LoginAction.TogglePasswordVisibility) },
                visualTransformation  = if (state.isPasswordVisible)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                supportingText = state.passwordError,
                isError        = state.passwordError != null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = androidx.compose.ui.text.input.ImeAction.Done,
                ),
            )

            // Forgot password
            Text(
                text     = "نسيت كلمة المرور؟",
                style    = HallaakTheme.typography.label.large,
                color    = primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
                    .clickableNoRipple { onAction(LoginAction.ForgotPasswordClicked) },
            )

            Spacer(Modifier.height(28.dp))

            HallaakButtonPrimary(
                modifier  = Modifier.fillMaxWidth(),
                label     = "تسجيل الدخول",
                isEnabled = state.email.isNotBlank() && state.password.isNotBlank(),
                onClick   = { onAction(LoginAction.LoginClicked) },
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text  = buildAnnotatedString {
                        withStyle(SpanStyle(color = HallaakTheme.colors.onSurfaceVariant)) {
                            append("ليس لديك حساب؟ ")
                        }
                        withStyle(SpanStyle(color = primary)) {
                            append("إنشاء حساب")
                        }
                    },
                    style     = HallaakTheme.typography.body.small,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.clickableNoRipple { onAction(LoginAction.RegisterClicked) },
                )
            }
        }
    }
}
