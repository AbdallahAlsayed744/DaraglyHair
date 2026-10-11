# Bottom Navigation Quick Reference

## 🚀 Quick Start

### Use the Bottom Navigation

Already integrated in `HallaakApp.kt`:

```kotlin
Scaffold(
    bottomBar = {
        HallaakBottomNavBar(
            navController = navHostController,
            destinations = BottomDestination.destinations,
            useArabicLabels = true,
        )
    }
) { ... }
```

### Navigate Programmatically

```kotlin
// From a ViewModel or screen
navigator.navigate(IMainGraph.Explore)
navigator.navigate(IMainGraph.Bookings)
```

## 📱 The 5 Destinations

| Destination | Route | Screen File |
|------------|-------|-------------|
| 🏠 Home | `IMainGraph.Home` | `feature/home/ui/HomeScreen.kt` |
| 🧭 Explore | `IMainGraph.Explore` | `feature/explore/ui/ExploreScreen.kt` |
| 📅 Bookings | `IMainGraph.Bookings` | `feature/bookings/ui/BookingsScreen.kt` |
| ❤️ Favorites | `IMainGraph.Favorites` | `feature/favorites/ui/FavoritesScreen.kt` |
| 👤 Profile | `IMainGraph.Profile` | `feature/profile/ui/ProfileScreen.kt` |

## 🎨 Visual Specs

```
Height:               72dp
Icon Size:            22dp
Icon Container:       36dp (circular)
Selected Color:       primary (Deep Slate Blue #1C3557)
Unselected Color:     onSurfaceVariant (Gray #5C5C5C)
Indicator:            gold with 25% alpha (#C8952A)
Elevation:            12dp
Animation:            slideIn/slideOut from bottom
```

## 🔧 Common Tasks

### Add Padding to Your Screen

```kotlin
@Composable
fun YourScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalPadding.current)  // ← Important!
    ) {
        // Your content
    }
}
```

### Check Current Destination

```kotlin
val navBackStackEntry by navController.currentBackStackEntryAsState()
val currentRoute = navBackStackEntry?.destination?.route
```

### Navigate with Options

```kotlin
navController.navigate(IMainGraph.Profile) {
    popUpTo(IMainGraph.Home) { inclusive = true }
    launchSingleTop = true
}
```

## 🌐 Localization

### Switch Language

```kotlin
HallaakBottomNavBar(
    navController = navHostController,
    useArabicLabels = true,   // Arabic labels
    // OR
    useArabicLabels = false,  // English labels
)
```

### Labels

| Destination | English | Arabic |
|------------|---------|--------|
| Home | Home | الرئيسية |
| Explore | Explore | اكتشف |
| Bookings | Bookings | حجوزاتي |
| Favorites | Favorites | المفضلة |
| Profile | Profile | حسابي |

## 🎯 Icons

All icons in `composeResources/drawable/`:

```
ic_home.xml + ic_home_outlined.xml
ic_explore.xml + ic_explore_outlined.xml
ic_bookings.xml + ic_bookings_outlined.xml
ic_favorite.xml + ic_favorite_outlined.xml
ic_profile.xml + ic_profile_outlined.xml
```

## 🔄 State Management

### How It Works

1. User taps destination
2. `navController.navigate()` called
3. NavHost recomposes with new screen
4. Bottom nav automatically updates selected state
5. Previous screen state is saved and can be restored

### Stack Behavior

```kotlin
navigate(destination) {
    popUpTo(startDestination) {
        saveState = true      // Saves current screen state
    }
    launchSingleTop = true   // Prevents duplicate destinations
    restoreState = true      // Restores state when returning
}
```

## 👁️ Visibility Control

### Automatically Shows On:
- Home screen
- Explore screen
- Bookings screen
- Favorites screen
- Profile screen

### Automatically Hides On:
- Login/Register screens
- Detail screens (shop details, booking details, etc.)
- Checkout flows
- Any non-bottom destination

No manual control needed!

## 🎨 Customization

### Custom Colors

```kotlin
HallaakBottomNavBar(
    navController = navHostController,
    containerColor = Color.White,  // Custom background
    elevation = 8.dp,              // Custom elevation
)
```

### Custom Item Colors

Edit `HallaakNavItemDefaults.colors()`:

```kotlin
@Composable
fun colors(
    selectedIconColor: Color = HallaakTheme.colors.primary,
    unselectedIconColor: Color = HallaakTheme.colors.onSurfaceVariant,
    indicatorColor: Color = HallaakTheme.colors.gold.copy(alpha = 0.25f),
) = ...
```

## 🛠️ Troubleshooting

### Bottom Nav Not Showing

Check:
1. Are you on a bottom destination? (Home, Explore, etc.)
2. Is the route correctly defined in `IMainGraph.kt`?
3. Is the screen registered in `buildMainNavGraph()`?

### Selected State Not Updating

Check:
1. Is the route using the correct `::class` reference?
2. Is NavController passed correctly to `HallaakBottomNavBar`?
3. Check the hierarchy: `currentDestination?.hierarchy?.any { ... }`

### Content Overlapping Bottom Nav

Make sure your screen uses:

```kotlin
modifier = Modifier
    .fillMaxSize()
    .padding(LocalPadding.current)  // ← This is essential!
```

### Icons Not Showing

1. Check icon files exist in `composeResources/drawable/`
2. Verify `Res.drawable.ic_[name]` imports
3. Run Gradle sync to generate resources

## 📚 File Locations

```
Key Files:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Navigation:
└── common/ui/navigation/
    └── IMainGraph.kt                 # Routes definition

Bottom Nav:
└── designsystem/components/bars/
    └── HallaakBottomNavBar.kt        # Navigation bar

Screens:
└── feature/
    ├── home/ui/HomeScreen.kt
    ├── explore/ui/ExploreScreen.kt
    ├── bookings/ui/BookingsScreen.kt
    ├── favorites/ui/FavoritesScreen.kt
    └── profile/ui/ProfileScreen.kt

Icons:
└── composeResources/drawable/
    └── ic_*.xml (10 files total)

Theme:
└── designsystem/theme/
    ├── HallaakColors.kt
    ├── HallaakTypography.kt
    └── HallaakDimensions.kt
```

## 🧪 Testing Checklist

Quick manual test:

- [ ] Tap each of 5 destinations
- [ ] Switch between destinations rapidly
- [ ] Verify selected state updates
- [ ] Check icons change (filled ↔ outlined)
- [ ] Navigate to detail screen → bar hides
- [ ] Back to main screen → bar shows
- [ ] State preserved when switching tabs
- [ ] Arabic labels display correctly
- [ ] English labels display correctly
- [ ] No content overlap with bottom nav

## 💡 Pro Tips

1. **Always use `LocalPadding.current`** in your screens
2. **Use type-safe routes** (`IMainGraph.Home` not `"home"`)
3. **Icons should be 24x24dp** vector drawables
4. **Labels should be short** (1-2 words max)
5. **Test on different screen sizes** (phones, tablets)
6. **Test in RTL mode** for Arabic
7. **Check system gesture navigation** compatibility

## 🆘 Need Help?

Check these docs:
- `BOTTOM_NAVIGATION_GUIDE.md` - Complete implementation guide
- `IMPLEMENTATION_SUMMARY.md` - What was built
- `NAVIGATION_STRUCTURE.md` - Architecture diagrams

## 🎓 Key Concepts

**Type-Safe Navigation**: Routes are Kotlin objects, not strings
**State Preservation**: Screen state saved when switching tabs
**Derived State**: Selected state calculated efficiently
**Composition Local**: Padding shared via CompositionLocal
**Sealed Classes**: Destinations defined as sealed hierarchy
**Resource Generation**: Icons accessed via generated `Res` object

---

**Quick Reference Version**: 1.0.0
**Last Updated**: Implementation completion date
