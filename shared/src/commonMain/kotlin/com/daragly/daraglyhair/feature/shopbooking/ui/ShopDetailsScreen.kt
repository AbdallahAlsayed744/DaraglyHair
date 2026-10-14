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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGold
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BarberProfile
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopBarber
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopService
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingInfoRow
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingNoticeBanner
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionCard
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionError
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingShimmerLines
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingTopBar
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.barber_not_bookable_badge
import daraglyhair.shared.generated.resources.barber_row_fallback
import daraglyhair.shared.generated.resources.book_appointment
import daraglyhair.shared.generated.resources.booking_unavailable_notice
import daraglyhair.shared.generated.resources.duration_minutes
import daraglyhair.shared.generated.resources.label_address
import daraglyhair.shared.generated.resources.label_phone
import daraglyhair.shared.generated.resources.price_egp_short
import daraglyhair.shared.generated.resources.section_about
import daraglyhair.shared.generated.resources.section_barbers
import daraglyhair.shared.generated.resources.section_services
import daraglyhair.shared.generated.resources.service_unnamed
import daraglyhair.shared.generated.resources.shop_details_title
import daraglyhair.shared.generated.resources.shop_inactive_notice
import daraglyhair.shared.generated.resources.verified_badge
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ShopDetailsScreen(
    shopId: String,
    viewModel: ShopDetailsViewModel = koinViewModel(parameters = { parametersOf(shopId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ShopDetailsEvent.NavigateToBarber ->
                    navigator.navigate(IMainGraph.BarberDetails(event.shopId, event.barberId))

                is ShopDetailsEvent.NavigateToBookingServices ->
                    navigator.navigate(IMainGraph.BookingServices(event.shopId))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        BookingTopBar(
            title = state.shopData?.name ?: stringResource(Res.string.shop_details_title),
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
            // ── Shop information ─────────────────────────────────────────────
            when (val shop = state.shop) {
                is SectionState.Loading -> BookingSectionCard(title = stringResource(Res.string.shop_details_title)) {
                    BookingShimmerLines(lines = 4)
                }

                is SectionState.Error -> BookingSectionCard(title = stringResource(Res.string.shop_details_title)) {
                    BookingSectionError(
                        message = shop.message,
                        onRetry = { viewModel.onAction(ShopDetailsAction.RetryShop) })
                }

                is SectionState.Data -> {
                    val barbershop = shop.model
                    BookingSectionCard(title = barbershop.name) {
                        if (barbershop.isVerified) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Verified,
                                    contentDescription = null,
                                    tint = HallaakTheme.colors.status.info,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = stringResource(Res.string.verified_badge),
                                    style = HallaakTheme.typography.label.large,
                                    color = HallaakTheme.colors.status.info,
                                )
                            }
                            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                        }

                        if (!barbershop.isActive) {
                            BookingNoticeBanner(message = stringResource(Res.string.shop_inactive_notice))
                            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                        } else if (!barbershop.isBookingEnabled) {
                            BookingNoticeBanner(
                                message = stringResource(Res.string.booking_unavailable_notice),
                                isError = false,
                            )
                            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                        }

                        barbershop.description?.takeIf { it.isNotBlank() }?.let { description ->
                            Text(
                                text = stringResource(Res.string.section_about),
                                style = HallaakTheme.typography.label.large,
                                color = HallaakTheme.colors.onSurfaceVariant,
                            )
                            Text(
                                text = description,
                                style = HallaakTheme.typography.body.small,
                                color = HallaakTheme.colors.onSurface,
                            )
                            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                        }

                        val address = buildString {
                            append(
                                listOf(
                                barbershop.district,
                                barbershop.city,
                                barbershop.governorate,
                                barbershop.streetAddress,
                            ).filterNotNull().filter { it.isNotBlank() }.joinToString("، ")
                            )
                        }
                        if (address.isNotBlank()) {
                            BookingInfoRow(
                                icon = Icons.Outlined.Place,
                                label = stringResource(Res.string.label_address),
                                value = address,
                            )
                            Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                        }
                        BookingInfoRow(
                            icon = Icons.Outlined.Call,
                            label = stringResource(Res.string.label_phone),
                            value = barbershop.phoneNumber,
                        )
                        Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                    }
                }
            }

            // ── Services ─────────────────────────────────────────────────────
            when (val services = state.services) {
                is SectionState.Loading -> BookingSectionCard(title = stringResource(Res.string.section_services)) {
                    BookingShimmerLines(lines = 3, lineHeight = 28.dp)
                }

                is SectionState.Error -> BookingSectionCard(title = stringResource(Res.string.section_services)) {
                    BookingSectionError(
                        message = services.message,
                        onRetry = { viewModel.onAction(ShopDetailsAction.RetryServices) })
                }

                is SectionState.Data -> if (services.model.isNotEmpty()) {
                    BookingSectionCard(title = stringResource(Res.string.section_services)) {
                        services.model.filter { it.isActive }.forEachIndexed { index, service ->
                            ServiceRow(service)
                            if (index != services.model.lastIndex) {
                                HorizontalDivider(
                                    color = HallaakTheme.colors.stroke.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(vertical = HallaakTheme.dimensions.space8),
                                )
                            }
                        }
                    }
                }
            }

            // ── Barbers ──────────────────────────────────────────────────────
            when (val barbers = state.barbers) {
                is SectionState.Loading -> BookingSectionCard(title = stringResource(Res.string.section_barbers)) {
                    BookingShimmerLines(lines = 3, lineHeight = 32.dp)
                }

                is SectionState.Error -> BookingSectionCard(title = stringResource(Res.string.section_barbers)) {
                    BookingSectionError(
                        message = barbers.message,
                        onRetry = { viewModel.onAction(ShopDetailsAction.RetryBarbers) })
                }

                is SectionState.Data -> if (barbers.model.isNotEmpty()) {
                    BookingSectionCard(title = stringResource(Res.string.section_barbers)) {
                        barbers.model.forEachIndexed { index, shopBarber ->
                            BarberRow(
                                shopBarber = shopBarber,
                                profile = state.barberProfiles[shopBarber.barberId],
                                onClick = {
                                    viewModel.onAction(
                                        ShopDetailsAction.BarberClicked(
                                            shopBarber
                                        )
                                    )
                                },
                            )
                            if (index != barbers.model.lastIndex) {
                                HorizontalDivider(
                                    color = HallaakTheme.colors.stroke.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(vertical = HallaakTheme.dimensions.space8),
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(HallaakTheme.dimensions.space8))
        }

        // ── Primary CTA ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HallaakTheme.colors.background.surface)
                .padding(HallaakTheme.dimensions.space16),
        ) {
            HallaakButtonGold(
                label = stringResource(Res.string.book_appointment),
                onClick = { viewModel.onAction(ShopDetailsAction.BookClicked) },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = state.canBookOnline,
            )
        }
    }
}

// ── Service row ───────────────────────────────────────────────────────────────

@Composable
private fun ServiceRow(service: ShopService) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service.name ?: stringResource(Res.string.service_unnamed),
                style = HallaakTheme.typography.title.large,
                color = HallaakTheme.colors.onSurface,
            )
            Text(
                text = stringResource(
                    Res.string.duration_minutes,
                    service.durationMinutes.toString()
                ),
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

// ── Barber row ────────────────────────────────────────────────────────────────

@Composable
private fun BarberRow(
    shopBarber: ShopBarber,
    profile: BarberProfile?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HallaakTheme.dimensions.space4)
            .clickable(onClick = onClick),
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
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = HallaakTheme.colors.primary,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = profile?.specialization?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.barber_row_fallback),
                style = HallaakTheme.typography.title.large,
                color = HallaakTheme.colors.onSurface,
            )
            if (!shopBarber.isBookable) {
                Text(
                    text = stringResource(Res.string.barber_not_bookable_badge),
                    style = HallaakTheme.typography.label.large,
                    color = HallaakTheme.colors.status.warning,
                )
            }
        }
    }
}
