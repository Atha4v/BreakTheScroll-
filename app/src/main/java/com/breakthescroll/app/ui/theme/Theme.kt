package com.breakthescroll.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BreakTheScrollLightColors = lightColorScheme(
    primary = PrimaryEmerald,
    onPrimary = Color.White,
    primaryContainer = PrimaryEmeraldContainer,
    onPrimaryContainer = PrimaryEmeraldDark,
    secondary = SecondaryAmber,
    onSecondary = Color.White,
    secondaryContainer = SecondaryAmberContainer,
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = PrimaryEmeraldDark,
    background = SurfaceNeutral,
    onBackground = OnSurfacePrimary,
    surface = ContainerWhite,
    onSurface = OnSurfacePrimary,
    surfaceVariant = SurfaceNeutral,
    onSurfaceVariant = OnSurfaceSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderSubtle,
    error = PassCoral,
    onError = Color.White,
    errorContainer = PassCoralContainer,
    onErrorContainer = Color(0xFF7F1D1D),
)

@Composable
fun BreakTheScrollTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BreakTheScrollLightColors,
        typography = BreakTheScrollTypography,
        content = content,
    )
}
