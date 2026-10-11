# Halla'ak Bottom Navigation Implementation Guide

## Overview

This document describes the complete bottom navigation system for the Halla'ak (حلاقك) barbershop discovery application, built with Kotlin Multiplatform and Compose Multiplatform.

## Architecture

### 5 Navigation Destinations

The bottom navigation implements exactly 5 destinations, each with a specific purpose:

| Destination | English | Arabic | Icon | Purpose |
|------------|---------|--------|------|---------|
| **Home** | Home | الرئيسية | House icon | Main dashboard, nearby shops, recommendations |
| **Explore** | Explore | اكتشف | Compass icon | Search, map view, filters, discovery |
| **Bookings** | Bookings | حجوزاتي | Calendar icon | Appointments (upcoming, past, cancelled) |
| **Favorites** | Favorites | المفضلة | Heart icon | Saved barbershops |
| **Profile** | Profile | حسابي | User icon | Account settings and preferences |

## File Structure

```
shared/src/commonMain/
├── composeResources/drawable/
│   ├── ic_home.xml                    # Filled home icon (selected)
│   ├── ic_home_outlined.xml           # Outlined home icon (unselected)
│   ├── ic_explore.xml                 # Filled compass icon (selected)
│   ├── ic_explore_outlined.xml        # Outlined compass icon (unselected)
│   ├── ic_bookings.xml                # Filled calendar icon (selected)
│   ├── ic_bookings_outlined.xml       # Outlined calendar icon (unselected)
│   ├── ic_favorite.xml                # Filled heart icon (selected)
│   ├── ic_favorite_outlined.xml       # Outlined heart icon (unselected)
│   ├── ic_profile.xml                 # Filled user icon (selected)
│   └── ic_profile_outlined.xml        # Outlined user icon (unselected)
│
├── kotlin/com/daragly/daraglyhair/
│   ├── HallaakApp.kt                  # Root app composable with navigation
│   │
│   ├── common/ui/navigation/
│   │   └── IMainGraph.kt              # Main navigation graph with 5 destinations
│   │
│   ├── designsystem/components/bars/
│   │   └── HallaakBottomNavBar.kt     # Bottom navigation bar component
│   │
│   └── feature/
│       ├── home/ui/HomeScreen.kt      # Home dashboard screen
│       ├── explore/ui/ExploreScreen.kt # Search and discovery screen
│       ├── bookings/ui/BookingsScreen.kt # Appointments screen
│       ├── favorites/ui/FavoritesScreen.kt # Saved shops screen
│       └── profile/ui/ProfileScreen.kt # Account settings screen
```

## Implementation Details

### 1. Navigation Graph (IMainGraph.kt)

Defines 5 type-safe destinations using Kotlin Serialization:

```kotlin
sealed interface IMainGraph : IDestination {
    @Serializable data object MainGraph  : IGraph
    @Serializable data object Home       : IMainGraph
    @Serializable data object Explore    : IMainGraph
    @Serializable data object Bookings   : IMainGraph
    @Serializable data object Favorites  : IMainGraph
    @Serializable data object Profile    : IMainGraph
}
```

### 2. Bottom Navigation Bar (HallaakBottomNavBar.kt)

#### Key Features:

- **Automatic visibility control**: Shows only when on a bottom destination
- **State management**: Uses NavController hierarchy check for selected state
- **Icon variants**: Filled icons for selected, outlined for unselected
- **Bilingual support**: Arabic and English labels
- **Smooth animations**: Slide-in/out when visibility changes
- **Material Design 3**: Follows M3 patterns with custom theming

#### Component Structure:

```kotlin
// Model
sealed class BottomDestination(
    val labelEn: String,
    val labelAr: String,
    val iconSelected: DrawableResource,
    val iconUnselected: DrawableResource,
    val route: IDestination,
)

// Main composable
@Composable
fun HallaakBottomNavBar(
    navController: NavHostController,
    destinations: List<BottomDestination> = BottomDestination.destinations,
    useArabicLabels: Boolean = true,
    containerColor: Color = HallaakNavBarDefaults.containerColor,
    elevation: Dp = HallaakNavBarDefaults.elevation,
)
```

#### Visual Design:

- **Height**: 72dp (defined in HallaakDimensions.bottomNavHeight)
- **Container**: Surface with 12dp elevation
- **Background**: surfaceHigh color from theme
- **Selected state**: 
  - Primary color for icon and label
  - Gold translucent pill (25% opacity) behind icon
  - Bold font weight for label
- **Unselected state**:
  - onSurfaceVariant color for icon and label
  - Regular font weight for label
  - No background

### 3. Icon Resources

All icons are Material Design vector drawables in Android XML format:

- **Size**: 24x24dp viewport
- **Format**: Vector XML
- **Variants**: Each destination has filled (selected) and outlined (unselected) versions
- **Location**: `composeResources/drawable/`

### 4. Screen Components

Each feature has a dedicated screen composable:

```kotlin
@Composable
fun [Feature]Screen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalPadding.current),  // Respects scaffold padding
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
        ) {
            Text(text = "Title — عنوان", ...)
            Text(text = "Description", ...)
        }
    }
}
```

**Important**: All screens use `LocalPadding.current` to respect the Scaffold's bottom navigation inset.

### 5. Integration in HallaakApp

```kotlin
@Composable
fun HallaakApp(...) {
    HallaakTheme {
        // Event observers...
        
        Scaffold(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            snackbarHost = { ... },
            bottomBar = {
                HallaakBottomNavBar(
                    navController = navHostController,
                    destinations = BottomDestination.destinations,
                    useArabicLabels = true,
                )
            },
        ) { innerPadding ->
            CompositionLocalProvider(LocalPadding provides innerPadding) {
                NavHost(
                    navController = navHostController,
                    startDestination = navigator.startGraph,
                ) {
                    buildAuthNavGraph()
                    buildMainNavGraph()
                }
            }
        }
    }
}
```

## Theme Integration

### Colors

From `HallaakColors.kt`:

- **Selected icon/label**: `primary` (Deep Slate Blue - #1C3557)
- **Unselected icon/label**: `onSurfaceVariant` (Medium Gray - #5C5C5C)
- **Indicator pill**: `gold` with 25% alpha (Warm Gold - #C8952A)
- **Container background**: `background.surfaceHigh` (Slightly elevated surface)

### Typography

From `HallaakTypography.kt`:

- **Selected label**: `label.medium` with `FontWeight.Bold`
- **Unselected label**: `label.medium` with normal weight
- **Font**: Cairo (Arabic-optimized font loaded from composeResources)

### Dimensions

From `HallaakDimensions.kt`:

- **Bottom nav height**: 72dp
- **Icon size**: 22dp
- **Icon container**: 36dp circular pill
- **Horizontal padding**: 16dp
- **Vertical padding**: 8dp
- **Label top padding**: 4dp

## Navigation Behavior

### Stack Management

When tapping a bottom destination:

```kotlin
navController.navigate(destination.route) {
    popUpTo(navController.graph.startDestinationId) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
```

- **popUpTo**: Prevents stack buildup
- **saveState**: Preserves screen state when switching tabs
- **launchSingleTop**: Avoids duplicate destinations
- **restoreState**: Restores previous state when returning to a tab

### Visibility Control

The bar automatically shows/hides based on the current destination:

```kotlin
val navigationBarState by remember {
    derivedStateOf {
        destinations.any { currentDestination?.hasRoute(it.route::class) == true }
    }
}
```

- **Visible**: When on any of the 5 bottom destinations
- **Hidden**: On auth screens, detail flows, booking checkout, etc.

## RTL Support

The bottom navigation fully supports right-to-left layouts:

- **Icons**: Automatically mirrored where appropriate
- **Labels**: Displayed in Arabic when `useArabicLabels = true`
- **Layout**: Row layout adapts to RTL direction
- **Typography**: Cairo font provides excellent Arabic rendering

## Accessibility

- **Semantic roles**: Each item marked with `Role.Tab`
- **Content descriptions**: Icon descriptions use localized labels
- **Touch targets**: 36dp minimum (icon container)
- **Visual feedback**: Ripple effect on tap
- **High contrast**: Primary color against surface for selected state

## Future Enhancements

### Language Switching

Connect `useArabicLabels` parameter to a language preference:

```kotlin
val currentLanguage by remember { /* language state */ }

HallaakBottomNavBar(
    navController = navHostController,
    useArabicLabels = (currentLanguage == Language.ARABIC),
)
```

### Badge Support

Add notification badges to specific destinations:

```kotlin
data class BottomDestination(
    // existing properties...
    val badge: Int? = null,  // Unread count
)
```

### Animation Refinement

Consider adding:
- Icon scale animation on selection
- Label fade animation
- Color transition animations
- Indicator pill slide animation

### Accessibility Improvements

- Add semantic properties for screen readers
- Implement keyboard navigation support
- Add haptic feedback on selection

## Testing Checklist

- [ ] All 5 destinations navigate correctly
- [ ] Selected state highlights properly
- [ ] Icons switch between filled/outlined variants
- [ ] Labels display in both Arabic and English
- [ ] Navigation bar shows/hides appropriately
- [ ] State persists when switching tabs
- [ ] Back button navigates correctly
- [ ] RTL layout works properly
- [ ] Scaffold padding respected by all screens
- [ ] Visual design matches specifications
- [ ] Smooth animations throughout
- [ ] No stack buildup after multiple taps
- [ ] Works on different screen sizes
- [ ] System navigation insets respected

## Maintenance Notes

### Adding a New Destination

1. Create icon resources (filled + outlined variants)
2. Add destination to `IMainGraph` sealed interface
3. Create screen composable in `feature/[name]/ui/`
4. Add composable route in `buildMainNavGraph()`
5. Add `BottomDestination` object with all properties
6. Add to `BottomDestination.destinations` list
7. Update this documentation

### Modifying Visual Design

Colors and dimensions should be changed in theme files:
- **Colors**: `HallaakColors.kt`
- **Typography**: `HallaakTypography.kt`
- **Dimensions**: `HallaakDimensions.kt`

Avoid hardcoding values in the navigation bar component.

## References

- **Delivery-User Project**: Reference implementation for navigation patterns
- **Material Design 3**: Navigation bar component guidelines
- **Jetpack Compose**: Navigation documentation
- **Kotlin Multiplatform**: Compose Multiplatform documentation

---

**Last Updated**: Implementation completed
**Version**: 1.0.0
**Status**: Production-ready
