# Halla'ak (حلاقك) API Documentation

## Base URL
```
https://barberhub-api.runasp.net/
```

## Authentication
All authenticated endpoints require a JWT Bearer token in the Authorization header:
```
Authorization: Bearer <token>
```

## Common Headers
```
tenant: root
Content-Type: application/json
Accept-Language: en|ar
```

---

## User Profile Endpoints

### GET /api/personal/profile
Get the authenticated user's profile information.

**Authentication**: Required

**Response 200**:
```json
{
  "id": "string",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phoneNumber": "string",
  "imageUrl": "string|null"
}
```

---

## Barbershop Search Endpoints

### POST /api/v1/barbershops/search
Search for barbershops with filters and pagination.

**Authentication**: Optional (returns more data when authenticated)

**Request Body**:
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "keyword": "string|optional",
  "governorate": "string|optional",
  "city": "string|optional",
  "district": "string|optional",
  "isActive": true,
  "isVerified": "boolean|optional",
  "isBookingEnabled": "boolean|optional"
}
```

**Response 200**:
```json
{
  "data": [
    {
      "id": "string",
      "name": "string",
      "description": "string|null",
      "phoneNumber": "string",
      "governorate": "string",
      "city": "string",
      "district": "string|null",
      "streetAddress": "string|null",
      "latitude": "number|null",
      "longitude": "number|null",
      "isVerified": boolean,
      "isBookingEnabled": boolean,
      "isActive": boolean
    }
  ],
  "currentPage": 1,
  "totalPages": 5,
  "totalCount": 50,
  "pageSize": 10,
  "hasPreviousPage": false,
  "hasNextPage": true
}
```

**Note**: The API returns barbershops in a `"data"` field, not `"items"`. The pagination metadata fields (`currentPage`, `totalPages`, etc.) may or may not be present in the response.
```

### GET /api/v1/barbershops/{id}
Get details for a specific barbershop.

**Authentication**: Optional

**Response 200**:
```json
{
  "id": "string",
  "name": "string",
  "description": "string|null",
  "phoneNumber": "string",
  "governorate": "string",
  "city": "string",
  "district": "string|null",
  "streetAddress": "string|null",
  "latitude": "number|null",
  "longitude": "number|null",
  "isVerified": boolean,
  "isBookingEnabled": boolean,
  "isActive": boolean
}
```

---

## Service Categories Endpoints

### GET /api/v1/services/categories
Get all service categories.

**Authentication**: Optional

**Response 200**:
```json
[
  {
    "id": "string",
    "name": "string",
    "description": "string|null",
    "sortOrder": number,
    "isActive": boolean
  }
]
```

---

## Favorites Endpoints

### GET /api/v1/favorites
Get the user's favorite barbershops.

**Authentication**: Required

**Response 200**:
```json
[
  {
    "id": "string",
    "shopId": "string",
    "createdAt": "2024-01-01T00:00:00Z"
  }
]
```

### POST /api/v1/favorites/{shopId}/toggle
Toggle a barbershop in favorites (add if not present, remove if present).

**Authentication**: Required

**Response 200**:
```json
boolean
```
- `true`: Shop was added to favorites
- `false`: Shop was removed from favorites

---

## Appointments Endpoints

### POST /api/v1/appointments/search
Search for appointments with filters.

**Authentication**: Required (automatically filters by current user)

**Request Body**:
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "userId": "string|optional",
  "shopId": "string|optional",
  "shopBarberId": "string|optional",
  "status": "number|optional",
  "fromUtc": "2024-01-01T00:00:00Z|optional",
  "toUtc": "2024-12-31T23:59:59Z|optional"
}
```

**Status Values**:
- `1`: Pending
- `2`: Confirmed
- `3`: Completed
- `4`: CancelledByCustomer
- `5`: CancelledByShop
- `6`: NoShow
- `7`: Arrived

**Response 200**:
```json
{
  "data": [
    {
      "id": "string",
      "shopId": "string",
      "shopBarberId": "string|null",
      "status": number,
      "startAtUtc": "2024-01-01T10:00:00Z",
      "endAtUtc": "2024-01-01T11:00:00Z",
      "totalPriceEgp": number,
      "depositAmountEgp": "number|null",
      "depositStatus": "number|null",
      "gracePeriodMinutes": number
    }
  ],
  "currentPage": 1,
  "totalPages": 1,
  "totalCount": 5,
  "pageSize": 10,
  "hasPreviousPage": false,
  "hasNextPage": false
}
```

**Note**: The API returns appointments in a `"data"` field, not `"items"`. The pagination metadata fields may or may not be present in the response.
```

---

## Notifications Endpoints

### GET /api/v1/notifications
Get user notifications.

**Authentication**: Required

**Response 200**:
```json
[
  {
    "id": "string",
    "title": "string",
    "message": "string",
    "isRead": boolean,
    "createdAt": "2024-01-01T00:00:00Z"
  }
]
```

### PUT /api/v1/notifications/{id}/read
Mark a notification as read.

**Authentication**: Required

**Response 200**: Empty (204 No Content)

---

## Error Response Format

All error responses follow this structure:

```json
{
  "message": "Error description",
  "errors": {
    "FieldName": ["Error message 1", "Error message 2"]
  }
}
```

**Status Codes**:
- `400`: Bad Request - validation errors
- `401`: Unauthorized - missing or invalid token
- `404`: Not Found - resource not found
- `422`: Unprocessable Entity - validation errors
- `500`: Internal Server Error - server-side error

---

## API Limitations & Notes

1. **Location Filtering**: The shop search endpoint accepts `governorate`, `city`, and `district` filters, but does NOT accept user latitude/longitude coordinates for distance-based filtering. Distance calculations must be done client-side if needed.

2. **No Reverse Geocoding**: The API does not provide an endpoint to convert coordinates to addresses.

3. **Shop Images**: The `BarberShopDto` does not currently include image URLs, ratings, review counts, price ranges, or opening hours fields.

4. **Appointment Shop Names**: The appointment response does not include shop names. If you need to display shop names for appointments, you must fetch shop details separately using `GET /api/v1/barbershops/{id}`.

5. **No Unread Count Endpoint**: To get unread notification count, fetch all notifications and count those with `isRead: false`.

6. **Category Filtering**: The shop search endpoint does not accept a `categoryId` filter. Service categories can be fetched separately but cannot be used to filter shops directly.

7. **Pagination**: All paginated endpoints use 1-based page numbering (`pageNumber: 1` is the first page).

8. **UTC Timestamps**: All datetime fields use UTC timezone. The client is responsible for converting to local time.

---

## Authentication Flow

1. User logs in via `POST /api/tokens` (see AuthRemoteDataSource)
2. Server returns `token` and `refreshToken`
3. Client stores tokens securely
4. Client includes `Authorization: Bearer <token>` header in all authenticated requests
5. When token expires, refresh using `POST /api/tokens/refresh`

---

## Tenant Header

All requests must include the `tenant: root` header as configured in the HttpClient.

---

**Documentation Version**: 1.0.0
**Last Updated**: Based on existing implementation patterns
