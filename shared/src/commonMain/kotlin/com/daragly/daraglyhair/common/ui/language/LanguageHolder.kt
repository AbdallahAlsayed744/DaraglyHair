package com.daragly.daraglyhair.common.ui.language

/**
 * In-memory copy of the app language, kept so network code can set `Accept-Language`
 * without a suspending DataStore read on every request.
 */
class LanguageHolder(defaultLanguageCode: String = DEFAULT_LANGUAGE) {

    private val fallback: String = defaultLanguageCode

    // Written from the UI, read from request threads; a torn read only mislabels one request.
    var languageCode: String = defaultLanguageCode
        set(value) {
            field = value.ifBlank { fallback }
        }

    companion object {
        const val DEFAULT_LANGUAGE = "ar"
    }
}
