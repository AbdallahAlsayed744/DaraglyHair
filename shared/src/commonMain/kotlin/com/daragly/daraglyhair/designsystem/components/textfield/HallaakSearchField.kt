package com.daragly.daraglyhair.designsystem.components.textfield

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.search_field_placeholder
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────
//  HallaakSearchField
//
//  Pill-shaped search input used on the
//  discovery / explore screen.
//  Search icon leading, gray fill background.
// ─────────────────────────────────────────────

@Composable
fun HallaakSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(Res.string.search_field_placeholder),
    onSearch: () -> Unit = {},
) {
    HallaakTextField(
        modifier      = modifier,
        value         = value,
        onValueChange = onValueChange,
        placeholder   = placeholder,
        leadingIcon   = Icons.Outlined.Search,
        leadingIconColor = HallaakTheme.colors.onSurfaceVariant,
        singleLine    = true,
        shape         = RoundedCornerShape(50.dp),  // pill shape
        colors        = HallaakSearchFieldDefaults.colors,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction    = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
    )
}

object HallaakSearchFieldDefaults {
    val colors: HallaakTextFieldColors
        @Composable get() = HallaakTextFieldDefaults.colors(
            focusedBorderColor    = HallaakTheme.colors.primary,
            unfocusedBorderColor  = HallaakTheme.colors.stroke,
            focusedContainerColor = HallaakTheme.colors.background.surface,
            unfocusedContainerColor = HallaakTheme.colors.background.surface,
            cursorColor           = HallaakTheme.colors.gold,
            contentColor          = HallaakTheme.colors.onSurface,
        )
}
