package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Dark color scheme for the platform
 * All applications use dark mode by default
 */
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.AccentPrimary,
    onPrimary = AppColors.TextPrimary,
    primaryContainer = AppColors.AccentHighlight,
    onPrimaryContainer = AppColors.TextPrimary,
    
    secondary = AppColors.AccentHighlight,
    onSecondary = AppColors.TextPrimary,
    secondaryContainer = AppColors.BackgroundSecondary,
    onSecondaryContainer = AppColors.TextPrimary,
    
    tertiary = AppColors.Info,
    onTertiary = AppColors.TextPrimary,
    
    error = AppColors.Error,
    onError = AppColors.TextPrimary,
    
    background = AppColors.BackgroundPrimary,
    onBackground = AppColors.TextPrimary,
    
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.TextSecondary,
    
    outline = AppColors.Border,
    outlineVariant = AppColors.BorderLight,
    
    scrim = AppColors.Overlay
)

/**
 * Main theme composable for all applications
 * 
 * @param content The content to be themed
 */
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
