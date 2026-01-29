package ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palette for the Steam V2 Better du Pauvre platform
 * Based on modern gaming platform aesthetics
 */
object AppColors {
    // Background colors
    val BackgroundPrimary = Color(0xFF1A1A2E)      // Deep blue background
    val BackgroundSecondary = Color(0xFF16213E)    // Night blue background
    val AccentPrimary = Color(0xFF0F3460)          // Deep blue accent
    val AccentHighlight = Color(0xFFE94560)        // Pink/red highlight
    
    // Text colors
    val TextPrimary = Color(0xFFFFFFFF)            // White
    val TextSecondary = Color(0xFFA0A0A0)          // Gray
    
    // Status colors
    val Success = Color(0xFF4ADE80)                // Green
    val Error = Color(0xFFEF4444)                  // Red
    val Warning = Color(0xFFF59E0B)                // Orange
    val Info = Color(0xFF3B82F6)                   // Blue
    
    // Additional UI colors
    val Surface = Color(0xFF1F2937)                // Card surface
    val SurfaceVariant = Color(0xFF374151)         // Elevated surface
    val Border = Color(0xFF4B5563)                 // Border color
    val BorderLight = Color(0xFF6B7280)            // Light border
    
    // Overlay colors
    val Overlay = Color(0x99000000)                // Semi-transparent black
    val OverlayLight = Color(0x66000000)           // Light overlay
    
    // Button hover states
    val PrimaryHover = Color(0xFF1A4E7C)           // Hover state for primary
    val SecondaryHover = Color(0xFF475569)         // Hover state for secondary
    val DangerHover = Color(0xFFDC2626)            // Hover state for danger
}
