package com.daragly.daraglyhair.feature.shopbooking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGhost
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.designsystem.util.HallaakShimmerBox
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.back
import daraglyhair.shared.generated.resources.error_generic_title
import daraglyhair.shared.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

// ── Shared top bar for shop-booking screens ───────────────────────────────────

@Composable
fun BookingTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.primary)
            .padding(
                top = HallaakTheme.dimensions.space48,
                start = HallaakTheme.dimensions.space8,
                end = HallaakTheme.dimensions.space20,
                bottom = HallaakTheme.dimensions.space16,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.back),
                    tint = HallaakTheme.colors.background.canvas,
                )
            }
        }
        Spacer(Modifier.size(HallaakTheme.dimensions.space8))
        Text(
            text = title,
            style = HallaakTheme.typography.headline.small,
            color = HallaakTheme.colors.background.canvas,
        )
    }
}

// ── Card section ──────────────────────────────────────────────────────────────

@Composable
fun BookingSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
        ) {
            Text(
                text = title,
                style = HallaakTheme.typography.title.small,
                color = HallaakTheme.colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
            content()
        }
    }
}

// ── Section error row with inline retry ───────────────────────────────────────

@Composable
fun BookingSectionError(
    message: String,
    onRetry: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8)) {
        Text(
            text = message.ifBlank { stringResource(Res.string.error_generic_title) },
            style = HallaakTheme.typography.body.small,
            color = HallaakTheme.colors.status.error,
        )
        HallaakButtonGhost(
            label = stringResource(Res.string.retry),
            onClick = onRetry,
        )
    }
}

// ── Shimmer placeholder lines ─────────────────────────────────────────────────

@Composable
fun BookingShimmerLines(
    lines: Int = 3,
    lineHeight: androidx.compose.ui.unit.Dp = 16.dp,
) {
    Column(verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8)) {
        repeat(lines) { index ->
            HallaakShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(lineHeight)
                    .padding(end = if (index == lines - 1) 48.dp else 0.dp),
            )
        }
    }
}

// ── Icon + label/value row ────────────────────────────────────────────────────

@Composable
fun BookingInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HallaakTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
        Column {
            Text(
                text = label,
                style = HallaakTheme.typography.label.large,
                color = HallaakTheme.colors.onSurfaceVariant,
            )
            Text(
                text = value,
                style = HallaakTheme.typography.body.small,
                color = HallaakTheme.colors.onSurface,
            )
        }
    }
}

// ── Label • value summary row (review screen) ─────────────────────────────────

@Composable
fun BookingSummaryRow(
    label: String,
    value: String,
    emphasizeValue: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = HallaakTheme.typography.body.small,
            color = HallaakTheme.colors.onSurfaceVariant,
        )
        Text(
            text = value,
            style = if (emphasizeValue) {
                HallaakTheme.typography.title.large
            } else {
                HallaakTheme.typography.body.small
            },
            color = if (emphasizeValue) HallaakTheme.colors.primary else HallaakTheme.colors.onSurface,
        )
    }
}

// ── Inline notice banner ──────────────────────────────────────────────────────

@Composable
fun BookingNoticeBanner(
    message: String,
    isError: Boolean = true,
) {
    val color = if (isError) HallaakTheme.colors.status.error else HallaakTheme.colors.status.info
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = color.copy(alpha = 0.12f), shape = HallaakTheme.shapes.card)
            .padding(HallaakTheme.dimensions.space12),
    ) {
        Text(
            text = message,
            style = HallaakTheme.typography.body.small,
            color = color,
        )
    }
}
