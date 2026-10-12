package com.daragly.daraglyhair.feature.favorites.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.designsystem.util.HallaakShimmerBox

/**
 * Skeleton placeholder for a single favorites shop card while the list is loading.
 */
@Composable
fun FavoriteShopCardShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = HallaakTheme.dimensions.space16,
                vertical = HallaakTheme.dimensions.space8,
            ),
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
    ) {
        // Top row: name + heart button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Shop name
            HallaakShimmerBox(
                modifier = Modifier
                    .weight(0.6f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.width(HallaakTheme.dimensions.space16))
            // Heart button
            HallaakShimmerBox(
                modifier = Modifier
                    .width(36.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
        }

        // Location line
        HallaakShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp)),
        )

        // Booking badge
        HallaakShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
    }
}

/**
 * A list of skeleton cards shown while favorites are loading.
 */
@Composable
fun FavoritesShimmerList(count: Int = 5, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        repeat(count) {
            FavoriteShopCardShimmer()
        }
    }
}
