package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
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
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.ui.formatEgp
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopService
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionCard
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionError
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingShimmerLines
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSummaryRow
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingTopBar
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.continue_button
import daraglyhair.shared.generated.resources.duration_minutes
import daraglyhair.shared.generated.resources.estimated_duration_label
import daraglyhair.shared.generated.resources.estimated_total_label
import daraglyhair.shared.generated.resources.price_egp_short
import daraglyhair.shared.generated.resources.select_services_title
import daraglyhair.shared.generated.resources.selection_count_format
import daraglyhair.shared.generated.resources.service_unnamed
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BookingServicesScreen(
    shopId: String,
    viewModel: BookingServicesViewModel = koinViewModel(parameters = { parametersOf(shopId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BookingServicesEvent.NavigateToDateTime ->
                    navigator.navigate(IMainGraph.BookingDateTime(event.shopId))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        BookingTopBar(
            title = stringResource(Res.string.select_services_title),
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
            when (val services = state.services) {
                is SectionState.Loading -> BookingSectionCard(title = stringResource(Res.string.select_services_title)) {
                    BookingShimmerLines(lines = 4, lineHeight = 32.dp)
                }
                is SectionState.Error -> BookingSectionCard(title = stringResource(Res.string.select_services_title)) {
                    BookingSectionError(message = services.message, onRetry = { viewModel.onAction(BookingServicesAction.Retry) })
                }
                is SectionState.Data -> BookingSectionCard(title = stringResource(Res.string.select_services_title)) {
                    services.model.forEachIndexed { index, service ->
                        SelectableServiceRow(
                            service = service,
                            checked = state.selectedServiceIds.contains(service.shopServiceId),
                            onToggle = { viewModel.onAction(BookingServicesAction.ToggleService(service)) },
                        )
                        if (index != services.model.lastIndex) {
                            HorizontalDivider(
                                color = HallaakTheme.colors.stroke.copy(alpha = 0.5f),
                                modifier = Modifier.padding(vertical = HallaakTheme.dimensions.space8),
                            )
                        }
                    }
                }
            }

            // ── Estimate summary ─────────────────────────────────────────────
            if (state.selectedServiceIds.isNotEmpty()) {
                val selected = state.selectedServices
                BookingSectionCard(title = stringResource(Res.string.estimated_total_label)) {
                    Text(
                        text = stringResource(Res.string.selection_count_format, selected.size.toString()),
                        style = HallaakTheme.typography.label.large,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                    BookingSummaryRow(
                        label = stringResource(Res.string.estimated_duration_label),
                        value = stringResource(
                            Res.string.duration_minutes,
                            selected.sumOf { it.durationMinutes }.toString(),
                        ),
                    )
                    Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                    BookingSummaryRow(
                        label = stringResource(Res.string.estimated_total_label),
                        value = formatEgp(selected.sumOf { it.priceEgp }),
                        emphasizeValue = true,
                    )
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
                onClick = { viewModel.onAction(BookingServicesAction.ContinueClicked) },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = state.selectedServiceIds.isNotEmpty(),
            )
        }
    }
}

@Composable
private fun SelectableServiceRow(
    service: ShopService,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = HallaakTheme.dimensions.space4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service.name ?: stringResource(Res.string.service_unnamed),
                style = HallaakTheme.typography.title.large,
                color = HallaakTheme.colors.onSurface,
            )
            Text(
                text = stringResource(Res.string.duration_minutes, service.durationMinutes.toString()),
                style = HallaakTheme.typography.label.large,
                color = HallaakTheme.colors.onSurfaceVariant,
            )
        }
        Text(
            text = stringResource(Res.string.price_egp_short, service.priceEgp.toString()),
            style = HallaakTheme.typography.headline.extraSmall,
            color = HallaakTheme.colors.primary,
        )
    }
}
