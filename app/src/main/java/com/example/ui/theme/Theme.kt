package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = PeonyRose,
  onPrimary = CardSurface,
  primaryContainer = PeonyLight,
  onPrimaryContainer = PeonyDark,
  secondary = PetalBlush,
  onSecondary = TextPrimary,
  secondaryContainer = CardSurfaceVariant,
  onSecondaryContainer = PeonyDark,
  tertiary = CoralWarm,
  onTertiary = CardSurface,
  background = CreamBackground,
  onBackground = TextPrimary,
  surface = CardSurface,
  onSurface = TextPrimary,
  surfaceVariant = CardSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = OutlineBorder,
  outlineVariant = SleekCardBorder
)

private val DarkColorScheme = darkColorScheme(
  primary = PeonyRoseDark,
  onPrimary = PeonyDarkSurface,
  primaryContainer = PeonyDarkSurfaceVariant,
  onPrimaryContainer = PeonyRoseDark,
  secondary = PetalBlush,
  onSecondary = PeonyDarkSurface,
  tertiary = CoralWarm,
  background = PeonyDarkSurface,
  onBackground = DarkTextPrimary,
  surface = PeonyDarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = PeonyDarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkOutlineBorder,
  outlineVariant = DarkOutlineBorder
)

@Composable
fun EvelynCycleTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted brand palette
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
