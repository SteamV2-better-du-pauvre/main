package ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for UI components
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Small elements, chips
    small = RoundedCornerShape(8.dp),        // Buttons, text fields
    medium = RoundedCornerShape(12.dp),      // Cards, dialogs
    large = RoundedCornerShape(16.dp),       // Large cards, bottom sheets
    extraLarge = RoundedCornerShape(24.dp)   // Special containers
)
