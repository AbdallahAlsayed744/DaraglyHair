package com.daragly.daraglyhair.designsystem.components.textfield

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakTextField – base text input primitive
//
//  Styled as an OutlinedTextField with:
//    • 12dp rounded corners (slightly softer)
//    • Primary-blue focused border
//    • Warm cream/surfaceHigh fill
//    • Charcoal text, gray placeholder
//    • Gold cursor
//    • RTL-aware icons
// ─────────────────────────────────────────────

@Composable
fun HallaakTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = HallaakTextFieldDefaults.textStyle,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    leadingIconColor: Color? = null,
    trailingIconColor: Color? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = HallaakTextFieldDefaults.keyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    shape: Shape = HallaakTheme.shapes.input,
    colors: HallaakTextFieldColors = HallaakTextFieldDefaults.colors(),
    textAlign: TextAlign = TextAlign.Start,
    onTrailingIconClicked: () -> Unit = {},
    onLeadingIconClicked: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier,
        enabled = enabled,
        value = value,
        readOnly = readOnly,
        textStyle = textStyle.copy(
            textAlign = textAlign,
            color = colors.contentColor,
        ),
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    modifier = Modifier.size(HallaakTextFieldDefaults.iconSize),
                    imageVector = leadingIcon,
                    tint = leadingIconColor ?: colors.contentColor,
                    contentDescription = null,
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                Icon(
                    modifier = Modifier.size(HallaakTextFieldDefaults.iconSize),
                    imageVector = trailingIcon,
                    tint = trailingIconColor ?: colors.contentColor,
                    contentDescription = null,
                )
            }
        },
        supportingText = supportingText?.let {
            {
                Text(
                    text = it,
                    color = colors.errorColor,
                    style = HallaakTextFieldDefaults.supportingTextStyle,
                    minLines = 1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = textAlign,
                )
            }
        },
        onValueChange = onValueChange,
        placeholder = placeholder?.let {
            {
                Text(
                    text = it,
                    style = textStyle,
                    color = colors.placeholderColor,
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor          = colors.contentColor,
            unfocusedTextColor        = colors.contentColor,
            disabledTextColor         = colors.contentColor.copy(alpha = 0.5f),
            focusedBorderColor        = colors.focusedBorderColor,
            unfocusedBorderColor      = colors.unfocusedBorderColor,
            disabledBorderColor       = colors.disabledBorderColor,
            focusedContainerColor     = colors.focusedContainerColor,
            unfocusedContainerColor   = colors.unfocusedContainerColor,
            disabledContainerColor    = colors.disabledContainerColor,
            errorContainerColor       = colors.errorContainerColor,
            cursorColor               = colors.cursorColor,
        ),
        shape = shape,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        isError = isError,
    )
}

// ── Color model ───────────────────────────────

data class HallaakTextFieldColors(
    val focusedBorderColor: Color,
    val unfocusedBorderColor: Color,
    val disabledBorderColor: Color,
    val focusedContainerColor: Color,
    val unfocusedContainerColor: Color,
    val disabledContainerColor: Color,
    val errorContainerColor: Color,
    val contentColor: Color,
    val placeholderColor: Color,
    val cursorColor: Color,
    val errorColor: Color,
)

// ── Defaults ──────────────────────────────────

object HallaakTextFieldDefaults {
    val iconSize = 20.dp

    val textStyle: TextStyle
        @Composable get() = HallaakTheme.typography.body.medium

    val supportingTextStyle: TextStyle
        @Composable get() = HallaakTheme.typography.label.small

    val keyboardOptions: KeyboardOptions
        get() = KeyboardOptions(imeAction = ImeAction.Next)

    @Composable
    fun colors(
        focusedBorderColor: Color    = HallaakTheme.colors.primary,
        unfocusedBorderColor: Color  = HallaakTheme.colors.stroke,
        disabledBorderColor: Color   = HallaakTheme.colors.background.disabled,
        focusedContainerColor: Color = HallaakTheme.colors.background.surfaceHigh,
        unfocusedContainerColor: Color = HallaakTheme.colors.background.surfaceHigh,
        disabledContainerColor: Color  = HallaakTheme.colors.background.disabled,
        errorContainerColor: Color   = HallaakTheme.colors.background.surfaceHigh,
        contentColor: Color          = HallaakTheme.colors.onSurface,
        placeholderColor: Color      = HallaakTheme.colors.background.hint,
        cursorColor: Color           = HallaakTheme.colors.gold,
        errorColor: Color            = HallaakTheme.colors.status.error,
    ) = HallaakTextFieldColors(
        focusedBorderColor     = focusedBorderColor,
        unfocusedBorderColor   = unfocusedBorderColor,
        disabledBorderColor    = disabledBorderColor,
        focusedContainerColor  = focusedContainerColor,
        unfocusedContainerColor = unfocusedContainerColor,
        disabledContainerColor = disabledContainerColor,
        errorContainerColor    = errorContainerColor,
        contentColor           = contentColor,
        placeholderColor       = placeholderColor,
        cursorColor            = cursorColor,
        errorColor             = errorColor,
    )
}
