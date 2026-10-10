package com.daragly.daraglyhair.designsystem.components.bars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakBottomNavBar + HallaakBottomNavItem
//
//  72dp bar with slide-in/out animation.
//  Selected items: primary-blue icon + gold
//  circular indicator pill.
//  Unselected: muted gray.
//
//  Mirrors DelivaryUserNavigationBar / Item.
// ─────────────────────────────────────────────

// ── Item state contract ───────────────────────

interface HallaakNavItemState {
    val labelEn: String
    val labelAr: String
    val icon: ImageVector
}

// ── Single nav item ───────────────────────────

@Composable
fun RowScope.HallaakBottomNavItem(
    item: HallaakNavItemState,
    isSelected: Boolean,
    label: String = item.labelAr,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    colors: HallaakNavItemColors = HallaakNavItemDefaults.colors(),
    textStyles: HallaakNavItemTextStyles = HallaakNavItemDefaults.textStyles(),
    onClick: (HallaakNavItemState) -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(HallaakNavItemDefaults.minHeight),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally,
    ) {
        // Icon container with indicator pill
        Box(
            modifier = Modifier
                .size(HallaakNavItemDefaults.containerSize)
                .clip(HallaakNavItemDefaults.iconShape)
                .selectable(
                    selected          = isSelected,
                    onClick           = { onClick(item) },
                    role              = Role.Tab,
                    interactionSource = interactionSource,
                    indication        = ripple(color = colors.iconColor(isSelected)),
                )
                .background(color = colors.indicatorColor(isSelected)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = item.icon,
                contentDescription = null,
                tint               = colors.iconColor(isSelected),
                modifier           = Modifier.size(HallaakNavItemDefaults.iconSize),
            )
        }

        Text(
            text     = label,
            style    = textStyles.labelStyle(isSelected),
            color    = colors.labelColor(isSelected),
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
        )
    }
}

// ── Full bar ──────────────────────────────────

@Composable
fun HallaakBottomNavBar(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color = HallaakNavBarDefaults.containerColor,
    content: @Composable RowScope.() -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible  = isVisible,
        enter    = slideInVertically(initialOffsetY = { it }),
        exit     = slideOutVertically(targetOffsetY = { it }),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(HallaakTheme.dimensions.bottomNavHeight),
            color = containerColor,
            shadowElevation = 12.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HallaakTheme.dimensions.space16, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

// ── Color model ───────────────────────────────

data class HallaakNavItemColors(
    val selectedIconColor: Color,
    val unselectedIconColor: Color,
    val selectedLabelColor: Color,
    val unselectedLabelColor: Color,
    val indicatorColor: Color,
) {
    fun iconColor(isSelected: Boolean) =
        if (isSelected) selectedIconColor else unselectedIconColor

    fun labelColor(isSelected: Boolean) =
        if (isSelected) selectedLabelColor else unselectedLabelColor

    fun indicatorColor(isSelected: Boolean) =
        if (isSelected) indicatorColor else Color.Transparent
}

data class HallaakNavItemTextStyles(
    val selectedStyle: TextStyle,
    val unselectedStyle: TextStyle,
) {
    fun labelStyle(isSelected: Boolean) =
        if (isSelected) selectedStyle else unselectedStyle
}

// ── Defaults ──────────────────────────────────

object HallaakNavItemDefaults {
    val minHeight: Dp    = 60.dp
    val iconShape: Shape = CircleShape
    val iconSize: Dp     = 22.dp
    val containerSize: Dp = 32.dp

    @Composable
    fun colors(
        selectedIconColor: Color   = HallaakTheme.colors.primary,
        unselectedIconColor: Color = HallaakTheme.colors.onSurfaceVariant,
        selectedLabelColor: Color  = HallaakTheme.colors.primary,
        unselectedLabelColor: Color = HallaakTheme.colors.onSurfaceVariant,
        indicatorColor: Color      = HallaakTheme.colors.gold.copy(alpha = 0.25f),
    ) = HallaakNavItemColors(
        selectedIconColor    = selectedIconColor,
        unselectedIconColor  = unselectedIconColor,
        selectedLabelColor   = selectedLabelColor,
        unselectedLabelColor = unselectedLabelColor,
        indicatorColor       = indicatorColor,
    )

    @Composable
    fun textStyles(
        selectedStyle: TextStyle   = HallaakTheme.typography.label.medium.copy(fontWeight = FontWeight.Bold),
        unselectedStyle: TextStyle = HallaakTheme.typography.label.medium,
    ) = HallaakNavItemTextStyles(
        selectedStyle   = selectedStyle,
        unselectedStyle = unselectedStyle,
    )
}

object HallaakNavBarDefaults {
    val containerColor: Color
        @Composable get() = HallaakTheme.colors.background.surfaceHigh
}
