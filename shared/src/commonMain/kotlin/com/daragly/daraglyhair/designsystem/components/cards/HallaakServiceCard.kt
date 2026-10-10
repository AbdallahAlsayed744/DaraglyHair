package com.daragly.daraglyhair.designsystem.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakServiceCard
//
//  Catalog item for a single barbershop service.
//
//  Layout (RTL):
//    ┌──────────────────────────────────┐
//    │  [✂ Icon]  Service Name          │
//    │            ⏱ 30 دقيقة  100 ج.م  │
//    └──────────────────────────────────┘
// ─────────────────────────────────────────────

@Composable
fun HallaakServiceCard(
    serviceName: String,
    priceEgp: String,
    durationMinutes: String,
    modifier: Modifier = Modifier,
    serviceIcon: ImageVector = Icons.Outlined.ContentCut,
    onClick: () -> Unit = {},
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = HallaakTheme.shapes.card,
        colors    = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.background.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick   = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Start: icon + name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = HallaakTheme.colors.goldVariant,
                            shape = HallaakTheme.shapes.card,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier           = Modifier.size(22.dp),
                        imageVector        = serviceIcon,
                        tint               = HallaakTheme.colors.primary,
                        contentDescription = null,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text  = serviceName,
                        style = HallaakTheme.typography.title.large,
                        color = HallaakTheme.colors.onSurface,
                    )
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            modifier           = Modifier.size(12.dp),
                            imageVector        = Icons.Outlined.AccessTime,
                            tint               = HallaakTheme.colors.onSurfaceVariant,
                            contentDescription = null,
                        )
                        Text(
                            text  = "$durationMinutes دقيقة",
                            style = HallaakTheme.typography.label.large,
                            color = HallaakTheme.colors.onSurfaceVariant,
                        )
                    }
                }
            }

            // End: price
            Text(
                text  = "$priceEgp ج.م",
                style = HallaakTheme.typography.headline.extraSmall,
                color = HallaakTheme.colors.primary,
            )
        }
    }
}
