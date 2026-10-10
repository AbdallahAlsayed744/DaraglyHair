package com.daragly.daraglyhair.common.domain

import com.daragly.daraglyhair.common.data.DaraglyHairException

sealed class Resource<out Model> {
    data class Success<out Model>(val model: Model) : Resource<Model>()
    data class Failure(val exception: DaraglyHairException) : Resource<Nothing>()
    data class Loading(val isLoading: Boolean = false) : Resource<Nothing>()
}
