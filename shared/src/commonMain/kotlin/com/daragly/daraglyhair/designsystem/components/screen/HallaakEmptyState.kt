package com.daragly.daraglyhair.designsystem.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakEmptyState
//
//  Centered empty-list / no-results placeholder.
//  Icon (tinted gold) + headline + optional subtitle.
// ─────────────────────────────────────────────

@Composable
fun HallaakEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = HallaakTheme.colors.background.canvas),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier           = Modifier.size(120.dp),
            imageVector        = icon,
            contentDescription = null,
            tint               = HallaakTheme.colors.gold.copy(alpha = 0.6f),
        )

        Text(
            modifier  = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
            text      = title,
            style     = HallaakTheme.typography.headline.extraSmall,
            color     = HallaakTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )

        subtitle?.let {
            Text(
                modifier  = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp),
                text      = it,
                style     = HallaakTheme.typography.body.small,
                color     = HallaakTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
