package com.daragly.daraglyhair.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Returns a lambda that, when invoked, opens the platform image picker.
 * The [onImagePicked] callback receives:
 *   - uriString : the content URI string to store/persist
 *   - painter   : a Painter ready for immediate display in Compose
 *
 * The actual implementation lives in androidMain (and iosMain for iOS).
 */
@Composable
expect fun rememberImagePickerLauncher(
    onImagePicked: (uriString: String, painter: Painter) -> Unit,
): () -> Unit
