package com.daragly.daraglyhair.feature.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.LocalPadding
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.home.domain.model.Appointment
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import com.daragly.daraglyhair.feature.home.domain.model.ServiceCategory
import com.daragly.daraglyhair.feature.home.ui.components.BarbershopCard
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.ic_calendar
import daraglyhair.shared.generated.resources.ic_close
import daraglyhair.shared.generated.resources.ic_location
import daraglyhair.shared.generated.resources.ic_notifications_outlined
import daraglyhair.shared.generated.resources.ic_search
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    // Collect events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.NavigateToNotifications -> {
                    // TODO: Navigate to notifications
                }
                is HomeEvent.NavigateToExplore -> {
                    navigator.navigate(IMainGraph.Explore)
                }
                is HomeEvent.NavigateToExploreWithCategory -> {
                    navigator.navigate(IMainGraph.Explore)
                }
                is HomeEvent.NavigateToShopDetails -> {
                    // TODO: Navigate to shop details
                }
                is HomeEvent.NavigateToAppointmentDetails -> {
                    // TODO: Navigate to appointment details
                }
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.onAction(HomeAction.Refresh) },
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 0.dp,
                bottom = LocalPadding.current.calculateBottomPadding(),
            ),
        ) {
            // Header Section with Status Bar Padding
            item {
                HomeHeader(
                    profileState = state.profile,
                    onNotificationClick = { viewModel.onAction(HomeAction.NotificationClicked) },
                    onRetryProfile = { viewModel.onAction(HomeAction.RetryProfile) },
                )
            }

            // Search Bar
            item {
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space16))
                SearchBar(
                    keyword = state.searchKeyword,
                    onKeywordChanged = { viewModel.onAction(HomeAction.SearchKeywordChanged(it)) },
                    onClearClick = { viewModel.onAction(HomeAction.ClearSearch) },
                    onSearchClick = { viewModel.onAction(HomeAction.ExploreClicked) },
                    modifier = Modifier.padding(horizontal = HallaakTheme.dimensions.space16),
                )
            }

            // Service Categories
            item {
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space24))
                ServiceCategoriesSection(
                    categoriesState = state.categories,
                    onCategoryClick = { viewModel.onAction(HomeAction.CategoryClicked(it)) },
                    onRetry = { viewModel.onAction(HomeAction.RetryCategories) },
                )
            }

            // Upcoming Appointment
            item {
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space24))
                UpcomingAppointmentSection(
                    appointmentState = state.upcomingAppointment,
                    onViewAppointmentClick = { viewModel.onAction(HomeAction.AppointmentCardClicked) },
                    onExploreClick = { viewModel.onAction(HomeAction.ExploreClicked) },
                    onRetry = { viewModel.onAction(HomeAction.RetryUpcomingAppointment) },
                    modifier = Modifier.padding(horizontal = HallaakTheme.dimensions.space16),
                )
            }

            // Nearby Barbershops Section Header
            item {
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space24))
                SectionHeader(
                    title = "صالونات قريبة منك",
                    titleAr = "Nearby Barbershops",
                    onSeeAllClick = { viewModel.onAction(HomeAction.SeeAllShopsClicked) },
                    modifier = Modifier.padding(horizontal = HallaakTheme.dimensions.space16),
                )
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space12))
            }

            // Nearby Barbershops List
            when (val shopsState = state.nearbyShops) {
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
                is NearbyShopsState.Success -> {
                    items(
                        items = shopsState.shops,
                        key = { it.id },
                    ) { shop ->
                        BarbershopCard(
                            barbershop = shop,
                            onCardClick = { viewModel.onAction(HomeAction.ShopCardClicked(shop.id)) },
                            onFavoriteClick = { viewModel.onAction(HomeAction.ToggleFavorite(shop.id)) },
                            isFavoriteToggleInProgress = state.favoriteToggleInProgress.contains(shop.id),
                            modifier = Modifier.padding(
                                horizontal = HallaakTheme.dimensions.space16,
                                vertical = HallaakTheme.dimensions.space8,
                            ),
                        )
                    }
                }
                is NearbyShopsState.Empty -> {
                    item {
                        EmptyState(
                            message = shopsState.message,
                            onActionClick = { viewModel.onAction(HomeAction.ExploreClicked) },
                            actionText = "استكشف الصالونات",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(HallaakTheme.dimensions.space32),
                        )
                    }
                }
                is NearbyShopsState.Error -> {
                    item {
                        ErrorState(
                            message = shopsState.message,
                            onRetryClick = { viewModel.onAction(HomeAction.RetryNearbyShops) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(HallaakTheme.dimensions.space32),
                        )
                    }
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space32))
            }
        }
    }
}


// ── Header Section ────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(
    profileState: ProfileState,
    onNotificationClick: () -> Unit,
    onRetryProfile: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.primary)
            .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 24.dp),
    ) {
        // Top Row: Greeting and Notification
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Greeting
            Column(
                modifier = Modifier.weight(1f),
            ) {
                when (profileState) {
                    is ProfileState.Loading -> {
                        Text(
                            text = "Hello",
                            style = HallaakTheme.typography.headline.large,
                            color = HallaakTheme.colors.background.canvas,
                        )
                    }
                    is ProfileState.Success -> {
                        val firstName = profileState.profile.firstName?.takeIf { it.isNotBlank() } ?: "Guest"
                        Text(
                            text = "مرحباً، $firstName",
                            style = HallaakTheme.typography.headline.large,
                            color = HallaakTheme.colors.background.canvas,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    is ProfileState.Error -> {
                        Text(
                            text = "مرحباً",
                            style = HallaakTheme.typography.headline.large,
                            color = HallaakTheme.colors.background.canvas,
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space4))
                
                Text(
                    text = "ابحث عن أقرب صالون حلاقة",
                    style = HallaakTheme.typography.body.medium,
                    color = HallaakTheme.colors.background.canvas.copy(alpha = 0.85f),
                )
            }

            // Notification button
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(HallaakTheme.colors.background.canvas.copy(alpha = 0.2f)),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_notifications_outlined),
                    contentDescription = "Notifications",
                    tint = HallaakTheme.colors.background.canvas,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

// ── Search Bar ────────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSearchClick),
        shape = HallaakTheme.shapes.input,
        colors = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.background.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = "Search",
                tint = HallaakTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
            
            Text(
                text = "ابحث عن صالونات الحلاقة...",
                style = HallaakTheme.typography.body.medium,
                color = HallaakTheme.colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Service Categories Section ────────────────────────────────────────────────

@Composable
private fun ServiceCategoriesSection(
    categoriesState: CategoriesState,
    onCategoryClick: (ServiceCategory) -> Unit,
    onRetry: () -> Unit,
) {
    when (categoriesState) {
        is CategoriesState.Loading -> {
            // Show loading shimmer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HallaakTheme.dimensions.space16),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }
        is CategoriesState.Success -> {
            if (categoriesState.categories.isNotEmpty()) {
                Column {
                    Text(
                        text = "الخدمات",
                        style = HallaakTheme.typography.title.large,
                        color = HallaakTheme.colors.onSurface,
                        modifier = Modifier.padding(horizontal = HallaakTheme.dimensions.space16),
                    )
                    Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space12))
                    
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = HallaakTheme.dimensions.space16),
                        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
                    ) {
                        items(
                            items = categoriesState.categories,
                            key = { it.id },
                        ) { category ->
                            CategoryChip(
                                category = category,
                                onClick = { onCategoryClick(category) },
                            )
                        }
                    }
                }
            }
        }
        is CategoriesState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HallaakTheme.dimensions.space16),
            ) {
                ErrorState(
                    message = categoriesState.message,
                    onRetryClick = onRetry,
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: ServiceCategory,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Text(
                text = category.name,
                style = HallaakTheme.typography.label.large,
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = HallaakTheme.colors.background.surfaceHigh,
            labelColor = HallaakTheme.colors.onSurface,
        ),
    )
}

// ── Upcoming Appointment Section ──────────────────────────────────────────────

@Composable
private fun UpcomingAppointmentSection(
    appointmentState: UpcomingAppointmentState,
    onViewAppointmentClick: () -> Unit,
    onExploreClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (appointmentState) {
        is UpcomingAppointmentState.Loading -> {
            // Optionally show loading skeleton
        }
        is UpcomingAppointmentState.Success -> {
            appointmentState.appointment?.let { appointment ->
                Column(modifier = modifier) {
                    Text(
                        text = "موعدك القادم",
                        style = HallaakTheme.typography.title.large,
                        color = HallaakTheme.colors.onSurface,
                    )
                    Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space12))
                    
                    UpcomingAppointmentCard(
                        appointment = appointment,
                        onClick = onViewAppointmentClick,
                    )
                }
            }
        }
        is UpcomingAppointmentState.Error -> {
            // Silently fail for upcoming appointment - not critical
        }
    }
}

@Composable
private fun UpcomingAppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.primary.copy(alpha = 0.08f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(HallaakTheme.colors.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_calendar),
                        contentDescription = null,
                        tint = HallaakTheme.colors.primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
                
                // Appointment info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
                ) {
                    appointment.shopName?.let { shopName ->
                        Text(
                            text = shopName,
                            style = HallaakTheme.typography.title.medium,
                            color = HallaakTheme.colors.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    
                    Text(
                        text = formatAppointmentDateTime(appointment.startAt),
                        style = HallaakTheme.typography.body.small,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                    
                    Text(
                        text = "EGP ${appointment.totalPriceEgp}",
                        style = HallaakTheme.typography.label.large,
                        color = HallaakTheme.colors.primary,
                    )
                }
            }
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    titleAr: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = HallaakTheme.typography.title.large,
            color = HallaakTheme.colors.onSurface,
        )
        
        TextButton(onClick = onSeeAllClick) {
            Text(
                text = "عرض الكل",
                style = HallaakTheme.typography.label.large,
                color = HallaakTheme.colors.primary,
            )
        }
    }
}

// ── Error State ───────────────────────────────────────────────────────────────

@Composable
private fun ErrorState(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
    ) {
        Text(
            text = message,
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.status.error,
        )
        HallaakButtonPrimary(
            label = "إعادة المحاولة",
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(
    message: String,
    onActionClick: () -> Unit,
    actionText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
    ) {
        Text(
            text = message,
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.onSurfaceVariant,
        )
        HallaakButtonPrimary(
            label = actionText,
            onClick = onActionClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ── Utility Functions ─────────────────────────────────────────────────────────

private fun formatAppointmentDateTime(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val date = "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}"
    val time = "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    return "$date at $time"
}
