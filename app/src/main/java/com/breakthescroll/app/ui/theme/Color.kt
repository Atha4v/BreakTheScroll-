package com.breakthescroll.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light — wellness copper/gold
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFF1F5F9)
val BadgerCharcoal = Color(0xFF1E293B)
val BadgerCharcoalMuted = Color(0xFF64748B)
val ResilientCopper = Color(0xFFC68642)
val ResilientCopperContainer = Color(0xFFF3E0C8)
val BadgerGold = Color(0xFFD9A06F)
val BadgerGoldContainer = Color(0xFFF8ECDF)
val LightBorder = Color(0xFFE2E8F0)

// Dark — calm slate/mint
val DeepSlate = Color(0xFF0F172A)
val Obsidian = Color(0xFF1E293B)
val ObsidianElevated = Color(0xFF273549)
val SoftFrost = Color(0xFFF8FAFC)
val SoftFrostMuted = Color(0xFF94A3B8)
val SereneMint = Color(0xFF10B981)
val SereneMintContainer = Color(0xFF064E3B)
val WarmAmber = Color(0xFFF59E0B)
val WarmAmberContainer = Color(0xFF78350F)
val DarkBorder = Color(0xFF334155)

val PassCoral = Color(0xFFF87171)
val PassCoralContainerLight = Color(0xFFFEE2E2)
val PassCoralContainerDark = Color(0xFF7F1D1D)
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
    background = LightBackground,
    surface = LightSurface,
    surfaceElevated = LightSurfaceElevated,
    primary = ResilientCopper,
    primaryContainer = ResilientCopperContainer,
    highlight = BadgerGold,
    highlightContainer = BadgerGoldContainer,
    onBackground = BadgerCharcoal,
    onSurfaceMuted = BadgerCharcoalMuted,
    border = LightBorder,
    error = PassCoral,
    errorContainer = PassCoralContainerLight,
    onPrimary = Color.White,
    isDark = false,
)

val DarkPalette = BtsPalette(
    background = DeepSlate,
    surface = Obsidian,
    surfaceElevated = ObsidianElevated,
    primary = SereneMint,
    primaryContainer = SereneMintContainer,
    highlight = WarmAmber,
    highlightContainer = WarmAmberContainer,
    onBackground = SoftFrost,
    onSurfaceMuted = SoftFrostMuted,
    border = DarkBorder,
    error = PassCoral,
    errorContainer = PassCoralContainerDark,
    onPrimary = DeepSlate,
    isDark = true,
)

val LocalBtsPalette = staticCompositionLocalOf { DarkPalette }

// Compatibility aliases for older call sites during migration
val SurfaceNeutral get() = DarkPalette.background
val ContainerWhite get() = DarkPalette.surface
val PrimaryEmerald get() = DarkPalette.primary
val PrimaryEmeraldDark get() = SereneMint
val PrimaryEmeraldContainer get() = DarkPalette.primaryContainer
val SecondaryAmber get() = DarkPalette.highlight
val SecondaryAmberContainer get() = DarkPalette.highlightContainer
val BorderSubtle get() = DarkPalette.border
val OnSurfacePrimary get() = DarkPalette.onBackground
val OnSurfaceSecondary get() = DarkPalette.onSurfaceMuted
val AcceptTint get() = DarkPalette.primary
val BorderObsidian get() = DarkPalette.border
val ObsidianElevatedAlias get() = DarkPalette.surfaceElevated
