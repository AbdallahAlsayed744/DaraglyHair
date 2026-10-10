package com.daragly.daraglyhair.designsystem.components.textfield

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakPasswordTextField
//
//  Pre-wired password field with visibility toggle.
//  Lock icon on the leading side, eye icon trailing.
//  Password keyboard, gold cursor.
// ─────────────────────────────────────────────

@Composable
fun HallaakPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "كلمة المرور",
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Done,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    HallaakTextField(
        modifier             = modifier,
        value                = value,
        onValueChange        = onValueChange,
        placeholder          = placeholder,
        leadingIcon          = Icons.Outlined.Lock,
        trailingIcon         = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
        trailingIconColor    = HallaakTheme.colors.onSurfaceVariant,
        onTrailingIconClicked = { isPasswordVisible = !isPasswordVisible },
        supportingText       = supportingText,
        keyboardOptions      = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction    = imeAction,
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None
                               else PasswordVisualTransformation(),
        colors               = HallaakPasswordTextFieldDefaults.colors,
    )
}

object HallaakPasswordTextFieldDefaults {
    val colors: HallaakTextFieldColors
        @Composable get() = HallaakTextFieldDefaults.colors(
            focusedBorderColor   = HallaakTheme.colors.primary,
            unfocusedBorderColor = HallaakTheme.colors.stroke,
            cursorColor          = HallaakTheme.colors.gold,
            errorColor           = HallaakTheme.colors.status.error,
        )
}
