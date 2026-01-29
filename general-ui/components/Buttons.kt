package ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.theme.AppColors

/**
 * Primary button with hover effect
 * Used for main actions
 * 
 * @param text Button label
 * @param onClick Click handler
 * @param modifier Optional modifier
 * @param enabled Whether the button is enabled
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isHovered && enabled) AppColors.PrimaryHover else AppColors.AccentPrimary,
            contentColor = AppColors.TextPrimary,
            disabledContainerColor = AppColors.AccentPrimary.copy(alpha = 0.5f),
            disabledContentColor = AppColors.TextPrimary.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Secondary button with outline style
 * Used for secondary actions
 * 
 * @param text Button label
 * @param onClick Click handler
 * @param modifier Optional modifier
 * @param enabled Whether the button is enabled
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isHovered && enabled) AppColors.SecondaryHover else Color.Transparent,
            contentColor = AppColors.TextPrimary,
            disabledContentColor = AppColors.TextSecondary
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (enabled) AppColors.Border else AppColors.Border.copy(alpha = 0.5f)
            ).brush
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Danger button for critical actions
 * Used for delete, unpublish, etc.
 * 
 * @param text Button label
 * @param onClick Click handler
 * @param modifier Optional modifier
 * @param enabled Whether the button is enabled
 */
@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isHovered && enabled) AppColors.DangerHover else AppColors.Error,
            contentColor = AppColors.TextPrimary,
            disabledContainerColor = AppColors.Error.copy(alpha = 0.5f),
            disabledContentColor = AppColors.TextPrimary.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Icon button variant
 * Used for actions with icons only
 * 
 * @param onClick Click handler
 * @param modifier Optional modifier
 * @param enabled Whether the button is enabled
 * @param content Icon content
 */
@Composable
fun AppIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        content()
    }
}
