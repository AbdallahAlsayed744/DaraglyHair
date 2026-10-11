# Halla'ak Navigation Structure

## Visual Navigation Flow

```
┌─────────────────────────────────────────────────────────────┐
│                      HallaakApp                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              NavHost (Type-Safe Routes)              │  │
│  │                                                      │  │
│  │  ┌─────────────────┐       ┌───────────────────┐  │  │
│  │  │   Auth Graph    │       │   Main Graph      │  │  │
│  │  │                 │       │                   │  │  │
│  │  │  - Login        │       │  - Home           │  │  │
│  │  │  - Register     │       │  - Explore        │  │  │
│  │  │  - Forgot Pass  │       │  - Bookings       │  │  │
│  │  └─────────────────┘       │  - Favorites      │  │  │
│  │                            │  - Profile        │  │  │
│  │                            └───────────────────┘  │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         HallaakBottomNavBar (Auto-Visibility)        │  │
│  │  ┌────┐  ┌────┐  ┌────┐  ┌────┐  ┌────┐           │  │
│  │  │Home│  │Expl│  │Book│  │Fav │  │Prof│           │  │
│  │  │ 🏠 │  │ 🧭 │  │ 📅 │  │ ❤️ │  │ 👤 │           │  │
│  │  └────┘  └────┘  └────┘  └────┘  └────┘           │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Bottom Navigation Destinations

### 1️⃣ Home — الرئيسية

```
┌─────────────────────────────────────┐
│         🏠 Home Screen              │
├─────────────────────────────────────┤
│                                     │
│  📍 Nearby Barbershops              │
│  ⭐ Recommended                      │
│  🔥 Popular Services                │
│  💈 Featured Barbers                │
│                                     │
│  [Shop Card] [Shop Card]            │
│  [Shop Card] [Shop Card]            │
│                                     │
└─────────────────────────────────────┘
     Route: IMainGraph.Home
     State: BottomDestination.Home
     Icon: ic_home (filled) / ic_home_outlined
```

### 2️⃣ Explore — اكتشف

```
┌─────────────────────────────────────┐
│       🧭 Explore Screen             │
├─────────────────────────────────────┤
│                                     │
│  🔍 Search: [____________]          │
│                                     │
│  🗺️  Map View  |  📋 List View     │
│                                     │
│  Filters:                           │
│  ⚙️ Services  💰 Price  📏 Distance │
│                                     │
│  [Map / List Results]               │
│                                     │
└─────────────────────────────────────┘
     Route: IMainGraph.Explore
     State: BottomDestination.Explore
     Icon: ic_explore (filled) / ic_explore_outlined
```

### 3️⃣ Bookings — حجوزاتي

```
┌─────────────────────────────────────┐
│      📅 Bookings Screen             │
├─────────────────────────────────────┤
│                                     │
│  Tabs:                              │
│  [Upcoming] [Past] [Cancelled]      │
│                                     │
│  Today:                             │
│  ┌───────────────────────────────┐ │
│  │ ✂️ Ahmed's Barber Shop        │ │
│  │ 🕐 2:30 PM - Haircut          │ │
│  │ [View] [Modify] [Cancel]      │ │
│  └───────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘
     Route: IMainGraph.Bookings
     State: BottomDestination.Bookings
     Icon: ic_bookings (filled) / ic_bookings_outlined
```

### 4️⃣ Favorites — المفضلة

```
┌─────────────────────────────────────┐
│      ❤️ Favorites Screen            │
├─────────────────────────────────────┤
│                                     │
│  Your Saved Barbershops (12)       │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ 💈 Elite Barber Shop          │ │
│  │ ⭐ 4.8 · 500m away            │ │
│  │ ❤️ [Remove from favorites]    │ │
│  └───────────────────────────────┘ │
│                                     │
│  [Shop Card] [Shop Card]            │
│                                     │
└─────────────────────────────────────┘
     Route: IMainGraph.Favorites
     State: BottomDestination.Favorites
     Icon: ic_favorite (filled) / ic_favorite_outlined
```

### 5️⃣ Profile — حسابي

```
┌─────────────────────────────────────┐
│       👤 Profile Screen             │
├─────────────────────────────────────┤
│                                     │
│  👤 [Profile Photo]                 │
│  Ahmed Mohamed                      │
│  ahmed@example.com                  │
│                                     │
│  📱 Personal Info                   │
│  🌐 Language (العربية/English)      │
│  🔔 Notifications                   │
│  💳 Payment Methods                 │
│  ℹ️  About                          │
│  🚪 Logout                          │
│                                     │
└─────────────────────────────────────┘
     Route: IMainGraph.Profile
     State: BottomDestination.Profile
     Icon: ic_profile (filled) / ic_profile_outlined
```

## Navigation State Management

### State Flow Diagram

```
┌────────────────────────────────────────────────────────────┐
│                 NavHostController                           │
│                                                             │
│  currentBackStackEntryAsState()                            │
│           │                                                 │
│           ▼                                                 │
│  ┌──────────────────┐                                      │
│  │ Current Destination                                     │
│  │  - route: KClass                                        │
│  │  - hierarchy: List<NavDestination>                     │
│  └──────────────────┘                                      │
│           │                                                 │
│           ▼                                                 │
│  ┌──────────────────────────────────────┐                 │
│  │ derivedStateOf {                      │                 │
│  │   destinations.any {                  │                 │
│  │     current?.hasRoute(it.route::class)│                 │
│  │   }                                   │                 │
│  │ }                                     │                 │
│  └──────────────────────────────────────┘                 │
│           │                                                 │
│           ├─── TRUE  ──> Show Bottom Nav                   │
│           └─── FALSE ──> Hide Bottom Nav                   │
└────────────────────────────────────────────────────────────┘
```

### Selected State Logic

```
For each destination item:

1. Get current destination from NavController
2. Check hierarchy: currentDestination?.hierarchy
3. Match route: hierarchy.any { it.hasRoute(item.route::class) }
4. Apply visual state:
   - If matched (true):
     * Icon: filled variant
     * Color: primary (Deep Slate Blue)
     * Label: bold weight
     * Background: gold pill (25% opacity)
   - If not matched (false):
     * Icon: outlined variant
     * Color: onSurfaceVariant (medium gray)
     * Label: normal weight
     * Background: transparent
```

## Navigation Actions

### User Tap Flow

```
User taps destination item
         │
         ▼
onClick(destination) triggered
         │
         ▼
navController.navigate(destination.route) {
    popUpTo(startDestinationId) {
        saveState = true        // Save screen state
    }
    launchSingleTop = true     // Prevent duplicates
    restoreState = true        // Restore previous state
}
         │
         ▼
NavHost recomposition
         │
         ├─── New screen displayed
         │
         └─── Bottom nav updates selected state
```

## Visibility Control

### Show/Hide Logic

```
Bottom Navigation is VISIBLE when:
- Current route matches any of:
  * IMainGraph.Home
  * IMainGraph.Explore
  * IMainGraph.Bookings
  * IMainGraph.Favorites
  * IMainGraph.Profile

Bottom Navigation is HIDDEN when:
- On Auth screens:
  * Login
  * Register
  * Forgot Password
- On detail/flow screens:
  * Barbershop Details
  * Booking Checkout
  * Service Selection
  * Payment
  * Any nested detail screen

Animation:
- Enter: slideInVertically (from bottom)
- Exit: slideOutVertically (to bottom)
```

## Component Hierarchy

```
HallaakApp
└── HallaakTheme
    └── Scaffold
        ├── bottomBar: HallaakBottomNavBar
        │   └── AnimatedVisibility (slideIn/slideOut)
        │       └── Surface (72dp height, elevation 12dp)
        │           └── Row (5 items, SpaceEvenly)
        │               ├── HallaakBottomNavItem (Home)
        │               ├── HallaakBottomNavItem (Explore)
        │               ├── HallaakBottomNavItem (Bookings)
        │               ├── HallaakBottomNavItem (Favorites)
        │               └── HallaakBottomNavItem (Profile)
        │
        └── content: NavHost
            ├── buildAuthNavGraph()
            └── buildMainNavGraph()
                ├── composable<Home> { HomeScreen() }
                ├── composable<Explore> { ExploreScreen() }
                ├── composable<Bookings> { BookingsScreen() }
                ├── composable<Favorites> { FavoritesScreen() }
                └── composable<Profile> { ProfileScreen() }
```

## Data Flow

```
┌─────────────────────────────────────────────────────────────┐
│                  BottomDestination (Sealed Class)            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  sealed class BottomDestination(                            │
│      val labelEn: String,          ← English label          │
│      val labelAr: String,          ← Arabic label           │
│      val iconSelected: Resource,   ← Filled icon            │
│      val iconUnselected: Resource, ← Outlined icon          │
│      val route: IDestination,      ← Navigation route       │
│  )                                                           │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ data object Home : BottomDestination(...)            │  │
│  │ data object Explore : BottomDestination(...)         │  │
│  │ data object Bookings : BottomDestination(...)        │  │
│  │ data object Favorites : BottomDestination(...)       │  │
│  │ data object Profile : BottomDestination(...)         │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  companion object {                                          │
│      val destinations = listOf(                              │
│          Home, Explore, Bookings, Favorites, Profile         │
│      )                                                       │
│  }                                                           │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
               HallaakBottomNavBar
                  (consumes list)
                           │
                           ▼
              For each destination item
                     renders
                           │
                           ▼
              HallaakBottomNavItem
           (icon + label + selection state)
```

## Theme Integration

```
HallaakTheme
├── colors
│   ├── primary (Deep Slate Blue #1C3557)
│   ├── gold (Warm Gold #C8952A)
│   ├── onSurface (Near-black #1A1A1A)
│   ├── onSurfaceVariant (Medium gray #5C5C5C)
│   └── background
│       └── surfaceHigh (Elevated surface #FAF9F7)
│
├── typography
│   └── label
│       └── medium (11sp, Cairo font)
│           ├── normal weight (unselected)
│           └── bold weight (selected)
│
└── dimensions
    ├── bottomNavHeight (72dp)
    ├── iconSize (22dp)
    ├── containerSize (36dp)
    ├── space16 (horizontal padding)
    └── space8 (vertical padding)
```

## Screen Padding Management

```
HallaakApp Scaffold
     │
     ├── bottomBar: HallaakBottomNavBar (72dp)
     │
     └── content: { innerPadding ->
              │
              ▼
         CompositionLocalProvider(LocalPadding provides innerPadding)
              │
              ▼
         All child screens access via:
         modifier.padding(LocalPadding.current)
              │
              └── Ensures content doesn't overlap bottom nav
```

## Type-Safe Routing

```kotlin
// Definition
sealed interface IMainGraph : IDestination {
    @Serializable data object Home : IMainGraph
    @Serializable data object Explore : IMainGraph
    // ...
}

// Usage
navController.navigate(IMainGraph.Home)  // ✅ Compile-time safe
navController.navigate("home")           // ❌ Not used - runtime error risk

// Route matching
currentDestination?.hasRoute(IMainGraph.Home::class)  // Type-safe check
```

## Summary

This navigation structure provides:

✅ **Type Safety**: Kotlin serialization for compile-time routing
✅ **State Management**: Proper state preservation and restoration
✅ **Visual Feedback**: Clear selected/unselected states
✅ **Smooth UX**: Animations and transitions
✅ **Accessibility**: Semantic roles and descriptions
✅ **Maintainability**: Clear separation of concerns
✅ **Scalability**: Easy to add new destinations
✅ **Bilingual**: English and Arabic support
✅ **Theme Integration**: Consistent design tokens
✅ **Production Ready**: No hardcoded values or shortcuts

---

**Architecture**: Clean, maintainable, and extensible
**Performance**: Efficient with derived state
**User Experience**: Smooth, intuitive, accessible
