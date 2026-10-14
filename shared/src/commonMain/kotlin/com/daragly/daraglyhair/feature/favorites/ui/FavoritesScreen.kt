package com.daragly.daraglyhair.feature.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.favorites.ui.components.FavoritesShimmerList
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import com.daragly.daraglyhair.feature.home.ui.components.BarbershopCard
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.error_generic_title
import daraglyhair.shared.generated.resources.explore_barbershops
import daraglyhair.shared.generated.resources.favorites_empty_subtitle
import daraglyhair.shared.generated.resources.favorites_empty_title
import daraglyhair.shared.generated.resources.favorites_subtitle
import daraglyhair.shared.generated.resources.favorites_subtitle_with_count
import daraglyhair.shared.generated.resources.favorites_title
import daraglyhair.shared.generated.resources.ic_explore
import daraglyhair.shared.generated.resources.ic_favorite_outlined
import daraglyhair.shared.generated.resources.retry
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    // Collect one-shot navigation events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is FavoritesEvent.NavigateToShopDetails -> {
                    navigator.navigate(IMainGraph.ShopDetails(event.shopId))
                }
                is FavoritesEvent.NavigateToExplore -> {
                    navigator.navigate(IMainGraph.Explore())
                }
            }
        }
    }

    val isRefreshing = state.content is FavoritesContentState.Loading &&
            state.content !is FavoritesContentState.Loading

    PullToRefreshBox(
        isRefreshing = false, // loading state is shown inline; pull-to-refresh only makes sense on Success
        onRefresh = { viewModel.onAction(FavoritesAction.Refresh) },
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
            // ── Screen header ─────────────────────────────────────────────
            item(key = "header") {
                FavoritesHeader(
                    shopCount = (state.content as? FavoritesContentState.Success)?.shops?.size,
                )
            }

            // ── Content ───────────────────────────────────────────────────
            when (val content = state.content) {

                is FavoritesContentState.Loading -> {
                    item(key = "shimmer") {
                        FavoritesShimmerList()
                    }
                }

                is FavoritesContentState.Success -> {
                    items(
                        items = content.shops,
                        key = { it.id },
                    ) { shop ->
                        FavoritesShopItem(
                            shop = shop,
                            isFavoriteToggleInProgress = state.favoriteToggleInProgress.contains(shop.id),
                            onCardClick = { viewModel.onAction(FavoritesAction.ShopCardClicked(shop.id)) },
                            onFavoriteClick = { viewModel.onAction(FavoritesAction.ToggleFavorite(shop.id)) },
                        )
                    }

                    item(key = "bottom_space") {
                        Spacer(Modifier.height(HallaakTheme.dimensions.space32))
                    }
                }

                is FavoritesContentState.Empty -> {
                    item(key = "empty") {
                        FavoritesEmptyState(
                            onExploreClick = { viewModel.onAction(FavoritesAction.ExploreClicked) },
                        )
                    }
                }

                is FavoritesContentState.Error -> {
                    item(key = "error") {
                        FavoritesErrorState(
                            message = content.message,
                            onRetryClick = { viewModel.onAction(FavoritesAction.Retry) },
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FavoritesHeader(
    shopCount: Int?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.primary)
            .padding(
                top = 48.dp,
                start = HallaakTheme.dimensions.space20,
                end = HallaakTheme.dimensions.space20,
                bottom = HallaakTheme.dimensions.space24,
            ),
    ) {
        val titleText = stringResource(Res.string.favorites_title)
        val subtitleText = if (shopCount != null && shopCount > 0) {
            stringResource(Res.string.favorites_subtitle_with_count, shopCount)
        } else {
            stringResource(Res.string.favorites_subtitle)
        }

        Text(
            text = titleText,
            style = HallaakTheme.typography.headline.large,
            color = HallaakTheme.colors.background.canvas,
        )

        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space4))

        Text(
            text = subtitleText,
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.background.canvas.copy(alpha = 0.85f),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Shop card row — wraps the shared BarbershopCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FavoritesShopItem(
    shop: Barbershop,
    isFavoriteToggleInProgress: Boolean,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BarbershopCard(
        barbershop = shop,
        onCardClick = onCardClick,
        onFavoriteClick = onFavoriteClick,
        isFavoriteToggleInProgress = isFavoriteToggleInProgress,
        modifier = modifier.padding(
            horizontal = HallaakTheme.dimensions.space16,
            vertical = HallaakTheme.dimensions.space8,
        ),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Empty state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FavoritesEmptyState(
    onExploreClick: () -> Unit,
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
        // Illustration — stacked icons: heart + explore
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_favorite_outlined),
                contentDescription = null,
                tint = HallaakTheme.colors.gold.copy(alpha = 0.25f),
                modifier = Modifier.size(120.dp),
            )
            Icon(
                painter = painterResource(Res.drawable.ic_explore),
                contentDescription = null,
                tint = HallaakTheme.colors.gold,
                modifier = Modifier.size(52.dp),
            )
        }

        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space8))

        Text(
            text = stringResource(Res.string.favorites_empty_title),
            style = HallaakTheme.typography.headline.extraSmall,
            color = HallaakTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.favorites_empty_subtitle),
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space8))

        val exploreLabel = stringResource(Res.string.explore_barbershops)
        HallaakButtonPrimary(
            label = exploreLabel,
            onClick = onExploreClick,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = exploreLabel },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Error state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FavoritesErrorState(
    message: String,
    onRetryClick: () -> Unit,
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
            painter = painterResource(Res.drawable.ic_favorite_outlined),
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

        val retryLabel = stringResource(Res.string.retry)
        HallaakButtonPrimary(
            label = retryLabel,
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = retryLabel },
        )
    }
}
