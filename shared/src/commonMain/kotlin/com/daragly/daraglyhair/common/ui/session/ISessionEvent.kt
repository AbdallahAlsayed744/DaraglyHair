package com.daragly.daraglyhair.common.ui.session

/**
 * Emitted when the stored session can no longer be used or refreshed,
 * so the app can leave the authenticated graph instead of looping on 401s.
 */
sealed interface ISessionEvent {
    data object Expired : ISessionEvent
}
