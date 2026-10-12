package com.daragly.daraglyhair.feature.profile.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext

/**
 * Android actual — decodes the content URI stored in DataStore into a
 * [BitmapPainter] using only the Android framework (no extra library).
 */
@Composable
actual fun rememberUriPainter(uriString: String?): Painter? {
    val context = LocalContext.current
    return remember(uriString) {
        if (uriString.isNullOrBlank()) return@remember null
        runCatching {
            val uri = Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                // Downsample to ≤ 256 px on the longest side for the small avatar
                val opts = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(stream, null, opts)

                val maxDim = 256
                var sampleSize = 1
                val rawMax = maxOf(opts.outWidth, opts.outHeight)
                while (rawMax / sampleSize > maxDim) sampleSize *= 2

                context.contentResolver.openInputStream(uri)?.use { s2 ->
                    BitmapFactory.decodeStream(
                        s2, null,
                        android.graphics.BitmapFactory.Options().apply {
                            inSampleSize = sampleSize
                        },
                    )
                }
            }?.let { bitmap -> BitmapPainter(bitmap.asImageBitmap()) }
        }.getOrNull()
    }
}
