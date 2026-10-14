package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGold
import com.daragly.daraglyhair.designsystem.components.textfield.HallaakTextField
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.ui.formatBookingDateTimeShort
import com.daragly.daraglyhair.feature.bookings.ui.formatEgp
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingNoticeBanner
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionCard
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSummaryRow
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingTopBar
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.any_barber_label
import daraglyhair.shared.generated.resources.backend_validation_notice
import daraglyhair.shared.generated.resources.barber_row_fallback
import daraglyhair.shared.generated.resources.booking_conflict_message
import daraglyhair.shared.generated.resources.choose_another_time
import daraglyhair.shared.generated.resources.confirm_booking_button
import daraglyhair.shared.generated.resources.duration_minutes
import daraglyhair.shared.generated.resources.estimated_duration_label
import daraglyhair.shared.generated.resources.label_date
import daraglyhair.shared.generated.resources.label_total
import daraglyhair.shared.generated.resources.notes_optional_hint
import daraglyhair.shared.generated.resources.price_egp_short
import daraglyhair.shared.generated.resources.review_title
import daraglyhair.shared.generated.resources.section_barbers
import daraglyhair.shared.generated.resources.section_notes
import daraglyhair.shared.generated.resources.section_services
import daraglyhair.shared.generated.resources.section_shop
import daraglyhair.shared.generated.resources.service_unnamed
import daraglyhair.shared.generated.resources.ok
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BookingReviewScreen(
    shopId: String,
    viewModel: BookingReviewViewModel = koinViewModel(parameters = { parametersOf(shopId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BookingReviewEvent.NavigateToSuccess ->
                    navigator.navigate(IMainGraph.BookingSuccess(event.shopId, event.appointmentId))
                is BookingReviewEvent.NavigateToDateTime ->
                    // Push a fresh step so available slots are re-fetched from the API.
                    navigator.navigate(IMainGraph.BookingDateTime(shopId))
            }
        }
    }

    val draft = state.draft

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        BookingTopBar(
            title = stringResource(Res.string.review_title),
            onBack = {
                scope.launch {
                    navigator.navigateUp()
                }
            },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(HallaakTheme.dimensions.space16),
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
        ) {
            // ── Conflict banner (409) — never show success on failure ────────
            if (state.hasConflict) {
                BookingNoticeBanner(message = stringResource(Res.string.booking_conflict_message))
                HallaakButtonGold(
                    label = stringResource(Res.string.choose_another_time),
                    onClick = { viewModel.onAction(BookingReviewAction.ChooseAnotherTimeClicked) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // ── Generic submission error ─────────────────────────────────────
            state.errorMessage?.let { message ->
                Column(verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4)) {
                    BookingNoticeBanner(message = message)
                    Text(
                        text = stringResource(Res.string.ok),
                        style = HallaakTheme.typography.label.large,
                        color = HallaakTheme.colors.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { viewModel.onAction(BookingReviewAction.DismissMessages) }
                            .padding(HallaakTheme.dimensions.space4),
                    )
                }
            }

            // ── Booking summary ──────────────────────────────────────────────
            BookingSectionCard(title = stringResource(Res.string.review_title)) {
                BookingSummaryRow(
                    label = stringResource(Res.string.section_shop),
                    value = draft.shopName ?: "",
                )
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                BookingSummaryRow(
                    label = stringResource(Res.string.section_barbers),
                    value = draft.barberSpecialization?.takeIf { it.isNotBlank() }
                        ?: if (draft.barberId != null) {
                            stringResource(Res.string.barber_row_fallback)
                        } else {
                            stringResource(Res.string.any_barber_label)
                        },
                )
                draft.slot?.let { slot ->
                    Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                    BookingSummaryRow(
                        label = stringResource(Res.string.label_date),
                        value = formatBookingDateTimeShort(slot.startAt),
                    )
                }

                HorizontalDivider(
                    color = HallaakTheme.colors.stroke.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = HallaakTheme.dimensions.space12),
                )

                draft.selectedServices.forEachIndexed { index, service ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = service.name ?: stringResource(Res.string.service_unnamed),
                            style = HallaakTheme.typography.body.small,
                            color = HallaakTheme.colors.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = stringResource(Res.string.price_egp_short, service.priceEgp.toString()),
                            style = HallaakTheme.typography.body.small,
                            color = HallaakTheme.colors.onSurface,
                        )
                    }
                    if (index != draft.selectedServices.lastIndex) {
                        Spacer(Modifier.height(HallaakTheme.dimensions.space4))
                    }
                }

                Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                BookingSummaryRow(
                    label = stringResource(Res.string.estimated_duration_label),
                    value = stringResource(
                        Res.string.duration_minutes,
                        draft.estimatedDurationMinutes.toString(),
                    ),
                )
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                BookingSummaryRow(
                    label = stringResource(Res.string.label_total),
                    value = formatEgp(draft.estimatedTotalEgp),
                    emphasizeValue = true,
                )

                Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                Text(
                    text = stringResource(Res.string.backend_validation_notice),
                    style = HallaakTheme.typography.label.large,
                    color = HallaakTheme.colors.onSurfaceVariant,
                )
            }

            // ── Optional notes ───────────────────────────────────────────────
            BookingSectionCard(title = stringResource(Res.string.section_notes)) {
                HallaakTextField(
                    value = draft.notes,
                    onValueChange = { viewModel.onAction(BookingReviewAction.NotesChanged(it)) },
                    placeholder = stringResource(Res.string.notes_optional_hint),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // ── Confirm ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HallaakTheme.colors.background.surface)
                .padding(HallaakTheme.dimensions.space16),
        ) {
            if (state.isSubmitting) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = HallaakTheme.colors.primary,
                        modifier = Modifier.size(36.dp),
                    )
                }
            } else {
                HallaakButtonGold(
                    label = stringResource(Res.string.confirm_booking_button),
                    onClick = { viewModel.onAction(BookingReviewAction.ConfirmClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = state.canConfirm,
                )
            }
        }
    }
}
