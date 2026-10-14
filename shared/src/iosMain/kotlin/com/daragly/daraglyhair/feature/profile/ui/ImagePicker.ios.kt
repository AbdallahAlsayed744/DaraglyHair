package com.daragly.daraglyhair.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/** iOS image picker — placeholder until PHPickerViewController integration is added. */
@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (uriString: String, painter: Painter, bytes: ByteArray?, extension: String?) -> Unit,
): () -> Unit = { /* no-op on iOS until implemented */ }
