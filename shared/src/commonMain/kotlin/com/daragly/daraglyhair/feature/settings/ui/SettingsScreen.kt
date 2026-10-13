package com.daragly.daraglyhair.feature.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.LocalPadding
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.profile.ui.rememberUriPainter
import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.about_app_icon_letter
import daraglyhair.shared.generated.resources.about_app_name
import daraglyhair.shared.generated.resources.about_app_tagline
import daraglyhair.shared.generated.resources.about_version
import daraglyhair.shared.generated.resources.cancel
import daraglyhair.shared.generated.resources.change_password
import daraglyhair.shared.generated.resources.coming_soon
import daraglyhair.shared.generated.resources.contact_support
import daraglyhair.shared.generated.resources.edit_profile_title
import daraglyhair.shared.generated.resources.enable_notifications
import daraglyhair.shared.generated.resources.greeting_with_name
import daraglyhair.shared.generated.resources.help_center
import daraglyhair.shared.generated.resources.language_arabic
import daraglyhair.shared.generated.resources.language_arabic_latin
import daraglyhair.shared.generated.resources.language_english
import daraglyhair.shared.generated.resources.notifications_subtitle
import daraglyhair.shared.generated.resources.privacy_policy
import daraglyhair.shared.generated.resources.section_about
import daraglyhair.shared.generated.resources.section_account
import daraglyhair.shared.generated.resources.section_appearance
import daraglyhair.shared.generated.resources.section_help_support
import daraglyhair.shared.generated.resources.section_language
import daraglyhair.shared.generated.resources.section_notifications
import daraglyhair.shared.generated.resources.section_privacy_security
import daraglyhair.shared.generated.resources.send_reset_link
import daraglyhair.shared.generated.resources.send_reset_link_email
import daraglyhair.shared.generated.resources.settings
import daraglyhair.shared.generated.resources.sign_out
import daraglyhair.shared.generated.resources.sign_out_message
import daraglyhair.shared.generated.resources.terms_conditions
import daraglyhair.shared.generated.resources.theme_dark
import daraglyhair.shared.generated.resources.theme_light
import daraglyhair.shared.generated.resources.theme_system
import daraglyhair.shared.generated.resources.user_fallback
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    // Resolve stored image URI → Painter every time user.image changes
    val resolvedPainter = rememberUriPainter(state.user.image)
    LaunchedEffect(resolvedPainter) {
        viewModel.onAction(SettingsAction.UserImagePainterResolved(resolvedPainter))
    }

    // Reload user data each time this screen resumes (e.g. back from EditProfile)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onAction(SettingsAction.ReloadUser)
        }
    }

    // Side-effect observer
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                SettingsEvent.NavigateBack -> navigator.navigateUp()
                SettingsEvent.NavigateToLogin -> navigator.navigate(IAuthGraph.Login) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }

                SettingsEvent.NavigateToProfile -> navigator.navigateUp()
                SettingsEvent.NavigateToEditProfile -> navigator.navigate(IMainGraph.EditProfile)
                SettingsEvent.NavigateToForgotPassword -> navigator.navigate(IAuthGraph.ForgotPassword)
                SettingsEvent.NavigateToNotifications -> Unit
                is SettingsEvent.OpenUrl -> runCatching { uriHandler.openUri(event.url) }
            }
        }
    }

    // Sign-out dialog
    if (state.showSignOutDialog) {
        SignOutDialog(
            onConfirm = { viewModel.onAction(SettingsAction.SignOutConfirmed) },
            onDismiss = { viewModel.onAction(SettingsAction.SignOutDismissed) },
        )
    }

    // Root — plain background, no HallaakTopBar, exactly like Favorites/Home
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = LocalPadding.current.calculateBottomPadding() + HallaakTheme.dimensions.space16,
            ),
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
        ) {
            // ── Hero header (same pattern as FavoritesHeader / HomeHeader) ──
            item(key = "header") {
                SettingsHeroHeader(state = state)
            }

            item(key = "space_after_header") {
                Spacer(Modifier.height(HallaakTheme.dimensions.space16))
            }

            // A. Account
            item(key = "sec_account") {
                SectionHeader(Res.string.section_account)
            }
            item(key = "card_account") {
                AccountCard(
                    state = state,
                    onEditProfile = { viewModel.onAction(SettingsAction.EditProfileClicked) },
                    onChangePassword = { viewModel.onAction(SettingsAction.ChangePasswordClicked) },
                )
            }

            // B. Appearance
            item(key = "sec_appearance") {
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                SectionHeader(Res.string.section_appearance)
            }
            item(key = "card_appearance") {
                AppearanceCard(
                    selectedTheme = state.theme,
                    onThemeSelected = { viewModel.onAction(SettingsAction.ThemeSelected(it)) },
                )
            }

            // C. Language
            item(key = "sec_language") {
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                SectionHeader(Res.string.section_language)
            }
            item(key = "card_language") {
                LanguageCard(
                    selectedLanguage = state.language,
                    onLanguageSelected = { viewModel.onAction(SettingsAction.LanguageSelected(it)) },
                )
            }

            // D. Notifications
            item(key = "sec_notif") {
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                SectionHeader(Res.string.section_notifications)
            }
            item(key = "card_notif") {
                NotificationsCard(
                    notificationsEnabled = state.notificationsEnabled,
                    onToggle = { viewModel.onAction(SettingsAction.NotificationsToggled(it)) },
                )
            }

            // E. Privacy & Security — Change Password ONLY
            item(key = "sec_privacy") {
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                SectionHeader(Res.string.section_privacy_security)
            }
            item(key = "card_privacy") {
                PrivacyCard(
                    onChangePassword = { viewModel.onAction(SettingsAction.ChangePasswordClicked) },
                )
            }

            // F. Help & Support
            item(key = "sec_help") {
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                SectionHeader(Res.string.section_help_support)
            }
            item(key = "card_help") { HelpCard() }

            // G. About — Privacy Policy + Terms only here
            item(key = "sec_about") {
                Spacer(Modifier.height(HallaakTheme.dimensions.space8))
                SectionHeader(Res.string.section_about)
            }
            item(key = "card_about") {
                AboutCard(
                    onPrivacyPolicy = { viewModel.onAction(SettingsAction.PrivacyPolicyClicked) },
                    onTerms = { viewModel.onAction(SettingsAction.TermsClicked) },
                )
            }

            // H. Sign out
            item(key = "signout") {
                Spacer(Modifier.height(HallaakTheme.dimensions.space16))
                SignOutRow(
                    isLoading = state.isSigningOut,
                    onClick = { viewModel.onAction(SettingsAction.SignOutClicked) },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Hero header — same structure as FavoritesHeader / HomeHeader
//  Primary-blue block, padding(top=48.dp) so it fills behind the status bar
//  Avatar on the right, name + subtitle on the left (RTL-aware)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsHeroHeader(
    state: SettingsState,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HallaakTheme.colors.primary)
            .padding(
                top = 48.dp,
                start = HallaakTheme.dimensions.space20,
                end = HallaakTheme.dimensions.space20,
                bottom = HallaakTheme.dimensions.space24,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Title + greeting
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
        ) {
            Text(
                text = stringResource(Res.string.settings),
                style = HallaakTheme.typography.headline.large,
                color = HallaakTheme.colors.background.canvas,
            )
            val displayName = state.user.name.trim()
                .ifBlank { stringResource(Res.string.user_fallback) }
            Text(
                text = stringResource(Res.string.greeting_with_name, displayName),
                style = HallaakTheme.typography.body.medium,
                color = HallaakTheme.colors.background.canvas.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.size(HallaakTheme.dimensions.space12))

        // User avatar (real image if available)
        UserAvatar(
            imagePainter = state.userImagePainter,
            isLoading = state.isLoadingUser,
            size = HallaakTheme.dimensions.avatarMedium.value.toInt(),
            borderColor = HallaakTheme.colors.background.canvas.copy(alpha = 0.4f),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Section header label
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: StringResource) {
    Text(
        text = stringResource(title),
        style = HallaakTheme.typography.label.extraLarge,
        color = HallaakTheme.colors.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = HallaakTheme.dimensions.space16,
                end = HallaakTheme.dimensions.space16,
                bottom = HallaakTheme.dimensions.space4,
            ),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Card wrapper
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HallaakTheme.dimensions.space16),
        shape = HallaakTheme.shapes.card,
        colors = CardDefaults.cardColors(containerColor = HallaakTheme.colors.background.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = { content() },
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Generic settings row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    showArrow: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(
                horizontal = HallaakTheme.dimensions.space16,
                vertical = HallaakTheme.dimensions.space12,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(HallaakTheme.colors.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HallaakTheme.colors.primary,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = HallaakTheme.typography.body.small,
                color = HallaakTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = HallaakTheme.typography.label.large,
                    color = HallaakTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        when {
            trailingContent != null -> trailingContent()
            showArrow -> Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = HallaakTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(HallaakTheme.dimensions.iconSize),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  User avatar — real image or placeholder icon
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun UserAvatar(
    imagePainter: Painter?,
    isLoading: Boolean,
    size: Int,
    modifier: Modifier = Modifier,
    borderColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
) {
    val borderMod = if (borderColor != androidx.compose.ui.graphics.Color.Transparent)
        modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(borderColor)
            .padding(2.dp)
            .clip(CircleShape)
            .background(HallaakTheme.colors.primary.copy(alpha = 0.12f))
    else
        modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(HallaakTheme.colors.primary.copy(alpha = 0.12f))

    Box(
        modifier = borderMod,
        contentAlignment = Alignment.Center,
    ) {
        when {
            isLoading -> CircularProgressIndicator(
                modifier = Modifier.size((size * 0.35f).dp),
                color = HallaakTheme.colors.primary,
                strokeWidth = 2.dp,
            )

            imagePainter != null -> Image(
                painter = imagePainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            else -> Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                tint = HallaakTheme.colors.background.canvas,
                modifier = Modifier.size((size * 0.65f).dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  A. Account card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AccountCard(
    state: SettingsState,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
) {
    SettingsCard {
        Column {
            // Mini profile summary row inside the card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HallaakTheme.dimensions.space16),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
            ) {
                UserAvatar(
                    imagePainter = state.userImagePainter,
                    isLoading = state.isLoadingUser,
                    size = HallaakTheme.dimensions.avatarMedium.value.toInt(),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
                ) {
                    if (state.isLoadingUser) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = HallaakTheme.colors.primary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = state.user.name.ifBlank { stringResource(Res.string.user_fallback) },
                            style = HallaakTheme.typography.title.extraLarge,
                            color = HallaakTheme.colors.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (state.user.email.isNotBlank()) {
                            ContactLine(icon = Icons.Outlined.Email, label = state.user.email)
                        }
                        if (state.user.phone.isNotBlank()) {
                            ContactLine(icon = Icons.Outlined.Phone, label = state.user.phone)
                        }
                    }
                }
            }

            HorizontalDivider(color = HallaakTheme.colors.stroke, thickness = 0.5.dp)

            SettingsRow(
                icon = Icons.Outlined.Person,
                title = stringResource(Res.string.edit_profile_title),
                onClick = onEditProfile,
            )

            HorizontalDivider(color = HallaakTheme.colors.stroke, thickness = 0.5.dp)

            SettingsRow(
                icon = Icons.Outlined.Lock,
                title = stringResource(Res.string.change_password),
                subtitle = stringResource(Res.string.send_reset_link),
                onClick = onChangePassword,
            )
        }
    }
}

@Composable
private fun ContactLine(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HallaakTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = label,
            style = HallaakTheme.typography.label.large,
            color = HallaakTheme.colors.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  B. Appearance card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AppearanceCard(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
) {
    data class Option(val theme: AppTheme, val labelRes: StringResource)

    val options = listOf(
        Option(AppTheme.SYSTEM, Res.string.theme_system),
        Option(AppTheme.LIGHT, Res.string.theme_light),
        Option(AppTheme.DARK, Res.string.theme_dark),
    )

    SettingsCard {
        Column {
            options.forEachIndexed { idx, opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeSelected(opt.theme) }
                        .padding(
                            horizontal = HallaakTheme.dimensions.space16,
                            vertical = HallaakTheme.dimensions.space4,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(opt.labelRes),
                        style = HallaakTheme.typography.body.small,
                        color = HallaakTheme.colors.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    RadioButton(
                        selected = selectedTheme == opt.theme,
                        onClick = { onThemeSelected(opt.theme) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = HallaakTheme.colors.primary,
                            unselectedColor = HallaakTheme.colors.onSurfaceVariant,
                        ),
                    )
                }
                if (idx < options.lastIndex) {
                    HorizontalDivider(color = HallaakTheme.colors.stroke, thickness = 0.5.dp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  C. Language card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LanguageCard(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
) {
    data class LangOpt(
        val code: String,
        val nativeLabelRes: StringResource,
        val latinLabelRes: StringResource,
    )

    val options = listOf(
        LangOpt("ar", Res.string.language_arabic, Res.string.language_arabic_latin),
        LangOpt("en", Res.string.language_english, Res.string.language_english),
    )

    SettingsCard {
        Column {
            options.forEachIndexed { idx, opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected(opt.code) }
                        .padding(
                            horizontal = HallaakTheme.dimensions.space16,
                            vertical = HallaakTheme.dimensions.space4,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(opt.nativeLabelRes),
                            style = HallaakTheme.typography.body.small,
                            color = HallaakTheme.colors.onSurface
                        )
                        Text(
                            text = stringResource(opt.latinLabelRes),
                            style = HallaakTheme.typography.label.large,
                            color = HallaakTheme.colors.onSurfaceVariant
                        )
                    }
                    RadioButton(
                        selected = selectedLanguage == opt.code,
                        onClick = { onLanguageSelected(opt.code) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = HallaakTheme.colors.primary,
                            unselectedColor = HallaakTheme.colors.onSurfaceVariant,
                        ),
                    )
                }
                if (idx < options.lastIndex) {
                    HorizontalDivider(color = HallaakTheme.colors.stroke, thickness = 0.5.dp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  D. Notifications card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NotificationsCard(
    notificationsEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    SettingsCard {
        SettingsRow(
            icon = Icons.Outlined.Notifications,
            title = stringResource(Res.string.enable_notifications),
            subtitle = stringResource(Res.string.notifications_subtitle),
            showArrow = false,
            trailingContent = {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = HallaakTheme.colors.background.surface,
                        checkedTrackColor = HallaakTheme.colors.primary,
                        uncheckedThumbColor = HallaakTheme.colors.background.surface,
                        uncheckedTrackColor = HallaakTheme.colors.onSurfaceVariant.copy(alpha = 0.4f),
                    ),
                )
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  E. Privacy & Security — Change Password ONLY
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PrivacyCard(
    onChangePassword: () -> Unit,
) {
    SettingsCard {
        SettingsRow(
            icon = Icons.Outlined.Lock,
            title = stringResource(Res.string.change_password),
            subtitle = stringResource(Res.string.send_reset_link_email),
            onClick = onChangePassword,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  F. Help & Support
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HelpCard() {
    SettingsCard {
        Column {
            SettingsRow(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                title = stringResource(Res.string.help_center),
                subtitle = stringResource(Res.string.coming_soon),
                showArrow = false,
            )
            HorizontalDivider(color = HallaakTheme.colors.stroke, thickness = 0.5.dp)
            SettingsRow(
                icon = Icons.Outlined.Email,
                title = stringResource(Res.string.contact_support),
                subtitle = stringResource(Res.string.coming_soon),
                showArrow = false,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  G. About — Privacy Policy + Terms & Conditions here only
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AboutCard(
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
) {
    SettingsCard {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HallaakTheme.dimensions.space16),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(HallaakTheme.shapes.card)
                        .background(HallaakTheme.colors.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.about_app_icon_letter),
                        style = HallaakTheme.typography.headline.medium,
                        color = HallaakTheme.colors.background.surface,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
                ) {
                    Text(
                        text = stringResource(Res.string.about_app_name),
                        style = HallaakTheme.typography.title.extraLarge,
                        color = HallaakTheme.colors.onSurface,
                    )
                    Text(
                        text = stringResource(Res.string.about_app_tagline),
                        style = HallaakTheme.typography.label.large,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(Res.string.about_version),
                        style = HallaakTheme.typography.label.medium,
                        color = HallaakTheme.colors.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
            }

            HorizontalDivider(color = HallaakTheme.colors.stroke, thickness = 0.5.dp)

            SettingsRow(
                icon = Icons.Outlined.Policy,
                title = stringResource(Res.string.privacy_policy),
                onClick = onPrivacyPolicy,
            )

            HorizontalDivider(color = HallaakTheme.colors.stroke, thickness = 0.5.dp)

            SettingsRow(
                icon = Icons.Outlined.Info,
                title = stringResource(Res.string.terms_conditions),
                onClick = onTerms,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  H. Sign out row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SignOutRow(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val signOutLabel = stringResource(Res.string.sign_out)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HallaakTheme.dimensions.space16)
            .clickable(enabled = !isLoading, onClick = onClick),
        shape = HallaakTheme.shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.status.error.copy(alpha = 0.08f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(HallaakTheme.colors.status.error.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = HallaakTheme.colors.status.error,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = signOutLabel,
                        tint = HallaakTheme.colors.status.error,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Text(
                text = signOutLabel,
                style = HallaakTheme.typography.body.small,
                color = HallaakTheme.colors.status.error,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Sign-out confirmation dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SignOutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = HallaakTheme.shapes.dialog,
        containerColor = HallaakTheme.colors.background.surface,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                contentDescription = null,
                tint = HallaakTheme.colors.status.error,
                modifier = Modifier.size(28.dp),
            )
        },
        title = {
            Text(
                text = stringResource(Res.string.sign_out),
                style = HallaakTheme.typography.title.extraLarge,
                color = HallaakTheme.colors.onSurface,
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.sign_out_message),
                style = HallaakTheme.typography.body.small,
                color = HallaakTheme.colors.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(Res.string.sign_out),
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.status.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.cancel),
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.primary,
                )
            }
        },
    )
}
