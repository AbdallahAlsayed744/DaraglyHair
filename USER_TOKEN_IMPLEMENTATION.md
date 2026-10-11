# User Token and Data Storage Implementation

This document describes the implementation of user token storage and HTTP client authentication in DaraglyHair, following the same pattern as Delivery-User.

## Overview

The implementation provides a clean architecture for:
- Storing and retrieving user authentication tokens
- Storing and retrieving user data
- Automatically adding authentication headers to HTTP requests
- Managing authentication state

## Architecture

### 1. User Domain Layer

**User Model** (`feature/user/domain/model/User.kt`)
```kotlin
@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val image: String? = null,
)
```

**User Repository Interface** (`feature/user/domain/repository/IUserRepository.kt`)
- `saveToken(token: String)`
- `getToken(): String`
- `saveUser(user: User)`
- `getUser(): User`
- `saveIsAuthenticated(isAuthenticated: Boolean)`
- `getIsAuthenticated(): Boolean`
- `deleteToken()`, `deleteUser()`, `deleteIsAuthenticated()`

### 2. Data Layer

**UserLocalDataSource** (`feature/user/data/repository/local/UserLocalDataSource.kt`)
- Handles actual DataStore operations
- Stores token and user data securely
- Uses JSON serialization for User object

**UserRepository** (`feature/user/data/repository/UserRepository.kt`)
- Implementation of IUserRepository
- Delegates to UserLocalDataSource

### 3. Use Cases

**Token Management:**
- `SaveTokenUseCase` - Save authentication token
- `GetTokenUseCase` - Retrieve authentication token

**User Management:**
- `SaveUserUseCase` - Save user data
- `GetUserUseCase` - Retrieve user data

**Authentication State:**
- `SaveIsAuthenticatedUseCase` - Save authentication state
- `GetIsAuthenticatedUseCase` - Check if user is authenticated (in auth module)

### 4. HTTP Client Integration

The HttpClient (`common/data/repository/remote/HttpClient.kt`) automatically:
1. Retrieves the stored token using `IUserRepository`
2. Adds the `Authorization: Bearer {token}` header to all requests
3. Adds the `Accept-Language` header based on user's language preference

**Key features:**
- Token is fetched on each request (always up-to-date)
- Suspending lambda allows async token retrieval
- Graceful error handling if token retrieval fails

## Usage Examples

### 1. After Successful Login

```kotlin
// In your login use case or repository
class LoginUseCase(
    private val authRepository: IAuthRepository,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val saveIsAuthenticatedUseCase: SaveIsAuthenticatedUseCase,
) : BaseUseCase<Resource<AuthToken>, LoginRequest>() {
    
    override suspend fun invoke(body: LoginRequest): Resource<AuthToken> = execute {
        val authToken = authRepository.login(body)
        
        // Save token
        saveTokenUseCase.invoke(authToken.token)
        
        // Save user data if available
        authToken.user?.let { user ->
            saveUserUseCase.invoke(user)
        }
        
        // Save authentication state
        saveIsAuthenticatedUseCase.invoke(true)
        
        authToken
    }
}
```

### 2. Check Authentication Status

```kotlin
class SplashViewModel(
    private val getIsAuthenticatedUseCase: GetIsAuthenticatedUseCase,
    private val getUserUseCase: GetUserUseCase,
) : BaseViewModel() {
    
    init {
        viewModelScope.launch {
            when (val result = getIsAuthenticatedUseCase.invoke(Unit)) {
                is Resource.Success -> {
                    if (result.model) {
                        // User is authenticated, get user data
                        when (val userResult = getUserUseCase.invoke(Unit)) {
                            is Resource.Success -> {
                                // Navigate to home with user data
                                navigateToHome(userResult.model)
                            }
                            else -> navigateToLogin()
                        }
                    } else {
                        navigateToLogin()
                    }
                }
                else -> navigateToLogin()
            }
        }
    }
}
```

### 3. Logout Implementation

```kotlin
class LogoutUseCase(
    private val userRepository: IUserRepository,
) : BaseUseCase<Unit, Unit>() {
    
    override suspend fun invoke(body: Unit) {
        // Clear all user data
        userRepository.deleteToken()
        userRepository.deleteUser()
        userRepository.deleteIsAuthenticated()
    }
}
```

### 4. Get Current User Data

```kotlin
class ProfileViewModel(
    private val getUserUseCase: GetUserUseCase,
) : BaseViewModel() {
    
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()
    
    init {
        viewModelScope.launch {
            when (val result = getUserUseCase.invoke(Unit)) {
                is Resource.Success -> {
                    _userState.value = result.model
                }
                else -> {
                    // Handle error
                }
            }
        }
    }
}
```

## HTTP Request Flow

When any HTTP request is made:

1. HttpClient's `defaultRequest` block executes
2. `getToken()` lambda is called
3. `IUserRepository.getToken()` retrieves token from DataStore
4. If token exists and is not blank, `Authorization: Bearer {token}` header is added
5. Request proceeds with authentication header

```
User Login
    ↓
SaveTokenUseCase → DataStore
    ↓
HTTP Request Triggered
    ↓
HttpClient defaultRequest
    ↓
getToken() lambda → IUserRepository.getToken()
    ↓
Token retrieved from DataStore
    ↓
Authorization header added
    ↓
Request sent with authentication
```

## Data Storage

All data is stored using DataStore (platform-specific):
- **ACCESS_TOKEN**: User authentication token
- **USER**: Serialized User object (JSON)
- **IS_AUTHENTICATED**: Boolean flag for auth state
- **LANGUAGE**: User's preferred language

Keys are defined in `LocalDataSourceEnum`.

## Dependencies (Koin DI)

The user module is automatically included in `appModule`:

```kotlin
val appModule = module {
    // ... other dependencies
    includes(authModule, homeModule, userModule)
}
```

All use cases and repositories are injected via Koin.

## Testing

To test the implementation:

1. **Login and verify token storage:**
   - Login with valid credentials
   - Check that subsequent API requests include Authorization header

2. **Logout and verify cleanup:**
   - Logout
   - Verify token is removed
   - Verify API requests no longer include Authorization header

3. **App restart:**
   - Login
   - Close and reopen app
   - Verify user remains authenticated

## Migration from Old Implementation

If you were previously using `ILocalDataSourceProvider` directly:

**Before:**
```kotlin
class SomeRepository(private val local: ILocalDataSourceProvider) {
    suspend fun getToken(): String {
        return local.read(
            key = LocalDataSourceEnum.ACCESS_TOKEN,
            defaultValue = "",
            type = String::class
        )
    }
}
```

**After:**
```kotlin
class SomeRepository(private val userRepository: IUserRepository) {
    suspend fun getToken(): String {
        return userRepository.getToken()
    }
}
```

## Benefits

1. **Clean Architecture**: Clear separation of concerns with domain/data layers
2. **Type Safety**: Use cases provide clear contracts
3. **Testability**: Easy to mock use cases for testing
4. **Consistency**: Same pattern as Delivery-User project
5. **Automatic Headers**: Token automatically added to all HTTP requests
6. **Maintainability**: Centralized user data management
