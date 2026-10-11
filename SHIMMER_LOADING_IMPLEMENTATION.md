# Shimmer Loading Implementation

## Summary
Replaced circular loading indicators with shimmer effects on the Home Screen, matching the pattern used in the Delivery-User app.

## Changes Made

### 1. Created HomeShimmer.kt Component File
**Location:** `shared/src/commonMain/kotlin/com/daragly/daraglyhair/feature/home/ui/components/HomeShimmer.kt`

**Components:**
- `CategoryChipShimmer()` - Shimmer placeholder for service category chips
- `ServiceCategoriesShimmer()` - Complete shimmer for categories section with title and chips
- `UpcomingAppointmentShimmer()` - Shimmer placeholder for appointment card
- `BarbershopCardShimmer()` - Shimmer placeholder for barbershop cards
- `SectionHeaderShimmer()` - Shimmer for section headers with "See All" button
- `HomeShimmerContent()` - Full home screen shimmer combining all sections

### 2. Updated HomeScreen.kt

**Changed Loading States:**

#### Service Categories Loading
**Before:**
```kotlin
is CategoriesState.Loading -> {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HallaakTheme.dimensions.space16),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(32.dp))
    }
}
```

**After:**
```kotlin
is CategoriesState.Loading -> {
    ServiceCategoriesShimmer()
}
```

#### Nearby Barbershops Loading
**Before:**
```kotlin
is NearbyShopsState.Loading -> {
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space48),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
```

**After:**
```kotlin
is NearbyShopsState.Loading -> {
    items(3) {
        BarbershopCardShimmer()
    }
}
```

#### Added Imports
```kotlin
import com.daragly.daraglyhair.feature.home.ui.components.BarbershopCardShimmer
import com.daragly.daraglyhair.feature.home.ui.components.ServiceCategoriesShimmer
```

## Design Pattern

### Shimmer Animation
- Uses the existing `HallaakShimmerBox` utility
- Diagonal sweep animation (1200ms duration)
- Warm colors (surfaceHigh + goldVariant) matching barbershop theme
- Smooth infinite repeat animation

### UI Consistency
- Shimmer placeholders match the size and shape of actual content
- Categories: Rounded chips (~100dp width)
- Barbershop cards: Full-width cards (180dp height)
- Maintains proper spacing using `HallaakTheme.dimensions`

## Benefits

1. **Better UX:** Shimmer effect gives users a sense of content loading rather than just waiting
2. **Visual Continuity:** Placeholder shapes match actual content layout
3. **Perceived Performance:** Makes loading feel faster
4. **Professional Look:** Matches modern app standards (similar to Delivery-User)
5. **Reusable Components:** Shimmer components can be reused across the app

## Usage Example

```kotlin
// In any loading state
when (state) {
    is Loading -> {
        ServiceCategoriesShimmer()
    }
    is Success -> {
        // Show actual content
    }
}
```

## Future Enhancements

Consider adding shimmer effects to other loading states:
- Profile loading in HomeHeader
- Upcoming appointment loading
- Search results loading
- Shop details loading
- Any other list or content loading states

## Testing

To see the shimmer effect:
1. Run the app
2. Navigate to Home screen
3. Pull to refresh or clear cache to trigger loading states
4. Observe smooth shimmer animations instead of circular progress indicators

---
*Last Updated: [Current Date]*
*Similar Pattern: Delivery-User app HomeShimmer.kt*
