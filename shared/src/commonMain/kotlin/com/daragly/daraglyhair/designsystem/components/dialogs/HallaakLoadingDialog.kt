package com.daragly.daraglyhair.designsystem.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakLoadingDialog
//
//  Blocks UI while an async operation runs.
//  24dp rounded card, gold progress indicator,
//  optional loading message.
// ─────────────────────────────────────────────

@Composable
fun HallaakLoadingDialog(
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = modifier
                .background(
                    color = HallaakTheme.colors.background.surfaceHigh,
                    shape = HallaakTheme.shapes.dialog,
                )
                .padding(horizontal = 40.dp, vertical = 32.dp),
            verticalArrangement   = Arrangement.spacedBy(16.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                modifier   = Modifier.size(48.dp),
                color      = HallaakTheme.colors.gold,
                trackColor = HallaakTheme.colors.background.disabled,
                strokeWidth = 4.dp,
            )

            message?.let {
                Text(
                    text  = it,
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}
