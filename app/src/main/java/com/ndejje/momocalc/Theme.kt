package com.ndejje.momocalc

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme


private val LightColorScheme = lightColorScheme(
    primary         = NavyBlue,
    onPrimary       = White,
    secondary       = BrandGold,
    onSecondary     = NavyBlueDark,
    background      = LightGrey,
    onBackground    = DarkSurface,
    surface         = White,
    onSurface       = DarkSurface,
    error           = ErrorRed,
    onError         = OnErrorWhite
)

@Composable
fun MoMoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // auto-detect by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = MoMoTypography,
        shapes      = MoMoShapes,
        content     = content
    )
}


private val DarkColorScheme = darkColorScheme(
    primary         = BrandGold,        // gold becomes the hero in dark mode
    onPrimary       = NavyBlueDark,
    secondary       = NavyBlue,
    onSecondary     = White,
    background      = DarkBackground,
    onBackground    = OnDarkText,
    surface         = DarkSurface,
    onSurface       = OnDarkText,
    error           = ErrorRed,
    onError         = OnErrorWhite
)