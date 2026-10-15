package com.daragly.daraglyhair.feature.shopbooking.ui

import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BarberProfile
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopBarber
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopService
import com.daragly.daraglyhair.feature.shopbooking.domain.model.TransportationMode

// ── Shop Details ───────────────────────────────────────────────────────────────

data class ShopDetailsState(
    val shop: SectionState<Barbershop> = SectionState.Loading,
    val services: SectionState<List<ShopService>> = SectionState.Loading,
    val barbers: SectionState<List<ShopBarber>> = SectionState.Loading,
    /** Best-effort profile map: barberId → profile (null values mean "unknown"). */
    val barberProfiles: Map<String, BarberProfile> = emptyMap(),
) {
    val shopData: Barbershop? get() = (shop as? SectionState.Data)?.model
    val canBookOnline: Boolean get() = shopData?.let { it.isActive && it.isBookingEnabled } == true
}

sealed interface ShopDetailsAction {
    data object RetryShop : ShopDetailsAction
    data object RetryServices : ShopDetailsAction
    data object RetryBarbers : ShopDetailsAction
    data class BarberClicked(val shopBarber: ShopBarber) : ShopDetailsAction
    data object BookClicked : ShopDetailsAction
}

sealed interface ShopDetailsEvent {
    data class NavigateToBarber(val shopId: String, val barberId: String) : ShopDetailsEvent
    data class NavigateToBookingServices(val shopId: String) : ShopDetailsEvent
}

// ── Barber Details ─────────────────────────────────────────────────────────────

data class BarberDetailsState(
    val barber: SectionState<BarberProfile> = SectionState.Loading,
    val shop: SectionState<Barbershop> = SectionState.Loading,
    val shopBarber: ShopBarber? = null,
) {
    val barberData: BarberProfile? get() = (barber as? SectionState.Data)?.model
    val shopData: Barbershop? get() = (shop as? SectionState.Data)?.model

    val bookBlockReason: BookBlockReason?
        get() {
            val barberProfile = barberData ?: return BookBlockReason.LOADING
            val relationship = shopBarber ?: return BookBlockReason.NOT_IN_SHOP
            val shopModel = shopData ?: return null
            return when {
                !relationship.isBookable -> BookBlockReason.RELATIONSHIP_DISABLED
                !barberProfile.isActive -> BookBlockReason.BARBER_INACTIVE
                !shopModel.isActive -> BookBlockReason.SHOP_INACTIVE
                !shopModel.isBookingEnabled -> BookBlockReason.SHOP_BOOKING_DISABLED
                else -> null
            }
        }
}

enum class BookBlockReason {
    LOADING,
    NOT_IN_SHOP,
    RELATIONSHIP_DISABLED,
    BARBER_INACTIVE,
    SHOP_INACTIVE,
    SHOP_BOOKING_DISABLED,
}

sealed interface BarberDetailsAction {
    data object Retry : BarberDetailsAction
    data object BookClicked : BarberDetailsAction
}

sealed interface BarberDetailsEvent {
    data class NavigateToBookingServices(val shopId: String) : BarberDetailsEvent
}

// ── Booking: Select Services ───────────────────────────────────────────────────

data class BookingServicesState(
    val services: SectionState<List<ShopService>> = SectionState.Loading,
    val selectedServiceIds: Set<String> = emptySet(),
) {
    val selectedServices: List<ShopService>
        get() = (services as? SectionState.Data)?.model
            ?.filter { selectedServiceIds.contains(it.shopServiceId) }
            ?: emptyList()
}

sealed interface BookingServicesAction {
    data object Retry : BookingServicesAction
    data class ToggleService(val service: ShopService) : BookingServicesAction
    data object ContinueClicked : BookingServicesAction
}

sealed interface BookingServicesEvent {
    data class NavigateToDateTime(val shopId: String) : BookingServicesEvent
}

// ── Booking: Date & Time ───────────────────────────────────────────────────────

data class BookingDateTimeState(
    val dates: List<kotlinx.datetime.LocalDate> = emptyList(),
    val selectedDate: kotlinx.datetime.LocalDate? = null,
    /** Slots are only requested after the user picks how they will travel. */
    val transportationMode: TransportationMode? = null,
    val slots: SectionState<com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotOffer> = SectionState.Loading,
    val selectedSlotStart: kotlinx.datetime.Instant? = null,
)

sealed interface BookingDateTimeAction {
    data class DateSelected(val date: kotlinx.datetime.LocalDate) : BookingDateTimeAction
    data class TransportationModeSelected(val mode: TransportationMode) : BookingDateTimeAction
    data class SlotSelected(val slotStart: kotlinx.datetime.Instant) : BookingDateTimeAction
    data object RetrySlots : BookingDateTimeAction
    data object ContinueClicked : BookingDateTimeAction
}

sealed interface BookingDateTimeEvent {
    data class NavigateToReview(val shopId: String) : BookingDateTimeEvent
}

// ── Booking: Review ────────────────────────────────────────────────────────────

data class BookingReviewState(
    val draft: com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingDraft =
        com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingDraft(),
    val isSubmitting: Boolean = false,
    val hasConflict: Boolean = false,
    val errorMessage: String? = null,
) {
    val canConfirm: Boolean get() = !isSubmitting && draft.isValidForSubmission
}

sealed interface BookingReviewAction {
    data class NotesChanged(val notes: String) : BookingReviewAction
    data object ConfirmClicked : BookingReviewAction
    data object ChooseAnotherTimeClicked : BookingReviewAction
    data object DismissMessages : BookingReviewAction
}

sealed interface BookingReviewEvent {
    data class NavigateToSuccess(val shopId: String, val appointmentId: String) : BookingReviewEvent
    data object NavigateToDateTime : BookingReviewEvent
}

// ── Booking: Success ───────────────────────────────────────────────────────────

data class BookingSuccessState(
    val appointmentId: String = "",
    val details: SectionState<BookingAppointment> = SectionState.Loading,
)

sealed interface BookingSuccessAction {
    data object RetryDetails : BookingSuccessAction
    data object ViewDetailsClicked : BookingSuccessAction
    data object OpenBookingsClicked : BookingSuccessAction
    data object BackToShopClicked : BookingSuccessAction
}

sealed interface BookingSuccessEvent {
    data class NavigateToAppointmentDetail(val appointmentId: String) : BookingSuccessEvent
    data object OpenBookingsTab : BookingSuccessEvent
    data class NavigateToShopDetails(val shopId: String) : BookingSuccessEvent
}
