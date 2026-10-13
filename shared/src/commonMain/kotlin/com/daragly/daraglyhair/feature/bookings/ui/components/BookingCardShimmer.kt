package com.daragly.daraglyhair.feature.bookings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.designsystem.util.HallaakShimmerBox

/**
 * Skeleton placeholder that matches the visual footprint of [BookingCard].
 * Prevents layout jumps when appointments are loading.
 */
@Composable
fun BookingCardShimmer(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
        ) {
            // Row 1: shop name + badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HallaakShimmerBox(
                    modifier = Modifier
                        .width(160.dp)
                        .height(18.dp)
                        .clip(HallaakTheme.shapes.pill),
                )
                HallaakShimmerBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(24.dp)
                        .clip(HallaakTheme.shapes.pill),
                )
            }

            // Row 2: date & time
            Row(
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
            ) {
                HallaakShimmerBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                        .clip(HallaakTheme.shapes.pill),
                )
                HallaakShimmerBox(
                    modifier = Modifier
                        .width(70.dp)
                        .height(14.dp)
                        .clip(HallaakTheme.shapes.pill),
                )
            }

            // Row 3: price
            HallaakShimmerBox(
                modifier = Modifier
                    .width(130.dp)
                    .height(14.dp)
                    .clip(HallaakTheme.shapes.pill),
            )

            Spacer(Modifier.height(HallaakTheme.dimensions.space4))

            // Divider placeholder
            HallaakShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
            )

            Spacer(Modifier.height(HallaakTheme.dimensions.space4))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HallaakShimmerBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(32.dp)
                        .clip(HallaakTheme.shapes.button),
                )
            }
        }
    }
}

@Composable
fun BookingShimmerList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
    ) {
        repeat(3) {
            BookingCardShimmer(
                modifier = Modifier.padding(horizontal = HallaakTheme.dimensions.space16),
            )
        }
    }
}
