package com.daragly.daraglyhair.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Resolves a persisted URI string (from DataStore) into a [Painter]
 * that can be used directly in Compose Image composables.
 *
 * Returns null when:
 *   - uriString is null or blank
 *   - The URI cannot be opened (e.g. permission revoked)
 */
@Composable
expect fun rememberUriPainter(uriString: String?): Painter?
