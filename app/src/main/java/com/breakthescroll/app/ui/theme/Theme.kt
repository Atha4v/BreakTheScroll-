package com.breakthescroll.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private fun lightScheme() = lightColorScheme(
    primary = Ember,
    onPrimary = Color.White,
    primaryContainer = EmberContainer,
    onPrimaryContainer = Ink,
    secondary = Brass,
    onSecondary = Color.White,
    secondaryContainer = BrassContainer,
    onSecondaryContainer = Ink,
    background = Porcelain,
    onBackground = Ink,
    surface = PorcelainSurface,
    onSurface = Ink,
    surfaceVariant = PorcelainElevated,
    onSurfaceVariant = InkMuted,
    outline = PorcelainBorder,
    outlineVariant = PorcelainBorder,
    error = PassRose,
    onError = Color.White,
    errorContainer = PassRoseContainerLight,
    onErrorContainer = Ink,
)

private fun darkScheme() = darkColorScheme(
    primary = Apricot,
    onPrimary = Graphite,
    primaryContainer = ApricotContainer,
    onPrimaryContainer = Bone,
    secondary = Champagne,
    onSecondary = Graphite,
    secondaryContainer = ChampagneContainer,
    onSecondaryContainer = Bone,
    background = Graphite,
    onBackground = Bone,
    surface = GraphiteSurface,
    onSurface = Bone,
    surfaceVariant = GraphiteElevated,
    onSurfaceVariant = BoneMuted,
    outline = GraphiteBorder,
    outlineVariant = GraphiteBorder,
    error = PassRose,
    onError = Graphite,
    errorContainer = PassRoseContainerDark,
    onErrorContainer = Bone,
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
