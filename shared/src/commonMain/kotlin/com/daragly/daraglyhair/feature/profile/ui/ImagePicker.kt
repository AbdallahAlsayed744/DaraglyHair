package com.daragly.daraglyhair.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Returns a lambda that, when invoked, opens the platform image picker.
 * The [onImagePicked] callback receives:
 *   - uriString : the content URI string to store/persist
 *   - painter   : a Painter ready for immediate display in Compose
 *   - bytes     : the raw image bytes for upload (null when the platform cannot read them)
 *   - extension : file extension including the dot, e.g. ".jpg"
 *
 * The actual implementation lives in androidMain (and iosMain for iOS).
 */
@Composable
expect fun rememberImagePickerLauncher(
    onImagePicked: (uriString: String, painter: Painter, bytes: ByteArray?, extension: String?) -> Unit,
): () -> Unit
