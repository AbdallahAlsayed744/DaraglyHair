package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.location.UserLocationHolder
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetAvailableSlotsUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingDraftStateHolder
import com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotQuery
import com.daragly.daraglyhair.feature.shopbooking.domain.model.TransportationMode
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val DATE_STRIP_DAYS = 14

/** Shown when no device fix is available (e.g. location permission not granted yet). */
internal const val LOCATION_REQUIRED_MESSAGE = "location_required"

private fun today(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

private fun buildDateStrip(): List<LocalDate> {
    val startDay = today().toEpochDays()
    return (0 until DATE_STRIP_DAYS).map { LocalDate.fromEpochDays(startDay + it) }
}

class BookingDateTimeViewModel(
    private val shopId: String,
    private val getAvailableSlotsUseCase: GetAvailableSlotsUseCase,
    private val draftHolder: BookingDraftStateHolder,
    private val userLocationHolder: UserLocationHolder,
) : BaseViewModel<BookingDateTimeState, BookingDateTimeAction, BookingDateTimeEvent>(
    BookingDateTimeState(dates = buildDateStrip()),
) {

    init {
        val initialDate = draftHolder.current.date
            ?.takeIf { it >= today() && it in buildDateStrip() }
            ?: today()
        draftHolder.selectDate(initialDate)
        updateState { copy(selectedDate = initialDate) }
    }

    override fun onAction(action: BookingDateTimeAction) {
        when (action) {
            is BookingDateTimeAction.DateSelected -> onDateSelected(action.date)
            is BookingDateTimeAction.TransportationModeSelected -> onTransportationModeSelected(action.mode)
            is BookingDateTimeAction.SlotSelected -> onSlotSelected(action.slotStart)
            is BookingDateTimeAction.RetrySlots -> loadSlots()
            is BookingDateTimeAction.ContinueClicked -> onContinueClicked()
        }
    }

    // ── Transportation mode ───────────────────────────────────────────────────

    private fun onTransportationModeSelected(mode: TransportationMode) {
        if (state.value.transportationMode == mode) return
        draftHolder.selectTransportationMode(mode)
        updateState { copy(transportationMode = mode, slots = SectionState.Loading) }
        loadSlots()
    }

    // ── Date handling ─────────────────────────────────────────────────────────

    private fun onDateSelected(date: LocalDate) {
        if (state.value.selectedDate == date) return
        draftHolder.selectDate(date)
        updateState { copy(selectedDate = date) }
        loadSlots()
    }

    // ── Slots ─────────────────────────────────────────────────────────────────

    private fun loadSlots() {
        val draft = draftHolder.current
        val duration = draft.estimatedDurationMinutes
        val mode = state.value.transportationMode
        if (draft.shopId != shopId || duration <= 0 || draft.selectedServices.isEmpty() || mode == null) {
            // Flow entered without services, or no travel mode chosen yet — nothing to ask the API for.
            updateState { copy(slots = SectionState.Error("")) }
            return
        }
        updateState { copy(slots = SectionState.Loading) }
        viewModelScope.launch {
            val location = draft.userLocation
                ?: userLocationHolder.currentLocation()?.also { draftHolder.saveUserLocation(it) }
            if (location == null) {
                // No real fix yet (permission denied or no signal) — don't send stand-in coordinates.
                updateState { copy(slots = SectionState.Error(LOCATION_REQUIRED_MESSAGE)) }
                return@launch
            }
            val query = SlotQuery(
                shopId = shopId,
                shopBarberId = draft.shopBarberId,
                date = state.value.selectedDate ?: today(),
                durationMinutes = duration,
                userLatitude = location.latitude,
                userLongitude = location.longitude,
                transportationMode = mode,
                shopServiceIds = draft.selectedServices.map { it.shopServiceId },
            )
            when (val result = getAvailableSlotsUseCase(query)) {
                is Resource.Success -> {
                    val offer = result.model
                    // Keep the selected slot only if the fresh response still offers it as reachable.
                    val selectedStart = draftHolder.current.slot?.startAt
                    val stillValid = offer.slots.firstOrNull {
                        it.startAt == selectedStart && it.isReachable
                    }
                    if (selectedStart != null && stillValid == null) draftHolder.selectSlot(null)
                    updateState {
                        copy(
                            slots = SectionState.Data(offer),
                            selectedSlotStart = draftHolder.current.slot?.startAt,
                        )
                    }
                }
                is Resource.Failure -> updateState {
                    copy(slots = SectionState.Error(result.exception.message.orEmpty()))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun onSlotSelected(slotStart: Instant) {
        val slot = (state.value.slots as? SectionState.Data)?.model?.slots
            ?.firstOrNull { it.startAt == slotStart && it.isReachable }
            ?: return
        draftHolder.selectSlot(slot)
        updateState { copy(selectedSlotStart = slot.startAt) }
    }

    private fun onContinueClicked() {
        if (draftHolder.current.slot == null) return
        emitEvent(BookingDateTimeEvent.NavigateToReview(shopId))
    }
}
