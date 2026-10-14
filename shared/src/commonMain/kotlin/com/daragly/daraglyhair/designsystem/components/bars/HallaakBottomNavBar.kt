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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.daragly.daraglyhair.common.ui.navigation.IDestination
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.ic_bookings
import daraglyhair.shared.generated.resources.ic_bookings_outlined
import daraglyhair.shared.generated.resources.ic_explore
import daraglyhair.shared.generated.resources.ic_explore_outlined
import daraglyhair.shared.generated.resources.ic_favorite
import daraglyhair.shared.generated.resources.ic_favorite_outlined
import daraglyhair.shared.generated.resources.ic_home
import daraglyhair.shared.generated.resources.ic_home_outlined
import daraglyhair.shared.generated.resources.ic_profile
import daraglyhair.shared.generated.resources.ic_profile_outlined
import daraglyhair.shared.generated.resources.nav_bookings
import daraglyhair.shared.generated.resources.nav_explore
import daraglyhair.shared.generated.resources.nav_favorites
import daraglyhair.shared.generated.resources.nav_home
import daraglyhair.shared.generated.resources.nav_profile
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────────────────────
//  HallaakBottomNavBar — Production-ready navigation bar
//
//  Mirrors DelivaryUserNavigationBar pattern:
//    • 5 equal-width destinations
//    • Filled/outlined icon variants for selected/unselected
//    • Gold indicator pill on selected
//    • Slide-in/out animation controlled by current route
//    • NavController integration with hierarchy check
//    • Bilingual labels (English + Arabic)
// ─────────────────────────────────────────────────────────────────────────────

// ── Bottom Destination Model ───────────────────────────────────────────────────

sealed class BottomDestination(
    val labelRes: StringResource,
    val iconSelected: org.jetbrains.compose.resources.DrawableResource,
    val iconUnselected: org.jetbrains.compose.resources.DrawableResource,
    val route: IDestination,
) {
    
    data object Home : BottomDestination(
        labelRes = Res.string.nav_home,
        iconSelected = Res.drawable.ic_home,
        iconUnselected = Res.drawable.ic_home_outlined,
        route = IMainGraph.Home,
    )
    
    data object Explore : BottomDestination(
        labelRes = Res.string.nav_explore,
        iconSelected = Res.drawable.ic_explore,
        iconUnselected = Res.drawable.ic_explore_outlined,
        route = IMainGraph.Explore(),
    )
    
    data object Bookings : BottomDestination(
        labelRes = Res.string.nav_bookings,
        iconSelected = Res.drawable.ic_bookings,
        iconUnselected = Res.drawable.ic_bookings_outlined,
        route = IMainGraph.Bookings,
    )
    
    data object Favorites : BottomDestination(
        labelRes = Res.string.nav_favorites,
        iconSelected = Res.drawable.ic_favorite,
        iconUnselected = Res.drawable.ic_favorite_outlined,
        route = IMainGraph.Favorites,
    )
    
    data object Profile : BottomDestination(
        labelRes = Res.string.nav_profile,
        iconSelected = Res.drawable.ic_profile,
        iconUnselected = Res.drawable.ic_profile_outlined,
        route = IMainGraph.Profile,
    )
    
    companion object {
        val destinations = listOf(Home, Explore, Bookings, Favorites, Profile)
    }
}

// ── Main navigation bar with NavController integration ──────────────────────────

@Composable
fun HallaakBottomNavBar(
    navController: NavHostController,
    destinations: List<BottomDestination> = BottomDestination.destinations,
    modifier: Modifier = Modifier,
    containerColor: Color = HallaakNavBarDefaults.containerColor,
    elevation: Dp = HallaakNavBarDefaults.elevation,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination by remember { derivedStateOf { navBackStackEntry?.destination } }
    
    // Show bar only when on a bottom destination
    val navigationBarState by remember {
        derivedStateOf {
            destinations.any { currentDestination?.hasRoute(it.route::class) == true }
        }
    }
    
    HallaakBottomNavBarContent(
        isVisible = navigationBarState,
        currentDestination = currentDestination,
        destinations = destinations,
        onClick = { destination ->
            // Tapping the already-selected tab does nothing — no pop, no re-creation
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(destination.route::class)
            } == true

            if (!isSelected) {
                navController.navigate(destination.route) {
                    // Pop up to start destination to avoid stack buildup
                    navController.graph.startDestinationRoute?.let {
                        popUpTo(it) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        modifier = modifier,
        containerColor = containerColor,
        elevation = elevation,
    )
}

// ── Internal bar content ────────────────────────────────────────────────────────

@Composable
private fun HallaakBottomNavBarContent(
    isVisible: Boolean,
    currentDestination: NavDestination?,
    destinations: List<BottomDestination>,
    onClick: (BottomDestination) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = HallaakNavBarDefaults.containerColor,
    elevation: Dp = HallaakNavBarDefaults.elevation,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(HallaakTheme.dimensions.bottomNavHeight),
            color = containerColor,
            shadowElevation = elevation,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = HallaakTheme.dimensions.space16,
                        vertical = HallaakTheme.dimensions.space8,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                destinations.forEach { destination ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(destination.route::class)
                    } == true
                    
                    HallaakBottomNavItem(
                        destination = destination,
                        isSelected = isSelected,
                        onClick = { onClick(destination) },
                    )
                }
            }
        }
    }
}

// ── Single nav item ─────────────────────────────────────────────────────────────

@Composable
private fun RowScope.HallaakBottomNavItem(
    destination: BottomDestination,
    isSelected: Boolean,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: HallaakNavItemColors = HallaakNavItemDefaults.colors(),
    textStyles: HallaakNavItemTextStyles = HallaakNavItemDefaults.textStyles(),
    onClick: () -> Unit,
) {
    val label = stringResource(destination.labelRes)
    val icon = if (isSelected) destination.iconSelected else destination.iconUnselected
    
    Column(
        modifier = Modifier
            .weight(1f)
            .height(HallaakNavItemDefaults.minHeight),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Icon container with indicator pill
        Box(
            modifier = Modifier
                .size(HallaakNavItemDefaults.containerSize)
                .clip(HallaakNavItemDefaults.iconShape)
                .selectable(
                    selected = isSelected,
                    onClick = onClick,
                    role = Role.Tab,
                    interactionSource = interactionSource,
                    indication = ripple(color = colors.iconColor(isSelected)),
                )
                .background(color = colors.indicatorColor(isSelected)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                tint = colors.iconColor(isSelected),
                modifier = Modifier.size(HallaakNavItemDefaults.iconSize),
            )
        }
        
        Text(
            text = label,
            style = textStyles.labelStyle(isSelected),
            color = colors.labelColor(isSelected),
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
        )
    }
}

// ── Color model ─────────────────────────────────────────────────────────────────

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

// ── Defaults ────────────────────────────────────────────────────────────────────

object HallaakNavItemDefaults {
    val minHeight: Dp = 60.dp
    val iconShape: Shape = CircleShape
    val iconSize: Dp = 22.dp
    val containerSize: Dp = 36.dp
    
    @Composable
    fun colors(
        selectedIconColor: Color = HallaakTheme.colors.primary,
        unselectedIconColor: Color = HallaakTheme.colors.onSurfaceVariant,
        selectedLabelColor: Color = HallaakTheme.colors.primary,
        unselectedLabelColor: Color = HallaakTheme.colors.onSurfaceVariant,
        indicatorColor: Color = HallaakTheme.colors.gold.copy(alpha = 0.25f),
    ) = HallaakNavItemColors(
        selectedIconColor = selectedIconColor,
        unselectedIconColor = unselectedIconColor,
        selectedLabelColor = selectedLabelColor,
        unselectedLabelColor = unselectedLabelColor,
        indicatorColor = indicatorColor,
    )
    
    @Composable
    fun textStyles(
        selectedStyle: TextStyle = HallaakTheme.typography.label.medium.copy(
            fontWeight = FontWeight.Bold
        ),
        unselectedStyle: TextStyle = HallaakTheme.typography.label.medium,
    ) = HallaakNavItemTextStyles(
        selectedStyle = selectedStyle,
        unselectedStyle = unselectedStyle,
    )
}

object HallaakNavBarDefaults {
    val containerColor: Color
        @Composable get() = HallaakTheme.colors.background.surfaceHigh
    
    val elevation: Dp = 12.dp
}
