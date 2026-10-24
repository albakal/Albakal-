package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = GreenPrimaryDark,
  onPrimary = Color(0xFF00390B),
  primaryContainer = Color(0xFF005315),
  onPrimaryContainer = Color(0xFFA1F59E),
  secondary = AmberSecondaryDark,
  onSecondary = Color(0xFF4B2800),
  secondaryContainer = Color(0xFF6B3B00),
  onSecondaryContainer = Color(0xFFFFDCC1),
  tertiary = TealTertiary,
  background = BackgroundDark,
  surface = SurfaceDark,
  onBackground = Color(0xFFE2E3DE),
  onSurface = Color(0xFFE2E3DE),
  surfaceVariant = Color(0xFF424940),
  onSurfaceVariant = Color(0xFFC2C9BD)
)

private val LightColorScheme = lightColorScheme(
  primary = GreenPrimary,
  onPrimary = Color.White,
  primaryContainer = GreenContainer,
  onPrimaryContainer = OnGreenContainer,
  secondary = AmberSecondary,
  onSecondary = Color.White,
  secondaryContainer = AmberContainer,
  onSecondaryContainer = OnAmberContainer,
  tertiary = TealTertiary,
  tertiaryContainer = TealContainer,
  background = BackgroundLight,
  surface = SurfaceLight,
  onBackground = Color(0xFF191C19),
  onSurface = Color(0xFF191C19),
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = Color(0xFF424940),
  outline = OutlineLight
)

@Composable
fun AlbakalTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep branded fresh green theme consistent
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
