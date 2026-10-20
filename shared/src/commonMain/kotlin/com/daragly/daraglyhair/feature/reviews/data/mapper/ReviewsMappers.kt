package com.daragly.daraglyhair.feature.reviews.data.mapper

import com.daragly.daraglyhair.feature.reviews.data.dto.ReviewDto
import com.daragly.daraglyhair.feature.reviews.domain.model.Review

fun ReviewDto.toDomain(): Review = Review(
    id = id,
    shopId = shopId,
    appointmentId = appointmentId,
    overallRating = overallRating,
    qualityRating = qualityRating,
    cleanlinessRating = cleanlinessRating,
    valueRating = valueRating,
    comment = comment?.takeIf { it.isNotBlank() },
    photoUrls = images?.mapNotNull { it.resolvedUrl() } ?: emptyList(),
    createdOn = createdOn,
)
