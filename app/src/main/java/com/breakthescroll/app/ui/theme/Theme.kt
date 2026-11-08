package com.breakthescroll.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private fun lightScheme() = lightColorScheme(
    primary = ResilientCopper,
    onPrimary = Color.White,
    primaryContainer = ResilientCopperContainer,
    onPrimaryContainer = BadgerCharcoal,
    secondary = BadgerGold,
    onSecondary = BadgerCharcoal,
    secondaryContainer = BadgerGoldContainer,
    onSecondaryContainer = BadgerCharcoal,
    background = LightBackground,
    onBackground = BadgerCharcoal,
    surface = LightSurface,
    onSurface = BadgerCharcoal,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = BadgerCharcoalMuted,
    outline = LightBorder,
    outlineVariant = LightBorder,
    error = PassCoral,
    onError = Color.White,
    errorContainer = PassCoralContainerLight,
    onErrorContainer = BadgerCharcoal,
)

private fun darkScheme() = darkColorScheme(
    primary = SereneMint,
    onPrimary = DeepSlate,
    primaryContainer = SereneMintContainer,
    onPrimaryContainer = SoftFrost,
    secondary = WarmAmber,
    onSecondary = DeepSlate,
    secondaryContainer = WarmAmberContainer,
    onSecondaryContainer = SoftFrost,
    background = DeepSlate,
    onBackground = SoftFrost,
    surface = Obsidian,
    onSurface = SoftFrost,
    surfaceVariant = ObsidianElevated,
    onSurfaceVariant = SoftFrostMuted,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = PassCoral,
    onError = DeepSlate,
    errorContainer = PassCoralContainerDark,
    onErrorContainer = SoftFrost,
)

@Composable
fun BreakTheScrollTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    CompositionLocalProvider(LocalBtsPalette provides palette) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkScheme() else lightScheme(),
            typography = BreakTheScrollTypography,
            content = content,
        )
    }
}
