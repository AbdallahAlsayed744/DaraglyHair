package com.daragly.daraglyhair.designsystem.components.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakScreen
//
//  Full-size screen wrapper with:
//    • Status bar + nav bar padding
//    • Cream/canvas background
//    • Optional scroll state
//    • IME padding for input screens
//    • Header + content slots (mirrors DelivaryUserScreen)
// ─────────────────────────────────────────────

@Composable
fun HallaakScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    scrollState: ScrollState? = null,
    isImePaddingEnabled: Boolean = true,
    contentVerticalArrangement: Arrangement.Vertical = Arrangement.Top,
    contentHorizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    header: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollModifier = if (scrollState != null) Modifier.verticalScroll(scrollState)
                         else Modifier
    val imeModifier    = if (isImePaddingEnabled) Modifier.imePadding() else Modifier

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .then(modifier)
            .background(color = HallaakTheme.colors.background.canvas),
        verticalArrangement   = Arrangement.Top,
        horizontalAlignment   = Alignment.CenterHorizontally,
    ) {
        header()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(scrollModifier)
                .padding(
                    start = HallaakTheme.dimensions.space16,
                    end   = HallaakTheme.dimensions.space16,
                )
                .then(imeModifier)
                .padding(contentPadding),
            verticalArrangement   = contentVerticalArrangement,
            horizontalAlignment   = contentHorizontalAlignment,
            content               = content,
        )
    }
}
