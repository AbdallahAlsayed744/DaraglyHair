package com.daragly.daraglyhair.feature.profile.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation — opens the system image picker (GET_CONTENT).
 * Decodes the selected image into a Compose BitmapPainter using only the
 * Android framework — no additional image-loading library required.
 */
@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (uriString: String, painter: Painter) -> Unit,
): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        // Persist read permission so the URI remains accessible after restart
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }

        // Decode the bitmap (downsampled to avoid OOM on large images)
        val bitmap = runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                // First pass: measure image dimensions
                val opts = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(stream, null, opts)

                // Calculate sample size to cap at ~512px on the longest side
                val maxDim = 512
                var sampleSize = 1
                val rawMax = maxOf(opts.outWidth, opts.outHeight)
                while (rawMax / sampleSize > maxDim) sampleSize *= 2

                // Second pass: decode at reduced size
                context.contentResolver.openInputStream(uri)?.use { s2 ->
                    BitmapFactory.decodeStream(
                        s2, null,
                        android.graphics.BitmapFactory.Options().apply {
                            inSampleSize = sampleSize
                        },
                    )
                }
            }
        }.getOrNull()

        if (bitmap != null) {
            val painter: Painter = BitmapPainter(bitmap.asImageBitmap())
            onImagePicked(uri.toString(), painter)
        }
    }

    return { launcher.launch("image/*") }
}
