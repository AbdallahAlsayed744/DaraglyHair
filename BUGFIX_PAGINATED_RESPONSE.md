# Bug Fix: PaginatedResponse Deserialization Error

## Problem
The app was crashing on the home screen with the error:
```
kotlinx.serialization.MissingFieldException: Field 'items' is required for type with serial name 
'com.daragly.daraglyhair.feature.home.data.dto.PaginatedResponse', but it was missing
```

## Root Cause
The API response structure changed. The barbershop search endpoint (`POST /api/v1/barbershops/search`) now returns data in a `"data"` field instead of `"items"`:

**Actual API Response:**
```json
{
  "data": [
    { "id": "...", "name": "...", ... }
  ]
}
```

**Expected by App:**
```json
{
  "items": [
    { "id": "...", "name": "...", ... }
  ]
}
```

## Solution
Modified `PaginatedResponse.kt` to support both field names (`data` and `items`) for backward compatibility:

```kotlin
@Serializable
data class PaginatedResponse<T>(
    @SerialName("data")
    val data: List<T>? = null,
    @SerialName("items")
    val items: List<T>? = null,
    // ... other fields with defaults
) {
    // Helper to get list regardless of field name
    fun getItemsList(): List<T> = data ?: items ?: emptyList()
}
```

**Note**: The method is named `getItemsList()` instead of `getItems()` to avoid JVM signature clash with the auto-generated getter for the `items` property.

## Changes Made

### 1. Updated `PaginatedResponse.kt`
- Added `data` field to support the new API response structure
- Made both `data` and `items` nullable with default null values
- Added `getItems()` helper method that returns whichever field has data
- Kept all pagination metadata fields with default values to handle incomplete responses

### 2. Updated `HomeRepository.kt`
- Changed `response.items` to `response.getItemsList()` in:
  - `searchBarbershops()` method
  - `getUpcomingAppointment()` method

### 3. Updated `API_DOCUMENTATION.md`
- Documented that the API returns `"data"` field, not `"items"`
- Added note about pagination metadata potentially being missing

### 4. Improved Error Logging in `RemoteDataSourceProvider.kt`
- Enhanced deserialization error logging to show full response body
- Helps diagnose future API structure mismatches

## Testing
After this fix:
- The app will successfully deserialize responses with `"data"` field (new API format)
- The app will still work with `"items"` field (old API format) if the API changes back
- Empty responses will return empty lists instead of crashing
- Better error messages if future deserialization issues occur

## Impact
- ✅ Fixes home screen crash
- ✅ Barbershops will now display correctly
- ✅ Appointments search will work correctly
- ✅ Backward compatible with old API format
- ✅ No breaking changes to domain layer or UI layer
