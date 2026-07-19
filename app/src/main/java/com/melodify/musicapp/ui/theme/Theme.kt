package com.melodify.musicapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

@Composable
fun MelodifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = MelodifyGreen,
            secondary = MelodifyGreen,
            background = DarkBackground,
            surface = DarkSurface,
            onPrimary = Color.White,
            onBackground = DarkOnSurface,
            onSurface = DarkOnSurface,
            surfaceVariant = MelodifyLightGray
        )
    } else {
        lightColorScheme(
            primary = MelodifyGreen,
            secondary = MelodifyGreen,
            background = LightBackground,
            surface = LightSurface,
            onPrimary = Color.White,
            onBackground = LightOnSurface,
            onSurface = LightOnSurface,
            surfaceVariant = MelodifyLightGray
        )
    }

    val window = (LocalContext.current as? androidx.activity.ComponentActivity)?.window
    window?.let {
        val controller = WindowCompat.getInsetsController(it, it.decorView)
        controller.isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MelodifyTypography,
        shapes = MelodifyShapes,
        content = content
    )
}

@Composable fun MelodifyColors() = MaterialTheme.colorScheme
@Composable fun MelodifyTypography() = MaterialTheme.typography
@Composable fun MelodifyShapes() = MaterialTheme.shapes