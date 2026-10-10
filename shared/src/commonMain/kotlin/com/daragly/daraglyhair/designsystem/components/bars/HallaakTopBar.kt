package com.daragly.daraglyhair.designsystem.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakTopBar
//
//  56dp height, primary-blue background (default).
//  Start icon (back/menu), center title or custom
//  slot, optional end icon.
//
//  Mirrors DelivaryUserTopBar structure exactly.
// ─────────────────────────────────────────────

@Composable
fun HallaakTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    onStartIconClicked: () -> Unit = {},
    onEndIconClicked: () -> Unit = {},
    colors: HallaakTopBarColors = HallaakTopBarDefaults.colors(),
    centerSlot: (@Composable () -> Unit)? = null,
    rowContent: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HallaakTopBarDefaults.height)
            .background(color = colors.containerColor)
            .padding(
                start = HallaakTopBarDefaults.horizontalPadding,
                end = HallaakTopBarDefaults.horizontalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Start icon / placeholder spacer
        if (startIcon != null) {
            IconButton(
                modifier = Modifier.size(HallaakTopBarDefaults.iconSize),
                onClick = onStartIconClicked,
            ) {
                Icon(
                    imageVector = startIcon,
                    tint = colors.contentColor,
                    contentDescription = null,
                )
            }
        } else {
            Spacer(modifier = Modifier.size(HallaakTopBarDefaults.iconSize))
        }

        // Center slot
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            when {
                rowContent != null -> Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = HallaakTheme.dimensions.space8),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    content = rowContent,
                )

                centerSlot != null -> centerSlot()
                title != null -> Text(
                    text = title,
                    style = HallaakTheme.typography.headline.small,
                    color = colors.contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                else -> {
                    // Logo placeholder – replace with actual image resource
                    Text(
                        text = "حلاقك",
                        style = HallaakTheme.typography.headline.medium,
                        color = colors.contentColor,
                    )
                }
            }
        }

        // End icon / placeholder spacer
        if (endIcon != null) {
            IconButton(
                modifier = Modifier.size(HallaakTopBarDefaults.iconSize),
                onClick = onEndIconClicked,
            ) {
                Icon(
                    imageVector = endIcon,
                    tint = colors.contentColor,
                    contentDescription = null,
                )
            }
        } else {
            Spacer(modifier = Modifier.size(HallaakTopBarDefaults.iconSize))
        }
    }
}

// ── Color model ───────────────────────────────

data class HallaakTopBarColors(
    val containerColor: Color,
    val contentColor: Color,
)

// ── Defaults ──────────────────────────────────

object HallaakTopBarDefaults {
    val height
        @Composable get() = HallaakTheme.dimensions.topBarHeight
    val horizontalPadding
        @Composable get() = HallaakTheme.dimensions.space16
    val iconSize
        @Composable get() = HallaakTheme.dimensions.iconSize

    @Composable
    fun colors(
        containerColor: Color = HallaakTheme.colors.primary,
        contentColor: Color = HallaakTheme.colors.background.surfaceHigh,
    ) = HallaakTopBarColors(containerColor = containerColor, contentColor = contentColor)

    /** Transparent bar for screens with hero imagery */
    @Composable
    fun transparentColors(
        contentColor: Color = HallaakTheme.colors.background.surfaceHigh,
    ) = HallaakTopBarColors(containerColor = Color.Transparent, contentColor = contentColor)
}
