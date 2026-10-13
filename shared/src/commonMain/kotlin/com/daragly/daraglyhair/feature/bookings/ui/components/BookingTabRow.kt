package com.daragly.daraglyhair.feature.bookings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingTabFilter
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.tab_cancelled
import daraglyhair.shared.generated.resources.tab_completed
import daraglyhair.shared.generated.resources.tab_upcoming
import org.jetbrains.compose.resources.stringResource

/**
 * Three-tab filter row: Upcoming | Completed | Cancelled
 */
@Composable
fun BookingTabRow(
    selectedTab: BookingTabFilter,
    onTabSelected: (BookingTabFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf(
        BookingTabFilter.UPCOMING  to Res.string.tab_upcoming,
        BookingTabFilter.COMPLETED to Res.string.tab_completed,
        BookingTabFilter.CANCELLED to Res.string.tab_cancelled,
    )

    val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.background.surface),
        containerColor = HallaakTheme.colors.background.surface,
        contentColor = HallaakTheme.colors.primary,
        indicator = { tabPositions ->
            // Gold underline indicator
            Box(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .height(3.dp)
                    .background(
                        color = HallaakTheme.colors.gold,
                        shape = HallaakTheme.shapes.pill,
                    )
            )
        },
        divider = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(HallaakTheme.colors.stroke),
            )
        },
    ) {
        tabs.forEachIndexed { index, (filter, labelRes) ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onTabSelected(filter) },
                text = {
                    Text(
                        text = stringResource(labelRes),
                        style = if (index == selectedIndex) {
                            HallaakTheme.typography.label.large.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        } else {
                            HallaakTheme.typography.label.large
                        },
                        color = if (index == selectedIndex) {
                            HallaakTheme.colors.primary
                        } else {
                            HallaakTheme.colors.onSurfaceVariant
                        },
                    )
                },
            )
        }
    }
}
