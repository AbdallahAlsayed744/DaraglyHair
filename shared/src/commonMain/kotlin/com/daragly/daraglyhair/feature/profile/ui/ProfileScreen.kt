package com.daragly.daraglyhair.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.LocalPadding
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.profile_title
import daraglyhair.shared.generated.resources.settings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    navigator: INavigator = koinInject(),
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalPadding.current)
            .background(HallaakTheme.colors.background.canvas),
    ) {
        // ── Simple header ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HallaakTheme.colors.primary)
                .padding(
                    top    = 48.dp,
                    start  = HallaakTheme.dimensions.space20,
                    end    = HallaakTheme.dimensions.space20,
                    bottom = HallaakTheme.dimensions.space24,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(HallaakTheme.dimensions.avatarLarge)
                    .clip(CircleShape)
                    .background(HallaakTheme.colors.background.canvas.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint               = HallaakTheme.colors.background.surface,
                    modifier           = Modifier.size(52.dp),
                )
            }

            Text(
                text  = stringResource(Res.string.profile_title),
                style = HallaakTheme.typography.headline.medium,
                color = HallaakTheme.colors.background.surface,
            )
        }

        Spacer(Modifier.height(HallaakTheme.dimensions.space24))

        // ── Settings entry point ────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch { navigator.navigate(IMainGraph.Settings) }
                }
                .padding(
                    horizontal = HallaakTheme.dimensions.space16,
                    vertical   = HallaakTheme.dimensions.space16,
                ),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(HallaakTheme.colors.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Settings,
                    contentDescription = null,
                    tint               = HallaakTheme.colors.primary,
                    modifier           = Modifier.size(22.dp),
                )
            }

            Text(
                text     = stringResource(Res.string.settings),
                style    = HallaakTheme.typography.body.medium,
                color    = HallaakTheme.colors.onSurface,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector        = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
                tint               = HallaakTheme.colors.onSurfaceVariant,
                modifier           = Modifier.size(HallaakTheme.dimensions.iconSize),
            )
        }
    }
}
