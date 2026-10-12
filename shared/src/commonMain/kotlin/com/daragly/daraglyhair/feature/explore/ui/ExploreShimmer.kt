package com.daragly.daraglyhair.feature.explore.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.designsystem.util.HallaakShimmerBox

@Composable
fun ExploreShimmerItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = HallaakTheme.dimensions.space16,
                vertical = HallaakTheme.dimensions.space8,
            ),
    ) {
        // Mimic the BarbershopCard layout
        // Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HallaakShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .height(22.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.width(HallaakTheme.dimensions.space16))
            HallaakShimmerBox(
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
        }

        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space8))

        // Location row
        HallaakShimmerBox(
            modifier = Modifier
                .width(180.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp)),
        )

        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space12))

        // Status chip
        HallaakShimmerBox(
            modifier = Modifier
                .width(140.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(50.dp)),
        )
    }
}

@Composable
fun ExploreShimmerList(count: Int = 6, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        repeat(count) {
            ExploreShimmerItem()
        }
    }
}
