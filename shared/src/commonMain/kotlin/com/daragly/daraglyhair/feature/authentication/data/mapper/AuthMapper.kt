package com.daragly.daraglyhair.feature.authentication.data.mapper

import com.daragly.daraglyhair.feature.authentication.data.dto.TokenDto
import com.daragly.daraglyhair.feature.authentication.domain.model.AuthToken

object AuthMapper {
    fun toDomain(dto: TokenDto) = AuthToken(
        token                 = dto.token.orEmpty(),
        refreshToken          = dto.refreshToken.orEmpty(),
        refreshTokenExpiryTime = dto.refreshTokenExpiryTime.orEmpty(),
    )
}
