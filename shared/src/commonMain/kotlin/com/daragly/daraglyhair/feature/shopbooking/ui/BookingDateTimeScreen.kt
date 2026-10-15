package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGold
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.ui.toBookingTimeString
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingSlot
import com.daragly.daraglyhair.feature.shopbooking.domain.model.TransportationMode
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingNoticeBanner
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionCard
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionError
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingTopBar
import com.daragly.daraglyhair.designsystem.util.HallaakShimmerBox
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.continue_button
import daraglyhair.shared.generated.resources.month_1
import daraglyhair.shared.generated.resources.month_10
import daraglyhair.shared.generated.resources.month_11
import daraglyhair.shared.generated.resources.month_12
import daraglyhair.shared.generated.resources.month_2
import daraglyhair.shared.generated.resources.month_3
import daraglyhair.shared.generated.resources.month_4
import daraglyhair.shared.generated.resources.month_5
import daraglyhair.shared.generated.resources.month_6
import daraglyhair.shared.generated.resources.month_7
import daraglyhair.shared.generated.resources.month_8
import daraglyhair.shared.generated.resources.month_9
import daraglyhair.shared.generated.resources.no_slots_subtitle
import daraglyhair.shared.generated.resources.no_slots_title
import daraglyhair.shared.generated.resources.select_date_time_title
import daraglyhair.shared.generated.resources.transport_mode_car
import daraglyhair.shared.generated.resources.transport_mode_hint
import daraglyhair.shared.generated.resources.transport_mode_public_transit
import daraglyhair.shared.generated.resources.transport_mode_title
import daraglyhair.shared.generated.resources.transport_mode_walking
import daraglyhair.shared.generated.resources.travel_time_format
import daraglyhair.shared.generated.resources.weekday_fri
import daraglyhair.shared.generated.resources.weekday_mon
import daraglyhair.shared.generated.resources.weekday_sat
import daraglyhair.shared.generated.resources.weekday_sun
import daraglyhair.shared.generated.resources.weekday_thu
import daraglyhair.shared.generated.resources.weekday_tue
import daraglyhair.shared.generated.resources.weekday_wed
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

private val weekdayResources = listOf(
    Res.string.weekday_mon,
    Res.string.weekday_tue,
    Res.string.weekday_wed,
    Res.string.weekday_thu,
    Res.string.weekday_fri,
    Res.string.weekday_sat,
    Res.string.weekday_sun,
)

private val monthResources = listOf(
    Res.string.month_1, Res.string.month_2, Res.string.month_3, Res.string.month_4,
    Res.string.month_5, Res.string.month_6, Res.string.month_7, Res.string.month_8,
    Res.string.month_9, Res.string.month_10, Res.string.month_11, Res.string.month_12,
)

@OptIn(KoinExperimentalAPI::class, ExperimentalLayoutApi::class)
@Composable
fun BookingDateTimeScreen(
    shopId: String,
    viewModel: BookingDateTimeViewModel = koinViewModel(parameters = { parametersOf(shopId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BookingDateTimeEvent.NavigateToReview ->
                    navigator.navigate(IMainGraph.BookingReview(event.shopId))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        BookingTopBar(
            title = stringResource(Res.string.select_date_time_title),
            onBack = {
                scope.launch {
                    navigator.navigateUp()
                }
            },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Date strip ───────────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(
                    horizontal = HallaakTheme.dimensions.space16,
                    vertical = HallaakTheme.dimensions.space16,
                ),
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
            ) {
                items(state.dates, key = { it.toString() }) { date ->
                    DateChip(
                        date = date,
                        selected = state.selectedDate == date,
                        onClick = { viewModel.onAction(BookingDateTimeAction.DateSelected(date)) },
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HallaakTheme.dimensions.space16),
                verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
            ) {
                // ── Transportation mode (required before slots load) ────────
                BookingSectionCard(title = stringResource(Res.string.transport_mode_title)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                    ) {
                        TransportationMode.entries.forEach { mode ->
                            TransportModeChip(
                                label = stringResource(mode.labelRes()),
                                icon = mode.icon,
                                selected = state.transportationMode == mode,
                                onClick = {
                                    viewModel.onAction(BookingDateTimeAction.TransportationModeSelected(mode))
                                },
                            )
                        }
                    }
                }

                when (state.transportationMode) {
                    null -> BookingNoticeBanner(
                        message = stringResource(Res.string.transport_mode_hint),
                        isError = false,
                    )

                    else -> when (val slots = state.slots) {
                        is SectionState.Loading -> BookingSectionCard(title = stringResource(Res.string.select_date_time_title)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                                verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                            ) {
                                repeat(6) {
                                    HallaakShimmerBox(
                                        modifier = Modifier
                                            .width(88.dp)
                                            .height(36.dp),
                                    )
                                }
                            }
                        }

                        is SectionState.Error -> BookingSectionCard(title = stringResource(Res.string.select_date_time_title)) {
                            BookingSectionError(
                                message = slots.message,
                                onRetry = { viewModel.onAction(BookingDateTimeAction.RetrySlots) },
                            )
                        }

                        is SectionState.Data -> {
                            val offer = slots.model
                            BookingSectionCard(title = stringResource(Res.string.select_date_time_title)) {
                                offer.estimatedTravelMinutes?.takeIf { it > 0 }?.let { minutes ->
                                    BookingNoticeBanner(
                                        message = stringResource(Res.string.travel_time_format, minutes.toString()),
                                        isError = false,
                                    )
                                    Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                                }

                                if (offer.slots.isEmpty()) {
                                    SlotsEmptyState()
                                } else {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                                        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                                    ) {
                                        offer.slots.forEach { slot ->
                                            SlotChip(
                                                slot = slot,
                                                selected = state.selectedSlotStart == slot.startAt,
                                                onClick = {
                                                    viewModel.onAction(BookingDateTimeAction.SlotSelected(slot.startAt))
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HallaakTheme.colors.background.surface)
                .padding(HallaakTheme.dimensions.space16),
        ) {
            HallaakButtonGold(
                label = stringResource(Res.string.continue_button),
                onClick = { viewModel.onAction(BookingDateTimeAction.ContinueClicked) },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = state.selectedSlotStart != null,
            )
        }
    }
}

// ── Date chip ─────────────────────────────────────────────────────────────────

@Composable
private fun DateChip(
    date: LocalDate,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (selected) {
        HallaakTheme.colors.primary
    } else {
        HallaakTheme.colors.background.surface
    }
    val contentColor = if (selected) {
        HallaakTheme.colors.background.canvas
    } else {
        HallaakTheme.colors.onSurface
    }

    Card(
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        shape = HallaakTheme.shapes.card,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = HallaakTheme.dimensions.space12),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(weekdayResources[date.dayOfWeek.ordinal]),
                style = HallaakTheme.typography.label.extraSmall,
                color = contentColor.copy(alpha = 0.8f),
            )
            Spacer(Modifier.height(HallaakTheme.dimensions.space4))
            Text(
                text = date.dayOfMonth.toString(),
                style = HallaakTheme.typography.title.large,
                color = contentColor,
            )
            Text(
                text = stringResource(monthResources[date.monthNumber - 1]),
                style = HallaakTheme.typography.label.extraSmall,
                color = contentColor.copy(alpha = 0.8f),
            )
        }
    }
}

// ── Slot chip ─────────────────────────────────────────────────────────────────

@Composable
private fun SlotChip(
    slot: BookingSlot,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val enabled = slot.isReachable
    val containerColor = when {
        selected && enabled -> HallaakTheme.colors.primary
        else -> HallaakTheme.colors.background.surfaceHigh
    }
    val contentColor = when {
        selected && enabled -> HallaakTheme.colors.background.canvas
        enabled -> HallaakTheme.colors.onSurface
        else -> HallaakTheme.colors.onSurfaceVariant.copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .height(36.dp)
            .background(color = containerColor, shape = HallaakTheme.shapes.pill)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = HallaakTheme.dimensions.space16),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = slot.startAt.toBookingTimeString(),
            style = HallaakTheme.typography.label.large,
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Transportation mode ───────────────────────────────────────────────────────

private fun TransportationMode.labelRes(): StringResource = when (this) {
    TransportationMode.WALKING -> Res.string.transport_mode_walking
    TransportationMode.CAR -> Res.string.transport_mode_car
    TransportationMode.PUBLIC_TRANSIT -> Res.string.transport_mode_public_transit
}

private val TransportationMode.icon: ImageVector
    get() = when (this) {
        TransportationMode.WALKING -> Icons.Outlined.DirectionsWalk
        TransportationMode.CAR -> Icons.Outlined.DirectionsCar
        TransportationMode.PUBLIC_TRANSIT -> Icons.Outlined.DirectionsBus
    }

@Composable
private fun TransportModeChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (selected) {
        HallaakTheme.colors.primary
    } else {
        HallaakTheme.colors.background.surfaceHigh
    }
    val contentColor = if (selected) {
        HallaakTheme.colors.background.canvas
    } else {
        HallaakTheme.colors.onSurface
    }

    Box(
        modifier = Modifier
            .height(36.dp)
            .background(color = containerColor, shape = HallaakTheme.shapes.pill)
            .clickable(onClick = onClick)
            .padding(horizontal = HallaakTheme.dimensions.space16),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                style = HallaakTheme.typography.label.large,
                color = contentColor,
            )
        }
    }
}

// ── Empty slots state ─────────────────────────────────────────────────────────

@Composable
private fun SlotsEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HallaakTheme.dimensions.space24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
    ) {
        Icon(
            imageVector = Icons.Outlined.EventBusy,
            contentDescription = null,
            tint = HallaakTheme.colors.onSurfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = stringResource(Res.string.no_slots_title),
            style = HallaakTheme.typography.title.large,
            color = HallaakTheme.colors.onSurface,
        )
        Text(
            text = stringResource(Res.string.no_slots_subtitle),
            style = HallaakTheme.typography.body.small,
            color = HallaakTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
