package com.daragly.daraglyhair.feature.reviews.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.reviews.domain.model.Review
import kotlin.math.floor
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.review_no_comment
import daraglyhair.shared.generated.resources.review_photo_count
import org.jetbrains.compose.resources.stringResource

// ── Read-only star row (supports fractional average) ──────────────────────────

@Composable
fun StarRatingDisplay(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: androidx.compose.ui.unit.Dp = 16.dp,
) {
    val rounded = rating.coerceIn(0.0, 5.0)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 1..5) {
            val filled = i <= floor(rounded).toInt()
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (filled) HallaakTheme.colors.gold else HallaakTheme.colors.stroke,
                modifier = Modifier.size(starSize),
            )
        }
    }
}

// ── Interactive 1..5 star picker ──────────────────────────────────────────────

@Composable
fun StarRatingInput(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 1..5) {
            val filled = i <= value
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (filled) HallaakTheme.colors.gold else HallaakTheme.colors.stroke,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable(enabled = enabled) { onValueChange(i) }
                    .padding(4.dp),
            )
        }
    }
}

// ── Labelled rating row for the write-review form ─────────────────────────────

@Composable
fun RatingCategoryRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.onSurface,
        )
        StarRatingInput(value = value, onValueChange = onValueChange)
    }
}

// ── A single review card used in list + preview ───────────────────────────────

@Composable
fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = HallaakTheme.shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.background.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space16),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
            ) {
                StarRatingDisplay(rating = review.overallRating.toDouble())
                Text(
                    text = review.overallRating.toString(),
                    style = HallaakTheme.typography.label.large,
                    color = HallaakTheme.colors.onSurfaceVariant,
                )
            }

            review.comment?.let { comment ->
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                Text(
                    text = comment,
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.onSurface,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                )
            } ?: run {
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                Text(
                    text = stringResource(Res.string.review_no_comment),
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.onSurfaceVariant,
                )
            }

            if (review.photoCount > 0) {
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddAPhoto,
                        contentDescription = null,
                        tint = HallaakTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = stringResource(Res.string.review_photo_count, review.photoCount),
                        style = HallaakTheme.typography.label.medium,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

// ── Selected-photo chip (thumbnail-free; base64 photos aren't previewable) ────

@Composable
fun ReviewPhotoChip(
    index: Int,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(HallaakTheme.shapes.card)
            .background(HallaakTheme.colors.background.surfaceHigh),
    ) {
        Icon(
            imageVector = Icons.Filled.AddAPhoto,
            contentDescription = null,
            tint = HallaakTheme.colors.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Center)
                .size(28.dp),
        )
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = null,
            tint = HallaakTheme.colors.background.canvas,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(HallaakTheme.colors.status.error)
                .clickable(onClick = onRemove)
                .padding(3.dp),
        )
        Text(
            text = (index + 1).toString(),
            style = HallaakTheme.typography.label.small,
            color = HallaakTheme.colors.onSurfaceVariant,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp),
        )
    }
}
