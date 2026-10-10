package com.daragly.daraglyhair.common.ui.language

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import com.daragly.daraglyhair.common.ui.eventcontroller.IEventController
import com.daragly.daraglyhair.common.ui.extension.ObserveAsEvents
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

/**
 * Android implementation of language change observer.
 * Mirrors Delivery-User's ObserveLanguageEvent exactly.
 */
@Composable
actual fun ObserveLanguageEvent() {
    val context = LocalContext.current
    val languageEvent: IEventController<ILanguageEvent> =
        koinInject(qualifier = named("LanguageEvent"))

    ObserveAsEvents(languageEvent.event) { event ->
        when (event) {
            is ILanguageEvent.ChangeLanguage -> {
                val compatLocales =
                    LocaleListCompat.forLanguageTags(event.languageCode)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val localeManager = context.getSystemService(LocaleManager::class.java)
                    val platformLocales = compatLocales.unwrap()
                    localeManager.applicationLocales = platformLocales as LocaleList
                } else {
                    AppCompatDelegate.setApplicationLocales(compatLocales)
                }
            }
        }
    }
}
