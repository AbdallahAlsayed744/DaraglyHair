package com.daragly.daraglyhair.common.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.exceptions.IErrorKeyEnum
import androidx.navigation.NavOptionsBuilder
import com.daragly.daraglyhair.common.ui.eventcontroller.IEventController
import com.daragly.daraglyhair.common.ui.loading.ILoadingEvent
import com.daragly.daraglyhair.common.ui.message.HallaakMessageType
import com.daragly.daraglyhair.common.ui.message.IMessageEvent
import com.daragly.daraglyhair.common.ui.navigation.IDestination
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * Base ViewModel for DaraglyHair — mirrors Delivery-User's BaseViewModel exactly.
 *
 * What it provides:
 *  - [state]            : StateFlow<State>  — current screen state
 *  - [event]            : SharedFlow<Event> — one-shot side effects
 *  - [updateState]      : thread-safe state mutation
 *  - [emitEvent]        : fire a one-shot UI event
 *  - [fireLoading]      : emit a global loading event (mirrors Delivery-User)
 *  - [fireMessage]      : emit a global snackbar message (mirrors Delivery-User)
 *  - [collectResource]  : routes Flow<Resource<T>> to success/failure/loading
 *  - [onRequestValidation] : override to handle field-level validation errors
 *  - [onGlobalError]    : override to handle generic errors
 */
abstract class BaseViewModel<State, Action, Event>(
    initialState: State,
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    // ── Koin-injected global event buses ──────────────────────────────────
    private val navigator: INavigator by inject()

    private val messageEventController: IEventController<IMessageEvent>
            by inject(qualifier = named("MessageEvent"))

    private val loadingEventController: IEventController<ILoadingEvent>
            by inject(qualifier = named("LoadingEvent"))

    // ── Abstract ──────────────────────────────────────────────────────────

    abstract fun onAction(action: Action)

    // ── State helpers ─────────────────────────────────────────────────────

    fun updateState(transform: State.() -> State) {
        _state.update { it.transform() }
    }

    // ── Event helpers ─────────────────────────────────────────────────────

    fun emitEvent(event: Event) =
        viewModelScope.launch { _event.emit(event) }

    /**
     * Fires a global circular-progress loading event.
     * Mirrors Delivery-User's [BaseViewModel.fireLoading].
     */
    fun fireLoading(isLoading: Boolean) =
        viewModelScope.launch {
            loadingEventController.emit(ILoadingEvent.CircularProgressIndicator(isLoading))
        }

    /**
     * Fires a global snackbar message event.
     * Mirrors Delivery-User's [BaseViewModel.fireMessage].
     */
    fun fireMessage(message: String, type: HallaakMessageType = HallaakMessageType.ERROR) =
        viewModelScope.launch {
            messageEventController.emit(IMessageEvent.Snackbar(message, type))
        }

    /**
     * Navigates to a destination using the injected Navigator.
     * Mirrors Delivery-User's [BaseViewModel.fireNavigate].
     */
    fun fireNavigate(destination: IDestination, builder: NavOptionsBuilder.() -> Unit = {}) =
        viewModelScope.launch {
            navigator.navigate(destination, builder)
        }

    /**
     * Navigates up using the injected Navigator.
     * Mirrors Delivery-User's [BaseViewModel.fireNavigateUp].
     */
    suspend fun fireNavigateUp() {
        navigator.navigateUp()
    }

    // ── Resource collector ────────────────────────────────────────────────

    /**
     * Collects a Flow<Resource<Result>> and routes each emission:
     *   Loading(true/false) → [onLoading]
     *   Success             → [onSuccess]
     *   Failure             → central exception handler + [onFailure]
     *
     * Mirrors Delivery-User's Flow<Resource<Result>>.collectResource extension.
     */
    fun <Result> Flow<Resource<Result>>.collectResource(
        onSuccess: suspend (Result) -> Unit = {},
        onFailure: suspend (DaraglyHairException) -> Unit = {},
        onLoading: suspend (Boolean) -> Unit = { fireLoading(it) },
    ) = viewModelScope.launch {
        this@collectResource.collect { resource ->
            when (resource) {
                is Resource.Loading -> onLoading(resource.isLoading)
                is Resource.Success -> onSuccess(resource.model)
                is Resource.Failure -> {
                    handleException(resource.exception)
                    onFailure(resource.exception)
                }
            }
        }
    }

    // ── Exception handling ────────────────────────────────────────────────

    private fun handleException(exception: DaraglyHairException) {
        when (exception) {
            is DaraglyHairException.Local.RequestValidation ->
                onRequestValidation(exception.errors.mapValues { it.value.name })

            is DaraglyHairException.Client.ResponseValidation ->
                onRequestValidation(exception.errors)

            is DaraglyHairException.Client.UnAuthorized ->
                onGlobalError(exception.message ?: "Unauthorized — please sign in again")

            is DaraglyHairException.Network.Repeatable ->
                onGlobalError(exception.message ?: "Check your internet connection")

            is DaraglyHairException.Server.InternalServerError ->
                onGlobalError(exception.message ?: "Something went wrong on our side")

            else -> onGlobalError(exception.message ?: "Something went wrong")
        }
    }

    /**
     * Override to map field-level validation errors into screen state.
     * Mirrors Delivery-User's [BaseViewModel.onRequestValidation].
     */
    open fun onRequestValidation(errors: Map<IErrorKeyEnum, String>) {}

    /**
     * Override to handle generic / global errors.
     * Default: fires a global error snackbar — mirrors Delivery-User behaviour.
     */
    open fun onGlobalError(message: String) {
        fireMessage(message, HallaakMessageType.ERROR)
    }
}
