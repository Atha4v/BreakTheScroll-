package com.breakthescroll.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light — "Porcelain": warm paper, ink type, ember accent
val Porcelain = Color(0xFFF7F4EF)
val PorcelainSurface = Color(0xFFFFFFFF)
val PorcelainElevated = Color(0xFFEFEAE3)
val Ink = Color(0xFF1B1A1F)
val InkMuted = Color(0xFF6E6A72)
val PorcelainBorder = Color(0xFFE5DFD6)
val Ember = Color(0xFFD2562E)
val EmberContainer = Color(0xFFF8E3D9)
val Brass = Color(0xFFA9824B)
val BrassContainer = Color(0xFFF2E8D8)

// Dark — "Graphite": warm near-black, soft bone type, apricot accent
val Graphite = Color(0xFF121114)
val GraphiteSurface = Color(0xFF1C1A1F)
val GraphiteElevated = Color(0xFF26232A)
val Bone = Color(0xFFF3EFE8)
val BoneMuted = Color(0xFF9E99A3)
val GraphiteBorder = Color(0xFF302D35)
val Apricot = Color(0xFFF0875A)
val ApricotContainer = Color(0xFF3D2219)
val Champagne = Color(0xFFE2C28E)
val ChampagneContainer = Color(0xFF3A2F1E)

val PassRose = Color(0xFFE5675F)
val PassRoseContainerLight = Color(0xFFFBE4E2)
val PassRoseContainerDark = Color(0xFF3E1D1B)
val BuddyBadgerFur = Color(0xFF3F2A1D)
val BuddyBadgerCream = Color(0xFFF5E6D3)
val BuddyBadgerStripe = Color(0xFFF8FAFC)

@Immutable
data class BtsPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val primary: Color,
    val primaryContainer: Color,
    val highlight: Color,
    val highlightContainer: Color,
    val onBackground: Color,
    val onSurfaceMuted: Color,
    val border: Color,
    val error: Color,
    val errorContainer: Color,
    val onPrimary: Color,
    val isDark: Boolean,
)

val LightPalette = BtsPalette(
    background = Porcelain,
    surface = PorcelainSurface,
    surfaceElevated = PorcelainElevated,
    primary = Ember,
    primaryContainer = EmberContainer,
    highlight = Brass,
    highlightContainer = BrassContainer,
    onBackground = Ink,
    onSurfaceMuted = InkMuted,
    border = PorcelainBorder,
    error = PassRose,
    errorContainer = PassRoseContainerLight,
    onPrimary = Color.White,
    isDark = false,
)

val DarkPalette = BtsPalette(
    background = Graphite,
    surface = GraphiteSurface,
    surfaceElevated = GraphiteElevated,
    primary = Apricot,
    primaryContainer = ApricotContainer,
    highlight = Champagne,
    highlightContainer = ChampagneContainer,
    onBackground = Bone,
    onSurfaceMuted = BoneMuted,
    border = GraphiteBorder,
    error = PassRose,
    errorContainer = PassRoseContainerDark,
    onPrimary = Graphite,
    isDark = true,
)

val LocalBtsPalette = staticCompositionLocalOf { DarkPalette }

// Compatibility aliases for older call sites
val SoftFrost = Bone
val SurfaceNeutral get() = DarkPalette.background
val ContainerWhite get() = DarkPalette.surface
val PrimaryEmerald get() = DarkPalette.primary
val PrimaryEmeraldDark get() = Apricot
val PrimaryEmeraldContainer get() = DarkPalette.primaryContainer
val SecondaryAmber get() = DarkPalette.highlight
val SecondaryAmberContainer get() = DarkPalette.highlightContainer
val BorderSubtle get() = DarkPalette.border
val OnSurfacePrimary get() = DarkPalette.onBackground
val OnSurfaceSecondary get() = DarkPalette.onSurfaceMuted
val AcceptTint get() = DarkPalette.primary
val BorderObsidian get() = DarkPalette.border
val ObsidianElevatedAlias get() = DarkPalette.surfaceElevated
