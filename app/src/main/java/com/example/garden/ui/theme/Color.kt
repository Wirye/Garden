package com.example.garden.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.SchemeTonalSpot

@SuppressLint("RestrictedApi")
fun createDynamicLightColorScheme(seed: Color): ColorScheme {
    val hct = Hct.fromInt(seed.toArgb())
    val scheme = SchemeTonalSpot(hct, false, 0.0)

    return lightColorScheme(
        primary = Color(scheme.primary),
        onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer),
        onPrimaryContainer = Color(scheme.onPrimaryContainer),
        secondary = Color(scheme.secondary),
        onSecondary = Color(scheme.onSecondary),
        secondaryContainer = Color(scheme.secondaryContainer),
        onSecondaryContainer = Color(scheme.onSecondaryContainer),
        tertiary = Color(scheme.tertiary),
        onTertiary = Color(scheme.onTertiary),
        tertiaryContainer = Color(scheme.tertiaryContainer),
        onTertiaryContainer = Color(scheme.onTertiaryContainer),
        background = Color(scheme.background),
        onBackground = Color(scheme.onBackground),
        surface = Color(scheme.surface),
        onSurface = Color(scheme.onSurface),
        surfaceVariant = Color(scheme.surfaceVariant),
        onSurfaceVariant = Color(scheme.onSurfaceVariant),
        outline = Color(scheme.outline),
        outlineVariant = Color(scheme.outlineVariant),
        surfaceTint = Color(scheme.surfaceTint),
        scrim = Color(scheme.scrim),
        inverseSurface = Color(scheme.inverseSurface),
        inverseOnSurface = Color(scheme.inverseOnSurface),
        inversePrimary = Color(scheme.inversePrimary),
        surfaceBright = Color(scheme.surfaceBright),
        surfaceDim = Color(scheme.surfaceDim),
        surfaceContainerHighest = Color(scheme.surfaceContainerHighest),
        surfaceContainerHigh = Color(scheme.surfaceContainerHigh),
        surfaceContainer = Color(scheme.surfaceContainer),
        surfaceContainerLow = Color(scheme.surfaceContainerLow),
        surfaceContainerLowest = Color(scheme.surfaceContainerLowest),
        tertiaryFixed = Color(scheme.tertiaryFixed),
        tertiaryFixedDim = Color(scheme.tertiaryFixedDim),
        onTertiaryFixed = Color(scheme.onTertiaryFixed),
        onTertiaryFixedVariant = Color(scheme.onTertiaryFixedVariant),
        errorContainer = Color(scheme.errorContainer),
        onErrorContainer = Color(scheme.onErrorContainer),
        onPrimaryFixedVariant = Color(scheme.onPrimaryFixedVariant),
        onSecondaryFixedVariant = Color(scheme.onSecondaryFixedVariant),
        primaryFixed = Color(scheme.primaryFixed),
        onPrimaryFixed = Color(scheme.onPrimaryFixed),
        secondaryFixed = Color(scheme.secondaryFixed),
        primaryFixedDim = Color(scheme.primaryFixedDim),
        onSecondaryFixed = Color(scheme.onSecondaryFixed),
        secondaryFixedDim = Color(scheme.secondaryFixedDim),
        error = Color(scheme.error),
        onError = Color(scheme.onError)
    )
}

@SuppressLint("RestrictedApi")
fun createDynamicDarkColorScheme(seed: Color): ColorScheme {
    val hct = Hct.fromInt(seed.toArgb())
    val scheme = SchemeTonalSpot(hct, true, 0.0)

    return darkColorScheme(
        primary = Color(scheme.primary),
        onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer),
        onPrimaryContainer = Color(scheme.onPrimaryContainer),
        secondary = Color(scheme.secondary),
        onSecondary = Color(scheme.onSecondary),
        secondaryContainer = Color(scheme.secondaryContainer),
        onSecondaryContainer = Color(scheme.onSecondaryContainer),
        tertiary = Color(scheme.tertiary),
        onTertiary = Color(scheme.onTertiary),
        tertiaryContainer = Color(scheme.tertiaryContainer),
        onTertiaryContainer = Color(scheme.onTertiaryContainer),
        background = Color(scheme.background),
        onBackground = Color(scheme.onBackground),
        surface = Color(scheme.surface),
        onSurface = Color(scheme.onSurface),
        surfaceVariant = Color(scheme.surfaceVariant),
        onSurfaceVariant = Color(scheme.onSurfaceVariant),
        outline = Color(scheme.outline),
        outlineVariant = Color(scheme.outlineVariant),
        surfaceTint = Color(scheme.surfaceTint),
        scrim = Color(scheme.scrim),
        inverseSurface = Color(scheme.inverseSurface),
        inverseOnSurface = Color(scheme.inverseOnSurface),
        inversePrimary = Color(scheme.inversePrimary),
        surfaceBright = Color(scheme.surfaceBright),
        surfaceDim = Color(scheme.surfaceDim),
        surfaceContainerHighest = Color(scheme.surfaceContainerHighest),
        surfaceContainerHigh = Color(scheme.surfaceContainerHigh),
        surfaceContainer = Color(scheme.surfaceContainer),
        surfaceContainerLow = Color(scheme.surfaceContainerLow),
        surfaceContainerLowest = Color(scheme.surfaceContainerLowest),
        tertiaryFixed = Color(scheme.tertiaryFixed),
        tertiaryFixedDim = Color(scheme.tertiaryFixedDim),
        onTertiaryFixed = Color(scheme.onTertiaryFixed),
        onTertiaryFixedVariant = Color(scheme.onTertiaryFixedVariant),
        errorContainer = Color(scheme.errorContainer),
        onErrorContainer = Color(scheme.onErrorContainer),
        onPrimaryFixedVariant = Color(scheme.onPrimaryFixedVariant),
        onSecondaryFixedVariant = Color(scheme.onSecondaryFixedVariant),
        primaryFixed = Color(scheme.primaryFixed),
        onPrimaryFixed = Color(scheme.onPrimaryFixed),
        secondaryFixed = Color(scheme.secondaryFixed),
        primaryFixedDim = Color(scheme.primaryFixedDim),
        onSecondaryFixed = Color(scheme.onSecondaryFixed),
        secondaryFixedDim = Color(scheme.secondaryFixedDim),
        error = Color(scheme.error),
        onError = Color(scheme.onError)
    )
}