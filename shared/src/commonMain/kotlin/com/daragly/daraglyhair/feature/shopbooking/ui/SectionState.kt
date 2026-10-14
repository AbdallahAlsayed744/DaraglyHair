package com.daragly.daraglyhair.feature.shopbooking.ui

/**
 * Generic per-section content state for the shop-booking screens, so one failed
 * section request does not hide successfully loaded sections.
 */
sealed interface SectionState<out M> {
    data object Loading : SectionState<Nothing>
    data class Data<M>(val model: M) : SectionState<M>
    data class Error(val message: String) : SectionState<Nothing>
}
