package com.daragly.daraglyhair.designsystem.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.designsystem.util.HallaakShimmerBox

// ─────────────────────────────────────────────
//  HallaakShopCard
//
//  Discovery list / grid item for a barber shop.
//
//  Layout:
//    ┌──────────────────────────────┐
//    │  [Hero Image – 160dp]        │
//    │                   [★ 4.8]   │  ← gold badge
//    ├──────────────────────────────┤
//    │  Shop Name (bold headline)   │
//    │  📍 District · 1.2 km        │
//    │  Services: Haircut, Beard…   │
//    └──────────────────────────────┘
// ─────────────────────────────────────────────

@Composable
fun HallaakShopCard(
    shopName: String,
    districtName: String,
    distanceKm: String,
    rating: String,
    servicesTags: String,
    modifier: Modifier = Modifier,
    isOnlineBooking: Boolean = false,
    heroImageContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = HallaakTheme.shapes.card,
        colors    = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.background.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick   = onClick,
    ) {
        Column {
            // ── Hero image area ───────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(HallaakTheme.shapes.card),
            ) {
                // Placeholder shimmer or actual image
                if (heroImageContent != null) {
                    heroImageContent()
                } else {
                    HallaakShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                    )
                }

                // Rating badge (top-end corner)
                HallaakRatingBadge(
                    rating   = rating,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                )

                // Online booking chip (top-start corner)
                if (isOnlineBooking) {
                    HallaakBookingChip(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                    )
                }
            }

            // ── Info section ──────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text     = shopName,
                    style    = HallaakTheme.typography.title.extraLarge,
                    color    = HallaakTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier           = Modifier.size(14.dp),
                        imageVector        = Icons.Outlined.LocationOn,
                        tint               = HallaakTheme.colors.onSurfaceVariant,
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text  = "$districtName · $distanceKm كم",
                        style = HallaakTheme.typography.label.large,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                }

                Text(
                    text     = servicesTags,
                    style    = HallaakTheme.typography.label.medium,
                    color    = HallaakTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ── Rating badge ──────────────────────────────

@Composable
fun HallaakRatingBadge(
    rating: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(
                color = HallaakTheme.colors.gold,
                shape = HallaakTheme.shapes.pill,
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            modifier           = Modifier.size(12.dp),
            imageVector        = Icons.Outlined.Star,
            tint               = HallaakTheme.colors.background.surfaceHigh,
            contentDescription = null,
        )
        Text(
            text  = rating,
            style = HallaakTheme.typography.label.medium,
            color = HallaakTheme.colors.background.surfaceHigh,
        )
    }
}

// ── Online booking chip ───────────────────────

@Composable
fun HallaakBookingChip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = HallaakTheme.colors.primary,
                shape = HallaakTheme.shapes.pill,
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text  = "حجز أونلاين",
            style = HallaakTheme.typography.label.medium,
            color = Color.White,
        )
    }
}
