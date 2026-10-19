package com.daragly.daraglyhair.feature.reviews.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.reviews.ui.components.ReviewItem
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionError
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingShimmerLines
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingTopBar
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.no_reviews_yet
import daraglyhair.shared.generated.resources.section_reviews
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ShopReviewsScreen(
    shopId: String,
    viewModel: ShopReviewsViewModel = koinViewModel(parameters = { parametersOf(shopId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        BookingTopBar(
            title = stringResource(Res.string.section_reviews),
            onBack = { scope.launch { navigator.navigateUp() } },
        )

        when (val content = state.content) {
            is ShopReviewsContent.Loading -> Column(
                modifier = Modifier.padding(HallaakTheme.dimensions.space16),
            ) {
                BookingShimmerLines(lines = 3, lineHeight = 84.dp)
            }

            is ShopReviewsContent.Error -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HallaakTheme.dimensions.space16),
            ) {
                BookingSectionError(
                    message = content.message,
                    onRetry = { viewModel.onAction(ShopReviewsAction.Retry) },
                )
            }

            ShopReviewsContent.Empty -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(HallaakTheme.dimensions.space32),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.no_reviews_yet),
                    style = HallaakTheme.typography.body.medium,
                    color = HallaakTheme.colors.onSurfaceVariant,
                )
            }

            is ShopReviewsContent.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(HallaakTheme.dimensions.space16),
                verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
            ) {
                items(items = content.reviews, key = { it.id }) { review ->
                    ReviewItem(review = review)
                }
                if (state.isLoadingMore) {
                    item(key = "loading_more") {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(HallaakTheme.dimensions.space16),
                            color = HallaakTheme.colors.primary,
                        )
                    }
                } else if (state.canLoadMore) {
                    item(key = "load_more") {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(bottom = HallaakTheme.dimensions.space8)
                                .background(HallaakTheme.colors.stroke)
                                .padding(top = HallaakTheme.dimensions.space8),
                        )
                        LaunchedEffect(Unit) { viewModel.onAction(ShopReviewsAction.LoadMore) }
                    }
                }
            }
        }
    }
}
