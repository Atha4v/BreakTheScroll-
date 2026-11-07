package com.breakthescroll.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BreakTheScrollCalmColors = darkColorScheme(
    primary = SereneMint,
    onPrimary = DeepSlate,
    primaryContainer = SereneMintContainer,
    onPrimaryContainer = SoftFrost,
    secondary = WarmAmber,
    onSecondary = DeepSlate,
    secondaryContainer = WarmAmberContainer,
    onSecondaryContainer = SoftFrost,
    tertiary = BuddyFox,
    onTertiary = SoftFrost,
    background = DeepSlate,
    onBackground = SoftFrost,
    surface = Obsidian,
    onSurface = SoftFrost,
    surfaceVariant = ObsidianElevated,
    onSurfaceVariant = SoftFrostMuted,
    outline = BorderObsidian,
    outlineVariant = BorderObsidian,
    error = PassCoral,
    onError = DeepSlate,
    errorContainer = PassCoralContainer,
    onErrorContainer = SoftFrost,
)

@Composable
fun BreakTheScrollTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BreakTheScrollCalmColors,
        typography = BreakTheScrollTypography,
        content = content,
    )
}
