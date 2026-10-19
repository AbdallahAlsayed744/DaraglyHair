package com.daragly.daraglyhair.feature.explore.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.LocalPadding
import com.daragly.daraglyhair.common.ui.location.EnsureLocationPermission
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.components.textfield.HallaakSearchField
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.home.ui.components.BarbershopCard
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.cd_clear_search
import daraglyhair.shared.generated.resources.cd_retry
import daraglyhair.shared.generated.resources.cd_search_barbershops
import daraglyhair.shared.generated.resources.error_generic_title
import daraglyhair.shared.generated.resources.explore_clear_search
import daraglyhair.shared.generated.resources.explore_distance_label
import daraglyhair.shared.generated.resources.explore_distance_value
import daraglyhair.shared.generated.resources.explore_empty_no_query
import daraglyhair.shared.generated.resources.explore_empty_subtitle
import daraglyhair.shared.generated.resources.explore_empty_with_query
import daraglyhair.shared.generated.resources.explore_search_placeholder
import daraglyhair.shared.generated.resources.explore_subtitle
import daraglyhair.shared.generated.resources.explore_title
import daraglyhair.shared.generated.resources.ic_explore
import daraglyhair.shared.generated.resources.ic_explore_outlined
import daraglyhair.shared.generated.resources.retry
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ExploreScreen(
    initialQuery: String = "",
    viewModel: ExploreViewModel = koinViewModel(),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    // Ask for location once per entry, then re-run the search with the user's real coordinates.
    EnsureLocationPermission { granted ->
        if (granted) viewModel.onAction(ExploreAction.Retry)
    }

    // Boot the screen — pre-fill keyword if coming from Home search bar tap
    LaunchedEffect(Unit) {
        viewModel.onAction(ExploreAction.Init(initialQuery))
    }

    // Collect one-shot navigation events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ExploreEvent.NavigateToShopDetails -> {
                    navigator.navigate(IMainGraph.ShopDetails(event.shopId))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = LocalPadding.current.calculateBottomPadding(),
            ),
        ) {
            // ── Sticky header: title + search bar ─────────────────────────
            stickyHeader(key = "search_header") {
                ExploreHeader(
                    keyword = state.searchKeyword,
                    onKeywordChanged = { viewModel.onAction(ExploreAction.SearchKeywordChanged(it)) },
                    onClearClick = { viewModel.onAction(ExploreAction.ClearSearch) },
                    maxDistanceKm = state.maxDistanceKm,
                    onDistanceChanged = { viewModel.onAction(ExploreAction.DistanceChanged(it)) },
                )
            }

            // ── Content ───────────────────────────────────────────────────
            when (val shopsState = state.shopsState) {

                is ExploreShopsState.Idle -> {
                    // Briefly visible before Init fires; show shimmer
                    item(key = "shimmer_idle") { ExploreShimmerList() }
                }

                is ExploreShopsState.Loading -> {
                    item(key = "shimmer") { ExploreShimmerList() }
                }

                is ExploreShopsState.Success -> {
                    items(
                        items = shopsState.shops,
                        key = { it.id },
                    ) { shop ->
                        BarbershopCard(
                            barbershop = shop,
                            onCardClick = { viewModel.onAction(ExploreAction.ShopCardClicked(shop.id)) },
                            onFavoriteClick = { viewModel.onAction(ExploreAction.ToggleFavorite(shop.id)) },
                            isFavoriteToggleInProgress = state.favoriteToggleInProgress.contains(shop.id),
                            modifier = Modifier.padding(
                                horizontal = HallaakTheme.dimensions.space16,
                                vertical = HallaakTheme.dimensions.space8,
                            ),
                        )
                    }

                    item(key = "bottom_space") {
                        Spacer(Modifier.height(HallaakTheme.dimensions.space32))
                    }
                }

                is ExploreShopsState.Empty -> {
                    item(key = "empty") {
                        ExploreEmptyState(
                            query = shopsState.query,
                            onClearSearch = { viewModel.onAction(ExploreAction.ClearSearch) },
                        )
                    }
                }

                is ExploreShopsState.Error -> {
                    item(key = "error") {
                        ExploreErrorState(
                            message = shopsState.message,
                            onRetry = { viewModel.onAction(ExploreAction.Retry) },
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header — primary-colored banner + search bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExploreHeader(
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    maxDistanceKm: Int,
    onDistanceChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        // Brand banner
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HallaakTheme.colors.primary)
                .padding(
                    top = 48.dp,
                    start = HallaakTheme.dimensions.space20,
                    end = HallaakTheme.dimensions.space20,
                    bottom = HallaakTheme.dimensions.space20,
                ),
        ) {
            Text(
                text = stringResource(Res.string.explore_title),
                style = HallaakTheme.typography.headline.large,
                color = HallaakTheme.colors.background.canvas,
            )
            Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space4))
            Text(
                text = stringResource(Res.string.explore_subtitle),
                style = HallaakTheme.typography.body.medium,
                color = HallaakTheme.colors.background.canvas.copy(alpha = 0.85f),
            )
        }

        // Search field — sits just below the banner
        val searchFieldDescription = stringResource(Res.string.cd_search_barbershops)
        HallaakSearchField(
            value = keyword,
            onValueChange = onKeywordChanged,
            placeholder = stringResource(Res.string.explore_search_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = HallaakTheme.dimensions.space16,
                    vertical = HallaakTheme.dimensions.space12,
                )
                .semantics { contentDescription = searchFieldDescription },
        )

        // Distance filter slider
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HallaakTheme.dimensions.space16)
                .padding(bottom = HallaakTheme.dimensions.space12),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.explore_distance_label),
                    style = HallaakTheme.typography.label.large,
                    color = HallaakTheme.colors.onSurfaceVariant,
                )
                Text(
                    text = stringResource(Res.string.explore_distance_value, maxDistanceKm),
                    style = HallaakTheme.typography.label.large,
                    color = HallaakTheme.colors.primary,
                )
            }
            Slider(
                value = maxDistanceKm.toFloat(),
                onValueChange = { onDistanceChanged(it.toInt()) },
                valueRange = MIN_DISTANCE_KM.toFloat()..MAX_DISTANCE_KM.toFloat(),
                steps = MAX_DISTANCE_KM - MIN_DISTANCE_KM - 1,
                colors = SliderDefaults.colors(
                    thumbColor = HallaakTheme.colors.primary,
                    activeTrackColor = HallaakTheme.colors.primary,
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Empty state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExploreEmptyState(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HallaakTheme.dimensions.space32)
            .padding(top = HallaakTheme.dimensions.space64),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_explore_outlined),
            contentDescription = null,
            tint = HallaakTheme.colors.gold.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp),
        )

        Text(
            text = if (query.isNotBlank()) {
                stringResource(Res.string.explore_empty_with_query, query)
            } else {
                stringResource(Res.string.explore_empty_no_query)
            },
            style = HallaakTheme.typography.headline.extraSmall,
            color = HallaakTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.explore_empty_subtitle),
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space8))

        if (query.isNotBlank()) {
            val clearSearchDescription = stringResource(Res.string.cd_clear_search)
            HallaakButtonPrimary(
                label = stringResource(Res.string.explore_clear_search),
                onClick = onClearSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = clearSearchDescription },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Error state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExploreErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HallaakTheme.dimensions.space32)
            .padding(top = HallaakTheme.dimensions.space64),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_explore),
            contentDescription = null,
            tint = HallaakTheme.colors.status.error.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp),
        )

        Text(
            text = stringResource(Res.string.error_generic_title),
            style = HallaakTheme.typography.headline.extraSmall,
            color = HallaakTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = message,
            style = HallaakTheme.typography.body.small,
            color = HallaakTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space8))

        val retryDescription = stringResource(Res.string.cd_retry)
        HallaakButtonPrimary(
            label = stringResource(Res.string.retry),
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = retryDescription },
        )
    }
}
