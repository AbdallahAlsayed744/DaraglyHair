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
import com.daragly.daraglyhair.common.ui.location.EnsureLocationPermission
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.home.domain.model.Appointment
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import com.daragly.daraglyhair.feature.home.domain.model.ServiceCategory
import com.daragly.daraglyhair.feature.home.ui.components.BarbershopCard
import com.daragly.daraglyhair.feature.home.ui.components.BarbershopCardShimmer
import com.daragly.daraglyhair.feature.home.ui.components.ServiceCategoriesShimmer
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.cd_notifications
import daraglyhair.shared.generated.resources.cd_search
import daraglyhair.shared.generated.resources.date_at_time
import daraglyhair.shared.generated.resources.greeting
import daraglyhair.shared.generated.resources.greeting_with_name
import daraglyhair.shared.generated.resources.guest
import daraglyhair.shared.generated.resources.home_explore_shops
import daraglyhair.shared.generated.resources.home_nearby_shops
import daraglyhair.shared.generated.resources.home_search_placeholder
import daraglyhair.shared.generated.resources.home_services
import daraglyhair.shared.generated.resources.home_subtitle
import daraglyhair.shared.generated.resources.home_upcoming_appointment
import daraglyhair.shared.generated.resources.ic_calendar
import daraglyhair.shared.generated.resources.ic_location
import daraglyhair.shared.generated.resources.ic_notifications_outlined
import daraglyhair.shared.generated.resources.ic_search
import daraglyhair.shared.generated.resources.price_format
import daraglyhair.shared.generated.resources.retry
import daraglyhair.shared.generated.resources.see_all
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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

    // Ask for location once per entry, then reload nearby shops with the user's real coordinates.
    EnsureLocationPermission { granted ->
        if (granted) viewModel.onAction(HomeAction.RetryNearbyShops)
    }

    // Collect events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.NavigateToNotifications -> {
                    // TODO: Navigate to notifications
                }
                is HomeEvent.NavigateToExplore -> {
                    navigator.navigate(IMainGraph.Explore(query = event.query))
                }
                is HomeEvent.NavigateToShopDetails -> {
                    navigator.navigate(IMainGraph.ShopDetails(event.shopId))
                }
                is HomeEvent.NavigateToAppointmentDetails -> {
                    val appointmentId =
                        (viewModel.state.value.upcomingAppointment as? UpcomingAppointmentState.Success)
                            ?.appointment?.id
                    if (appointmentId != null) {
                        navigator.navigate(IMainGraph.AppointmentDetail(appointmentId))
                    }
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

            // Search Bar — tapping navigates to Explore with focus
            item {
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space16))
                SearchBar(
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
                    title = stringResource(Res.string.home_nearby_shops),
                    onSeeAllClick = { viewModel.onAction(HomeAction.SeeAllShopsClicked) },
                    modifier = Modifier.padding(horizontal = HallaakTheme.dimensions.space16),
                )
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space12))
            }

            // Nearby Barbershops List
            when (val shopsState = state.nearbyShops) {
                is NearbyShopsState.Loading -> {
                    items(3) {
                        BarbershopCardShimmer()
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
                            actionText = stringResource(Res.string.home_explore_shops),
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
                            text = stringResource(Res.string.greeting),
                            style = HallaakTheme.typography.headline.large,
                            color = HallaakTheme.colors.background.canvas,
                        )
                    }
                    is ProfileState.Success -> {
                        val firstName = profileState.profile.firstName?.takeIf { it.isNotBlank() }
                            ?: stringResource(Res.string.guest)
                        Text(
                            text = stringResource(Res.string.greeting_with_name, firstName),
                            style = HallaakTheme.typography.headline.large,
                            color = HallaakTheme.colors.background.canvas,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    is ProfileState.Error -> {
                        Text(
                            text = stringResource(Res.string.greeting),
                            style = HallaakTheme.typography.headline.large,
                            color = HallaakTheme.colors.background.canvas,
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space4))
                
                Text(
                    text = stringResource(Res.string.home_subtitle),
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
                    contentDescription = stringResource(Res.string.cd_notifications),
                    tint = HallaakTheme.colors.background.canvas,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

// ── Search Bar ────────────────────────────────────────────────────────────────
// Read-only on Home — tapping navigates to Explore where real search happens.

@Composable
private fun SearchBar(
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
                contentDescription = stringResource(Res.string.cd_search),
                tint = HallaakTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )

            Text(
                text = stringResource(Res.string.home_search_placeholder),
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
            ServiceCategoriesShimmer()
        }
        is CategoriesState.Success -> {
            if (categoriesState.categories.isNotEmpty()) {
                Column {
                    Text(
                        text = stringResource(Res.string.home_services),
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
                        text = stringResource(Res.string.home_upcoming_appointment),
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
                        text = stringResource(Res.string.price_format, appointment.totalPriceEgp.toString()),
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
                text = stringResource(Res.string.see_all),
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
            label = stringResource(Res.string.retry),
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

@Composable
private fun formatAppointmentDateTime(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val date = "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}"
    val time = "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    return stringResource(Res.string.date_at_time, date, time)
}
