package com.daragly.daraglyhair.feature.bookings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.LocalPadding
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.domain.model.isCancellable
import com.daragly.daraglyhair.feature.bookings.domain.model.isCompleted
import com.daragly.daraglyhair.feature.bookings.ui.components.BookingCard
import com.daragly.daraglyhair.feature.bookings.ui.components.BookingEmptyState
import com.daragly.daraglyhair.feature.bookings.ui.components.BookingErrorState
import com.daragly.daraglyhair.feature.bookings.ui.components.BookingShimmerList
import com.daragly.daraglyhair.feature.bookings.ui.components.BookingTabRow
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.bookings_subtitle
import daraglyhair.shared.generated.resources.bookings_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BookingsScreen(
    viewModel: BookingsViewModel = koinViewModel(),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    // Collect one-shot navigation events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BookingsEvent.NavigateToDetail ->
                    navigator.navigate(IMainGraph.AppointmentDetail(event.appointmentId))
                is BookingsEvent.NavigateToWriteReview ->
                    navigator.navigate(IMainGraph.WriteReview(event.appointmentId, event.shopName))
                is BookingsEvent.NavigateToExplore ->
                    navigator.navigate(IMainGraph.Explore())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        // ── Top bar ──────────────────────────────────────────────────────────
        BookingsTopBar()

        // ── Tab filter row ───────────────────────────────────────────────────
        BookingTabRow(
            selectedTab = state.selectedTab,
            onTabSelected = { viewModel.onAction(BookingsAction.TabSelected(it)) },
        )

        // ── Content ──────────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = HallaakTheme.dimensions.space16,
                bottom = LocalPadding.current.calculateBottomPadding() + HallaakTheme.dimensions.space16,
            ),
        ) {
            when (val content = state.content) {
                is BookingsContentState.Loading -> {
                    item(key = "shimmer") {
                        BookingShimmerList()
                    }
                }

                is BookingsContentState.Success -> {
                    items(
                        items = content.appointments,
                        key = { it.id },
                    ) { appointment ->
                        BookingCard(
                            appointment = appointment,
                            onViewDetailsClick = {
                                viewModel.onAction(BookingsAction.AppointmentClicked(appointment.id))
                            },
                            // Cancel action navigates to detail where the full cancel flow lives
                            onCancelClick = if (appointment.status.isCancellable()) {
                                { viewModel.onAction(BookingsAction.AppointmentClicked(appointment.id)) }
                            } else null,
                            onWriteReviewClick = if (appointment.status.isCompleted()) {
                                { viewModel.onAction(BookingsAction.WriteReviewClicked(appointment.id, appointment.shopName ?: "")) }
                            } else null,
                            modifier = Modifier.padding(
                                horizontal = HallaakTheme.dimensions.space16,
                                vertical = 6.dp,
                            ),
                        )
                    }

                    item(key = "bottom_space") {
                        Spacer(Modifier.height(HallaakTheme.dimensions.space32))
                    }
                }

                is BookingsContentState.Empty -> {
                    item(key = "empty") {
                        BookingEmptyState(
                            tab = state.selectedTab,
                            onExploreClick = { viewModel.onAction(BookingsAction.ExploreClicked) },
                        )
                    }
                }

                is BookingsContentState.Error -> {
                    item(key = "error") {
                        BookingErrorState(
                            message = content.message,
                            onRetry = { viewModel.onAction(BookingsAction.Retry) },
                        )
                    }
                }
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun BookingsTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.primary)
            .padding(
                top = HallaakTheme.dimensions.space48,
                start = HallaakTheme.dimensions.space20,
                end = HallaakTheme.dimensions.space20,
                bottom = HallaakTheme.dimensions.space20,
            ),
    ) {
        Text(
            text = stringResource(Res.string.bookings_title),
            style = HallaakTheme.typography.headline.large,
            color = HallaakTheme.colors.background.canvas,
        )
        Text(
            text = stringResource(Res.string.bookings_subtitle),
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.background.canvas.copy(alpha = 0.85f),
        )
    }
}
