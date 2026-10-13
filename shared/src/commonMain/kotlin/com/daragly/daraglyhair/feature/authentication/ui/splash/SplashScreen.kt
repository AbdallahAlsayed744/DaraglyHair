package com.daragly.daraglyhair.feature.authentication.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.common.ui.extension.ObserveAsEvents
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.app_name
import daraglyhair.shared.generated.resources.cd_app_logo
import daraglyhair.shared.generated.resources.ic_hallaak_logo
import daraglyhair.shared.generated.resources.splash_tagline
import daraglyhair.shared.generated.resources.splash_version
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel(),
) {
    SplashContent()
}

@Composable
private fun SplashContent() {
    val primary = HallaakTheme.colors.primary
    val dark    = Color(0xFF0A1520)

    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val tagAlpha  = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run logo scale + alpha in parallel, then sequence text/tag
        launch {
            logoScale.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        }
        logoAlpha.animateTo(1f, tween(500))
        textAlpha.animateTo(1f, tween(350, delayMillis = 120))
        tagAlpha.animateTo(1f, tween(300, delayMillis = 200))
    }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(dark, primary))),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter            = painterResource(Res.drawable.ic_hallaak_logo),
                contentDescription = stringResource(Res.string.cd_app_logo),
                modifier           = Modifier
                    .size(140.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text     = stringResource(Res.string.app_name),
                style    = HallaakTheme.typography.headline.extraLarge,
                color    = Color.White,
                modifier = Modifier.alpha(textAlpha.value),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text     = stringResource(Res.string.splash_tagline),
                style    = HallaakTheme.typography.body.medium,
                color    = HallaakTheme.colors.gold,
                modifier = Modifier.alpha(tagAlpha.value),
            )
        }

        Text(
            text     = stringResource(Res.string.splash_version),
            style    = HallaakTheme.typography.label.small,
            color    = Color.White.copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(tagAlpha.value),
        )
    }
}
