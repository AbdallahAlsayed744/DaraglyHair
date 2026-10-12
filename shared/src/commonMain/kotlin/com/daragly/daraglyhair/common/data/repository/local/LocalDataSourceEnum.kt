package com.daragly.daraglyhair.common.data.repository.local

import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceEnum

/**
 * All DataStore preference keys for DaraglyHair.
 * Add new keys here as features require persistent local storage.
 */
enum class LocalDataSourceEnum(override val keyValue: String) : ILocalDataSourceEnum {
    ACCESS_TOKEN("access_token"),
    USER("user"),
    REMEMBER_ME("remember_me"),
    IS_AUTHENTICATED("is_authenticated"),
    LANGUAGE("language"),
    FIRST_LAUNCH_COMPLETE("first_launch_complete"),
    THEME("theme"),                              // "system" | "light" | "dark"
    NOTIFICATIONS_ENABLED("notifications_enabled"),
}
