package com.daragly.daraglyhair.designsystem.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakButton – base button primitive
//
//  All other button variants (Primary, Secondary,
//  Ghost, Gold) are built on top of this.
// ─────────────────────────────────────────────

@Composable
fun HallaakButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: HallaakButtonColors = HallaakButtonDefaults.color(),
    isEnabled: Boolean = true,
    contentPadding: PaddingValues = HallaakButtonDefaults.contentPadding,
    shape: Shape = HallaakTheme.shapes.button,
    labelStyle: TextStyle = HallaakButtonDefaults.labelStyle,
) {
    // Animate container color on enable/disable transitions
    val containerColor by animateColorAsState(
        targetValue = if (isEnabled) colors.containerColor else colors.disabledContainerColor,
        animationSpec = tween(durationMillis = 200),
        label = "button-container-color",
    )
    val contentColor by animateColorAsState(
        targetValue = if (isEnabled) colors.contentColor else colors.disabledContentColor,
        animationSpec = tween(durationMillis = 200),
        label = "button-content-color",
    )

    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(HallaakTheme.dimensions.buttonHeight)
            .clip(shape),
        shape = shape,
        colors = ButtonColors(
            containerColor         = containerColor,
            contentColor           = contentColor,
            disabledContainerColor = colors.disabledContainerColor,
            disabledContentColor   = colors.disabledContentColor,
        ),
        border = BorderStroke(
            width = HallaakButtonDefaults.borderWidth,
            color = if (isEnabled) colors.borderColor else Color.Transparent,
        ),
        contentPadding = contentPadding,
        enabled = isEnabled,
        onClick = onClick,
    ) {
        Text(text = label, style = labelStyle)
    }
}

// ── Color model ───────────────────────────────

data class HallaakButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val borderColor: Color,
)

// ── Defaults ──────────────────────────────────

object HallaakButtonDefaults {
    val borderWidth = 1.5.dp
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)

    val labelStyle: TextStyle
        @Composable get() = HallaakTheme.typography.headline.small

    fun color(
        containerColor: Color = Color.Unspecified,
        contentColor: Color = Color.Unspecified,
        disabledContainerColor: Color = Color.Unspecified,
        disabledContentColor: Color = Color.Unspecified,
        borderColor: Color = Color.Transparent,
    ) = HallaakButtonColors(
        containerColor         = containerColor,
        contentColor           = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor   = disabledContentColor,
        borderColor            = borderColor,
    )
}
