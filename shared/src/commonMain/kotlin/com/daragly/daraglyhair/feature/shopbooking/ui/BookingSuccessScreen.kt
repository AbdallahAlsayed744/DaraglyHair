package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGhost
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGold
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.ui.components.BookingStatusBadge
import com.daragly.daraglyhair.feature.bookings.ui.formatBookingDateTimeShort
import com.daragly.daraglyhair.feature.bookings.ui.formatEgp
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingNoticeBanner
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionCard
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionError
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingShimmerLines
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSummaryRow
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.appointment_reference_label
import daraglyhair.shared.generated.resources.back_to_shop
import daraglyhair.shared.generated.resources.booking_success_subtitle
import daraglyhair.shared.generated.resources.booking_success_title
import daraglyhair.shared.generated.resources.confirmed_details_title
import daraglyhair.shared.generated.resources.details_unavailable_notice
import daraglyhair.shared.generated.resources.deposit_label
import daraglyhair.shared.generated.resources.go_to_bookings
import daraglyhair.shared.generated.resources.label_date
import daraglyhair.shared.generated.resources.label_total
import daraglyhair.shared.generated.resources.section_shop
import daraglyhair.shared.generated.resources.view_details
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BookingSuccessScreen(
    shopId: String,
    appointmentId: String,
    viewModel: BookingSuccessViewModel = koinViewModel(parameters = { parametersOf(shopId, appointmentId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BookingSuccessEvent.NavigateToAppointmentDetail ->
                    navigator.navigate(IMainGraph.AppointmentDetail(event.appointmentId))
                is BookingSuccessEvent.OpenBookingsTab ->
                    // BookingsViewModel is created fresh on this route → list reloads.
                    navigator.navigate(IMainGraph.Bookings)
                is BookingSuccessEvent.NavigateToShopDetails ->
                    navigator.navigate(IMainGraph.ShopDetails(event.shopId))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(HallaakTheme.dimensions.space16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
        ) {
            Spacer(Modifier.height(HallaakTheme.dimensions.space32))

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        color = HallaakTheme.colors.status.success.copy(alpha = 0.12f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = HallaakTheme.colors.status.success,
                    modifier = Modifier.size(48.dp),
                )
            }

            Text(
                text = stringResource(Res.string.booking_success_title),
                style = HallaakTheme.typography.headline.small,
                color = HallaakTheme.colors.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.booking_success_subtitle),
                style = HallaakTheme.typography.body.small,
                color = HallaakTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            // ── Appointment reference ────────────────────────────────────────
            BookingSectionCard(title = stringResource(Res.string.appointment_reference_label)) {
                Text(
                    text = state.appointmentId,
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.onSurface,
                )
            }

            // ── Backend-confirmed details (authoritative) ────────────────────
            when (val details = state.details) {
                is SectionState.Loading -> BookingSectionCard(title = stringResource(Res.string.confirmed_details_title)) {
                    BookingShimmerLines(lines = 4)
                }
                is SectionState.Error -> BookingSectionCard(title = stringResource(Res.string.confirmed_details_title)) {
                    BookingNoticeBanner(
                        message = stringResource(Res.string.details_unavailable_notice),
                        isError = false,
                    )
                    Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                    BookingSectionError(message = "", onRetry = { viewModel.onAction(BookingSuccessAction.RetryDetails) })
                }
                is SectionState.Data -> ConfirmedDetailsCard(details.model)
            }
        }

        // ── Actions ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HallaakTheme.colors.background.surface)
                .padding(HallaakTheme.dimensions.space16),
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
        ) {
            HallaakButtonGold(
                label = stringResource(Res.string.go_to_bookings),
                onClick = { viewModel.onAction(BookingSuccessAction.OpenBookingsClicked) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8)) {
                HallaakButtonGhost(
                    label = stringResource(Res.string.view_details),
                    onClick = { viewModel.onAction(BookingSuccessAction.ViewDetailsClicked) },
                    modifier = Modifier.weight(1f),
                )
                HallaakButtonGhost(
                    label = stringResource(Res.string.back_to_shop),
                    onClick = { viewModel.onAction(BookingSuccessAction.BackToShopClicked) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// ── Authoritative appointment details ─────────────────────────────────────────

@Composable
private fun ConfirmedDetailsCard(appointment: BookingAppointment) {
    BookingSectionCard(title = stringResource(Res.string.confirmed_details_title)) {
        BookingSummaryRow(
            label = stringResource(Res.string.section_shop),
            value = appointment.shopName ?: "",
        )
        Spacer(Modifier.height(HallaakTheme.dimensions.space8))
        BookingSummaryRow(
            label = stringResource(Res.string.label_date),
            value = formatBookingDateTimeShort(appointment.startAt),
        )
        Spacer(Modifier.height(HallaakTheme.dimensions.space8))
        BookingSummaryRow(
            label = stringResource(Res.string.label_total),
            value = formatEgp(appointment.totalPriceEgp),
            emphasizeValue = true,
        )

        appointment.depositAmountEgp?.takeIf { it > 0 }?.let { deposit ->
            Spacer(Modifier.height(HallaakTheme.dimensions.space8))
            BookingSummaryRow(
                label = stringResource(Res.string.deposit_label),
                value = formatEgp(deposit),
            )
        }

        HorizontalDivider(
            color = HallaakTheme.colors.stroke.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = HallaakTheme.dimensions.space12),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
        ) {
            Text(
                text = stringResource(Res.string.confirmed_details_title),
                style = HallaakTheme.typography.label.large,
                color = HallaakTheme.colors.onSurfaceVariant,
            )
            BookingStatusBadge(status = appointment.status)
        }
    }
}
