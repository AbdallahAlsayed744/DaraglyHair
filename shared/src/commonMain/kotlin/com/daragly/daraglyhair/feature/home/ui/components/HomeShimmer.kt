package com.daragly.daraglyhair.feature.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.designsystem.util.HallaakShimmerBox

/**
 * Shimmer placeholder for a single service category chip
 */
@Composable
fun CategoryChipShimmer(modifier: Modifier = Modifier) {
    HallaakShimmerBox(
        modifier = modifier
            .width(100.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

/**
 * Shimmer placeholder for the service categories section
 */
@Composable
fun ServiceCategoriesShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section title shimmer
        HallaakShimmerBox(
            modifier = Modifier
                .padding(horizontal = HallaakTheme.dimensions.space16)
                .width(120.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        
        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space12))
        
        // Categories row shimmer
        LazyRow(
            contentPadding = PaddingValues(horizontal = HallaakTheme.dimensions.space16),
            horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
            userScrollEnabled = false
        ) {
            items(5) {
                CategoryChipShimmer()
            }
        }
    }
}

/**
 * Shimmer placeholder for upcoming appointment card
 */
@Composable
fun UpcomingAppointmentShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section title
        HallaakShimmerBox(
            modifier = Modifier
                .width(140.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        
        Spacer(modifier = Modifier.height(HallaakTheme.dimensions.space12))
        
        // Appointment card
        HallaakShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

/**
 * Shimmer placeholder for a single barbershop card
 */
@Composable
fun BarbershopCardShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = HallaakTheme.dimensions.space16,
                vertical = HallaakTheme.dimensions.space8
            )
    ) {
        HallaakShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

/**
 * Shimmer placeholder for nearby barbershops section header
 */
@Composable
fun SectionHeaderShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HallaakTheme.dimensions.space16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title shimmer
        HallaakShimmerBox(
            modifier = Modifier
                .width(160.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        
        // "See all" button shimmer
        HallaakShimmerBox(
            modifier = Modifier
                .width(80.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

/**
 * Full home screen shimmer content showing all loading states
 */
@Composable
fun HomeShimmerContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space24)
    ) {
        // Service Categories Shimmer
        ServiceCategoriesShimmer()
        
        // Upcoming Appointment Shimmer
        UpcomingAppointmentShimmer(
            modifier = Modifier.padding(horizontal = HallaakTheme.dimensions.space16)
        )
        
        // Nearby Barbershops Section Header
        SectionHeaderShimmer()
        
        // Nearby Barbershops Cards
        repeat(3) {
            BarbershopCardShimmer()
        }
    }
}
