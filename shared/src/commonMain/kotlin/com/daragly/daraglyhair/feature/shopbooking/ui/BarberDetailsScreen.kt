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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Store
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
import kotlinx.coroutines.launch
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGold
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingInfoRow
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingNoticeBanner
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionCard
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionError
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingShimmerLines
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingTopBar
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.barber_details_title
import daraglyhair.shared.generated.resources.barber_row_fallback
import daraglyhair.shared.generated.resources.book_with_this_barber
import daraglyhair.shared.generated.resources.barber_inactive_notice
import daraglyhair.shared.generated.resources.booking_unavailable_notice
import daraglyhair.shared.generated.resources.experience_years_format
import daraglyhair.shared.generated.resources.label_experience
import daraglyhair.shared.generated.resources.label_specialization
import daraglyhair.shared.generated.resources.section_about
import daraglyhair.shared.generated.resources.section_shop
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BarberDetailsScreen(
    shopId: String,
    barberId: String,
    viewModel: BarberDetailsViewModel = koinViewModel(parameters = { parametersOf(shopId, barberId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BarberDetailsEvent.NavigateToBookingServices ->
                    navigator.navigate(IMainGraph.BookingServices(event.shopId))
            }
        }
    }

    val blockReason = state.bookBlockReason
    val bookEnabled = blockReason == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        BookingTopBar(
            title = state.barberData?.specialization?.takeIf { it.isNotBlank() }
                ?: stringResource(Res.string.barber_details_title),
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
            // ── Shop context — so the user knows which shop the booking belongs to ──
            state.shopData?.let { shop ->
                BookingSectionCard(title = stringResource(Res.string.section_shop)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
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
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        Text(
                            text = shop.name,
                            style = HallaakTheme.typography.title.large,
                            color = HallaakTheme.colors.onSurface,
                        )
                    }
                }
            }

            // ── Barber profile ───────────────────────────────────────────────
            when (val barber = state.barber) {
                is SectionState.Loading -> BookingSectionCard(title = stringResource(Res.string.barber_details_title)) {
                    BookingShimmerLines(lines = 4)
                }
                is SectionState.Error -> BookingSectionCard(title = stringResource(Res.string.barber_details_title)) {
                    BookingSectionError(message = barber.message, onRetry = { viewModel.onAction(BarberDetailsAction.Retry) })
                }
                is SectionState.Data -> {
                    val profile = barber.model
                    BookingSectionCard(title = stringResource(Res.string.barber_details_title)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        color = HallaakTheme.colors.goldVariant,
                                        shape = CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = HallaakTheme.colors.primary,
                                    modifier = Modifier.size(26.dp),
                                )
                            }
                            Text(
                                text = profile.specialization?.takeIf { it.isNotBlank() }
                                    ?: stringResource(Res.string.barber_row_fallback),
                                style = HallaakTheme.typography.headline.extraSmall,
                                color = HallaakTheme.colors.onSurface,
                            )
                        }

                        profile.specialization?.takeIf { it.isNotBlank() }?.let { spec ->
                            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                            BookingInfoRow(
                                icon = Icons.Outlined.Person,
                                label = stringResource(Res.string.label_specialization),
                                value = spec,
                            )
                        }

                        profile.experienceYears?.let { years ->
                            Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                            BookingInfoRow(
                                icon = Icons.Outlined.Person,
                                label = stringResource(Res.string.label_experience),
                                value = stringResource(Res.string.experience_years_format, years),
                            )
                        }

                        profile.bio?.takeIf { it.isNotBlank() }?.let { bio ->
                            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                            Text(
                                text = stringResource(Res.string.section_about),
                                style = HallaakTheme.typography.label.large,
                                color = HallaakTheme.colors.onSurfaceVariant,
                            )
                            Text(
                                text = bio,
                                style = HallaakTheme.typography.body.small,
                                color = HallaakTheme.colors.onSurface,
                            )
                        }
                    }
                }
            }

            // ── Booking blocked explanation ──────────────────────────────────
            when (blockReason) {
                BookBlockReason.BARBER_INACTIVE,
                BookBlockReason.RELATIONSHIP_DISABLED,
                BookBlockReason.NOT_IN_SHOP -> BookingNoticeBanner(
                    message = stringResource(Res.string.barber_inactive_notice),
                )
                BookBlockReason.SHOP_INACTIVE,
                BookBlockReason.SHOP_BOOKING_DISABLED -> BookingNoticeBanner(
                    message = stringResource(Res.string.booking_unavailable_notice),
                )
                BookBlockReason.LOADING, null -> Unit
            }
        }

        // ── CTA ──────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HallaakTheme.colors.background.surface)
                .padding(HallaakTheme.dimensions.space16),
        ) {
            HallaakButtonGold(
                label = stringResource(Res.string.book_with_this_barber),
                onClick = { viewModel.onAction(BarberDetailsAction.BookClicked) },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = bookEnabled,
            )
        }
    }
}
