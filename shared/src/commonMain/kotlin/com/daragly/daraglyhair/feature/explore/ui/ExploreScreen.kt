package com.daragly.daraglyhair.feature.explore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.daragly.daraglyhair.LocalPadding
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

@Composable
fun ExploreScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalPadding.current),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
        ) {
            Text(
                text = "Explore — اكتشف",
                style = HallaakTheme.typography.headline.medium,
                color = HallaakTheme.colors.onSurface,
            )
            Text(
                text = "Search barbershops, map view, and filters",
                style = HallaakTheme.typography.body.medium,
                color = HallaakTheme.colors.onSurfaceVariant,
            )
        }
    }
}
