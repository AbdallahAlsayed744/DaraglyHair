package com.daragly.daraglyhair.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.ic_booking_enabled
import daraglyhair.shared.generated.resources.ic_favorite
import daraglyhair.shared.generated.resources.ic_favorite_outlined
import daraglyhair.shared.generated.resources.ic_location
import daraglyhair.shared.generated.resources.ic_verified
import org.jetbrains.compose.resources.painterResource

@Composable
fun BarbershopCard(
    barbershop: Barbershop,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavoriteToggleInProgress: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        colors = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.background.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space16),
        ) {
            // Top row: Shop name + favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                // Shop name + verified badge
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = barbershop.name,
                        style = HallaakTheme.typography.title.large,
                        color = HallaakTheme.colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    
                    if (barbershop.isVerified) {
                        Spacer(modifier = Modifier.width(HallaakTheme.dimensions.space4))
                        Icon(
                            painter = painterResource(Res.drawable.ic_verified),
                            contentDescription = "Verified",
                            tint = HallaakTheme.colors.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    enabled = !isFavoriteToggleInProgress,
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        painter = painterResource(
                            if (barbershop.isFavorite) Res.drawable.ic_favorite
                            else Res.drawable.ic_favorite_outlined
                        ),
                        contentDescription = if (barbershop.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (barbershop.isFavorite) HallaakTheme.colors.status.error else HallaakTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space8))

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_location),
                    contentDescription = null,
                    tint = HallaakTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(HallaakTheme.dimensions.space4))
                Text(
                    text = buildLocationString(barbershop),
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                
                // Distance if available
                barbershop.distanceKm?.let { distance ->
                    Text(
                        text = " • ${formatDistance(distance)}",
                        style = HallaakTheme.typography.body.small,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space12))

            // Booking status
            Row(
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
            ) {
                if (barbershop.isBookingEnabled) {
                    StatusChip(
                        text = "Booking Available",
                        icon = Res.drawable.ic_booking_enabled,
                        backgroundColor = HallaakTheme.colors.status.success.copy(alpha = 0.1f),
                        textColor = HallaakTheme.colors.status.success,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    icon: org.jetbrains.compose.resources.DrawableResource,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
) {
    Row(
        modifier = Modifier
            .background(backgroundColor, shape = HallaakTheme.shapes.pill)
            .padding(horizontal = HallaakTheme.dimensions.space8, vertical = HallaakTheme.dimensions.space4),
        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            style = HallaakTheme.typography.label.medium,
            color = textColor,
        )
    }
}

private fun buildLocationString(barbershop: Barbershop): String {
    val parts = mutableListOf<String>()
    
    if (!barbershop.district.isNullOrBlank()) {
        parts.add(barbershop.district)
    }
    
    parts.add(barbershop.city)
    
    return parts.joinToString(", ")
}

private fun formatDistance(distanceKm: Double): String {
    return if (distanceKm < 1.0) {
        "${(distanceKm * 1000).toInt()}m"
    } else {
        // Format to 1 decimal place
        val rounded = (distanceKm * 10).toInt() / 10.0
        "$rounded km"
    }
}
