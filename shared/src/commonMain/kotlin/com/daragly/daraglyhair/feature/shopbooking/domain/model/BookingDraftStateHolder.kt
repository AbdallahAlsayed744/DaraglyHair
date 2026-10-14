package com.daragly.daraglyhair.feature.shopbooking.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

/**
 * Koin single that keeps the [BookingDraft] alive across the booking-flow screens.
 * A fresh flow starts with [start]; a completed booking clears it.
 */
class BookingDraftStateHolder {

    private val _draft = MutableStateFlow(BookingDraft())
    val draft = _draft.asStateFlow()

    val current: BookingDraft get() = _draft.value

    /** Begins (or resumes) a booking flow for [shopId]; selections from another shop are discarded. */
    fun start(shopId: String, shopName: String?) {
        if (_draft.value.shopId == shopId) {
            if (shopName != null && _draft.value.shopName != shopName) {
                _draft.update { it.copy(shopName = shopName) }
            }
            return
        }
        _draft.value = BookingDraft(shopId = shopId, shopName = shopName)
    }

    fun selectBarber(shopBarberId: String?, barberId: String?, specialization: String?) {
        _draft.update {
            if (it.shopBarberId == shopBarberId && it.barberId == barberId) return@update it
            it.copy(
                shopBarberId = shopBarberId,
                barberId = barberId,
                barberSpecialization = specialization,
                slot = null,
            )
        }
    }

    fun selectServices(services: List<ShopService>) {
        _draft.update { it.copy(selectedServices = services, slot = null) }
    }

    fun selectDate(date: LocalDate) {
        // Invalidate the slot only when the date actually changes, so back-navigation keeps it.
        _draft.update {
            if (it.date == date) return@update it
            it.copy(date = date, slot = null)
        }
    }

    fun selectSlot(slot: BookingSlot?) {
        _draft.update { it.copy(slot = slot) }
    }

    fun setNotes(notes: String) {
        _draft.update { it.copy(notes = notes) }
    }

    fun clear() {
        _draft.value = BookingDraft()
    }
}
