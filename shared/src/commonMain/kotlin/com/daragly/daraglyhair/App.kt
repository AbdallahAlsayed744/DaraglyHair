package com.daragly.daraglyhair

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.ic_hallaak_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    HallaakTheme {
        SplashScreen()
    }
}

@Composable
private fun SplashScreen() {
    // ── Animation state ────────────────────────
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val tagAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo pops in
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        )
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400),
        )
        // App name fades in after logo settles
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350, delayMillis = 100),
        )
        // Tagline fades in last
        tagAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, delayMillis = 150),
        )
    }

    // ── Background: Deep Slate Blue ────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            // ── Logo mark ─────────────────────
            Image(
                painter = painterResource(Res.drawable.ic_hallaak_logo),
                contentDescription = "Halla'ak Logo",
                modifier = Modifier
                    .size(148.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── App name: حلاقك ───────────────
            Text(
                text = "حلاقك",
                style = HallaakTheme.typography.headline.extraLarge,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha.value),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tagline ───────────────────────
            Text(
                text = "حلاقك في جيبك",
                style = HallaakTheme.typography.body.medium,
                color = HallaakTheme.colors.gold,
                modifier = Modifier.alpha(tagAlpha.value),
            )
        }

        // ── Bottom version label ───────────────
        Text(
            text = "v1.0",
            style = HallaakTheme.typography.label.small,
            color = Color.White.copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(tagAlpha.value)
                .then(Modifier.height(32.dp)),
        )
    }
}
