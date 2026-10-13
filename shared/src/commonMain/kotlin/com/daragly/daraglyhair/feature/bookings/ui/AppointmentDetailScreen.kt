package com.daragly.daraglyhair.feature.bookings.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonSecondary
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingStatus
import com.daragly.daraglyhair.feature.bookings.domain.model.DepositStatus
import com.daragly.daraglyhair.feature.bookings.domain.model.isCancellable
import com.daragly.daraglyhair.feature.bookings.ui.components.BookingStatusBadge
import com.daragly.daraglyhair.feature.bookings.ui.components.CancellationConfirmDialog
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.appointment_detail_error_title
import daraglyhair.shared.generated.resources.appointment_detail_title
import daraglyhair.shared.generated.resources.back
import daraglyhair.shared.generated.resources.back_to_bookings
import daraglyhair.shared.generated.resources.cancel_booking
import daraglyhair.shared.generated.resources.cancellation_success
import daraglyhair.shared.generated.resources.cd_copy_number
import daraglyhair.shared.generated.resources.deposit_label
import daraglyhair.shared.generated.resources.deposit_label_forfeited
import daraglyhair.shared.generated.resources.deposit_label_paid
import daraglyhair.shared.generated.resources.deposit_label_pending
import daraglyhair.shared.generated.resources.deposit_label_refunded
import daraglyhair.shared.generated.resources.label_date
import daraglyhair.shared.generated.resources.label_time
import daraglyhair.shared.generated.resources.label_total
import daraglyhair.shared.generated.resources.label_value
import daraglyhair.shared.generated.resources.ok
import daraglyhair.shared.generated.resources.retry
import daraglyhair.shared.generated.resources.section_booking_id
import daraglyhair.shared.generated.resources.section_date_time
import daraglyhair.shared.generated.resources.section_notes
import daraglyhair.shared.generated.resources.section_payment
import daraglyhair.shared.generated.resources.section_shop
import daraglyhair.shared.generated.resources.time_range
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppointmentDetailScreen(
    appointmentId: String,
    viewModel: AppointmentDetailViewModel = koinViewModel(parameters = { parametersOf(appointmentId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    // Collect one-shot navigation events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is AppointmentDetailEvent.NavigateBack,
                is AppointmentDetailEvent.NavigateBackAndRefresh ->
                    navigator.navigateUp()
            }
        }
    }

    // Cancellation confirm dialog
    if (state.showCancellationDialog) {
        CancellationConfirmDialog(
            onDismiss = { viewModel.onAction(AppointmentDetailAction.DismissCancellationDialog) },
            onConfirm = { viewModel.onAction(AppointmentDetailAction.ConfirmCancellation) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        // ── Top bar ──────────────────────────────────────────────────────────
        DetailTopBar(
            onBack = { viewModel.onAction(AppointmentDetailAction.BackClicked) },
        )

        // ── Scrollable content ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            when (val content = state.content) {
                is AppointmentDetailContentState.Loading -> {
                    DetailLoadingState()
                }

                is AppointmentDetailContentState.Error -> {
                    DetailErrorState(
                        message = content.message,
                        onRetry = { viewModel.onAction(AppointmentDetailAction.Retry) },
                        onBack = { viewModel.onAction(AppointmentDetailAction.BackClicked) },
                    )
                }

                is AppointmentDetailContentState.Success -> {
                    val appointment = content.appointment

                    // ── Cancellation result banners ──────────────────────────
                    when (val cancelState = state.cancellationState) {
                        is CancellationState.Success -> {
                            CancellationResultBanner(
                                isSuccess = true,
                                message = stringResource(Res.string.cancellation_success),
                                onDismiss = { viewModel.onAction(AppointmentDetailAction.DismissCancellationResult) },
                            )
                        }
                        is CancellationState.Failure -> {
                            CancellationResultBanner(
                                isSuccess = false,
                                message = cancelState.message,
                                onDismiss = { viewModel.onAction(AppointmentDetailAction.DismissCancellationResult) },
                            )
                        }
                        else -> {}
                    }

                    // ── Appointment details content ──────────────────────────
                    AppointmentDetailContent(
                        appointment = appointment,
                        cancellationInProgress = state.cancellationState is CancellationState.InProgress,
                        onCancelClick = { viewModel.onAction(AppointmentDetailAction.CancelClicked) },
                    )
                }
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DetailTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.primary)
            .padding(
                top = HallaakTheme.dimensions.space48,
                start = HallaakTheme.dimensions.space8,
                end = HallaakTheme.dimensions.space20,
                bottom = HallaakTheme.dimensions.space16,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(Res.string.back),
                tint = HallaakTheme.colors.background.canvas,
            )
        }
        Spacer(Modifier.width(HallaakTheme.dimensions.space8))
        Text(
            text = stringResource(Res.string.appointment_detail_title),
            style = HallaakTheme.typography.headline.small,
            color = HallaakTheme.colors.background.canvas,
        )
    }
}

// ── Loading ───────────────────────────────────────────────────────────────────

@Composable
private fun DetailLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = HallaakTheme.colors.primary)
    }
}

// ── Error ─────────────────────────────────────────────────────────────────────

@Composable
private fun DetailErrorState(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HallaakTheme.dimensions.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
    ) {
        Spacer(Modifier.height(HallaakTheme.dimensions.space32))

        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = HallaakTheme.colors.status.error.copy(alpha = 0.45f),
            modifier = Modifier.size(72.dp),
        )

        Text(
            text = stringResource(Res.string.appointment_detail_error_title),
            style = HallaakTheme.typography.headline.extraSmall,
            color = HallaakTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )

        HallaakButtonPrimary(
            label = stringResource(Res.string.retry),
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        )

        TextButton(onClick = onBack) {
            Text(
                text = stringResource(Res.string.back_to_bookings),
                color = HallaakTheme.colors.onSurfaceVariant,
                style = HallaakTheme.typography.body.medium,
            )
        }
    }
}

// ── Cancellation result banner ────────────────────────────────────────────────

@Composable
private fun CancellationResultBanner(
    isSuccess: Boolean,
    message: String,
    onDismiss: () -> Unit,
) {
    val bgColor = if (isSuccess) HallaakTheme.colors.status.success else HallaakTheme.colors.status.error
    val icon = if (isSuccess) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor.copy(alpha = 0.12f))
            .padding(horizontal = HallaakTheme.dimensions.space16, vertical = HallaakTheme.dimensions.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = bgColor,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = message,
            style = HallaakTheme.typography.body.small,
            color = bgColor,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onDismiss) {
            Text(
                text = stringResource(Res.string.ok),
                color = bgColor,
                style = HallaakTheme.typography.label.large,
            )
        }
    }
}

// ── Main detail content ───────────────────────────────────────────────────────

@Composable
private fun AppointmentDetailContent(
    appointment: BookingAppointment,
    cancellationInProgress: Boolean,
    onCancelClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HallaakTheme.dimensions.space16),
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
    ) {
        // ── Status hero card ──────────────────────────────────────────────────
        StatusHeroCard(status = appointment.status)

        // ── Shop info section ─────────────────────────────────────────────────
        appointment.shopName?.let { shopName ->
            DetailSection(title = stringResource(Res.string.section_shop)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = HallaakTheme.colors.primary.copy(alpha = 0.1f),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Store,
                            contentDescription = null,
                            tint = HallaakTheme.colors.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Text(
                        text = shopName,
                        style = HallaakTheme.typography.title.extraLarge,
                        color = HallaakTheme.colors.onSurface,
                    )
                }
            }
        }

        // ── Date & time section ───────────────────────────────────────────────
        DetailSection(title = stringResource(Res.string.section_date_time)) {
            DetailInfoRow(
                icon = Icons.Outlined.CalendarMonth,
                label = stringResource(Res.string.label_date),
                value = appointment.startAt.toBookingDateString(),
            )
            Spacer(Modifier.height(HallaakTheme.dimensions.space8))
            DetailInfoRow(
                icon = Icons.Outlined.Schedule,
                label = stringResource(Res.string.label_time),
                value = stringResource(
                    Res.string.time_range,
                    appointment.startAt.toBookingTimeString(),
                    appointment.endAt.toBookingTimeString(),
                ),
            )
        }

        // ── Price section ─────────────────────────────────────────────────────
        DetailSection(title = stringResource(Res.string.section_payment)) {
            PriceRow(
                label = stringResource(Res.string.label_total),
                value = formatEgp(appointment.totalPriceEgp),
            )

            appointment.depositAmountEgp?.let { deposit ->
                if (deposit > 0) {
                    Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                    val depositLabel = when (appointment.depositStatus) {
                        DepositStatus.PAID      -> stringResource(Res.string.deposit_label_paid)
                        DepositStatus.PENDING   -> stringResource(Res.string.deposit_label_pending)
                        DepositStatus.FORFEITED -> stringResource(Res.string.deposit_label_forfeited)
                        DepositStatus.REFUNDED  -> stringResource(Res.string.deposit_label_refunded)
                        DepositStatus.NONE      -> stringResource(Res.string.deposit_label)
                    }
                    PriceRow(
                        label = depositLabel,
                        value = formatEgp(deposit),
                        isSecondary = true,
                    )
                }
            }
        }

        // ── Notes section — only if notes are present ─────────────────────────
        appointment.notes?.let { notes ->
            if (notes.isNotBlank()) {
                DetailSection(title = stringResource(Res.string.section_notes)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notes,
                            contentDescription = null,
                            tint = HallaakTheme.colors.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = notes,
                            style = HallaakTheme.typography.body.small,
                            color = HallaakTheme.colors.onSurface,
                        )
                    }
                }
            }
        }

        // ── Appointment reference ─────────────────────────────────────────────
        DetailSection(title = stringResource(Res.string.section_booking_id)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
            ) {
                Text(
                    text = appointment.id.take(18) + if (appointment.id.length > 18) "…" else "",
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = stringResource(Res.string.cd_copy_number),
                    tint = HallaakTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        // ── Cancel action ─────────────────────────────────────────────────────
        if (appointment.status.isCancellable()) {
            Spacer(Modifier.height(HallaakTheme.dimensions.space8))

            if (cancellationInProgress) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = HallaakTheme.colors.status.error,
                        modifier = Modifier.size(36.dp),
                    )
                }
            } else {
                HallaakButtonSecondary(
                    label = stringResource(Res.string.cancel_booking),
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(HallaakTheme.dimensions.space32))
    }
}

// ── Status hero card ──────────────────────────────────────────────────────────

@Composable
private fun StatusHeroCard(status: BookingStatus) {
    val (statusIcon, statusColor) = when (status) {
        BookingStatus.PENDING             -> Icons.Outlined.Schedule to HallaakTheme.colors.status.pending
        BookingStatus.CONFIRMED           -> Icons.Outlined.CheckCircle to HallaakTheme.colors.status.info
        BookingStatus.ARRIVED             -> Icons.Outlined.CheckCircle to HallaakTheme.colors.status.success
        BookingStatus.COMPLETED           -> Icons.Outlined.CheckCircle to HallaakTheme.colors.status.success
        BookingStatus.CANCELLED_BY_CUSTOMER,
        BookingStatus.CANCELLED_BY_SHOP   -> Icons.Outlined.Cancel to HallaakTheme.colors.status.error
        BookingStatus.NO_SHOW             -> Icons.Outlined.Cancel to HallaakTheme.colors.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = HallaakTheme.shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.08f),
        ),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(28.dp),
            )
            BookingStatusBadge(status = status)
        }
    }
}

// ── Section wrapper ───────────────────────────────────────────────────────────

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = HallaakTheme.shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.background.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space16),
        ) {
            Text(
                text = title,
                style = HallaakTheme.typography.title.small,
                color = HallaakTheme.colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
            content()
        }
    }
}

// ── Row helpers ───────────────────────────────────────────────────────────────

@Composable
private fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HallaakTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(Res.string.label_value, label, value),
            style = HallaakTheme.typography.body.small,
            color = HallaakTheme.colors.onSurface,
        )
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    isSecondary: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = if (isSecondary) HallaakTheme.typography.body.small else HallaakTheme.typography.body.medium,
            color = if (isSecondary) HallaakTheme.colors.onSurfaceVariant else HallaakTheme.colors.onSurface,
        )
        Text(
            text = value,
            style = if (isSecondary) HallaakTheme.typography.label.large else HallaakTheme.typography.title.extraLarge,
            color = if (isSecondary) HallaakTheme.colors.onSurfaceVariant else HallaakTheme.colors.primary,
        )
    }
}
