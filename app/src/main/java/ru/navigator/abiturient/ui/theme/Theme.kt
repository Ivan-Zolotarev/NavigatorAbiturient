package ru.navigator.abiturient.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Burgundy,
    onPrimary = OnBurgundy,
    primaryContainer = BurgundyLight,
    onPrimaryContainer = OnBurgundy,
    secondary = BurgundyDark,
    onSecondary = OnBurgundy,
    background = SurfaceLight,
    onBackground = OnSurfaceDark,
    surface = SurfaceLight,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFFF5E6E9),
    onSurfaceVariant = OnSurfaceDark,
)

@Composable
fun NavigatorAbiturientTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
