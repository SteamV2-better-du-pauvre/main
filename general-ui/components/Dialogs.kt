package ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import ui.theme.AppColors

/**
 * Custom dialog with consistent styling
 * 
 * @param title Dialog title
 * @param onDismiss Callback when dialog is dismissed
 * @param modifier Optional modifier
 * @param icon Optional icon to display at top
 * @param confirmButton Optional confirm button composable
 * @param dismissButton Optional dismiss button composable
 * @param content Dialog content
 */
@Composable
fun AppDialog(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .widthIn(max = 560.dp)
                .padding(16.dp),
            color = AppColors.Surface,
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Icon (optional)
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.AccentHighlight,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    content = content
                )
                
                // Action buttons
                if (confirmButton != null || dismissButton != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                    ) {
                        dismissButton?.invoke()
                        confirmButton?.invoke()
                    }
                }
            }
        }
    }
}

/**
 * Snackbar notification component
 * Auto-dismisses after duration
 * 
 * @param message Message to display
 * @param type Type of snackbar (success, error, warning, info)
 * @param onDismiss Callback when snackbar is dismissed
 * @param actionLabel Optional action button label
 * @param onAction Optional action button callback
 * @param duration Duration in milliseconds before auto-dismiss
 */
@Composable
fun AppSnackbar(
    message: String,
    type: SnackbarType = SnackbarType.Info,
    onDismiss: () -> Unit,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    duration: Long = 3000L
) {
    LaunchedEffect(Unit) {
        delay(duration)
        onDismiss()
    }
    
    val backgroundColor = when (type) {
        SnackbarType.Success -> AppColors.Success
        SnackbarType.Error -> AppColors.Error
        SnackbarType.Warning -> AppColors.Warning
        SnackbarType.Info -> AppColors.Info
    }
    
    val icon = when (type) {
        SnackbarType.Success -> Icons.Default.CheckCircle
        SnackbarType.Error -> Icons.Default.Error
        SnackbarType.Warning -> Icons.Default.Warning
        SnackbarType.Info -> Icons.Default.Info
    }
    
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.TextPrimary
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        color = AppColors.TextPrimary
                    )
                }
            }
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = AppColors.TextPrimary
                )
            }
        }
    }
}

enum class SnackbarType {
    Success, Error, Warning, Info
}

/**
 * Loading indicator (circular progress)
 * 
 * @param modifier Optional modifier
 * @param message Optional loading message
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            color = AppColors.AccentPrimary,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )
        
        if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary
            )
        }
    }
}

/**
 * Full-screen loading overlay
 * 
 * @param message Optional loading message
 */
@Composable
fun LoadingOverlay(
    message: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Overlay),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = AppColors.Surface,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            LoadingIndicator(
                message = message,
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}

/**
 * Empty state component for empty lists/screens
 * 
 * @param icon Icon to display
 * @param title Empty state title
 * @param description Optional description text
 * @param modifier Optional modifier
 * @param actionButton Optional action button
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(80.dp)
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
        
        if (actionButton != null) {
            Spacer(modifier = Modifier.height(8.dp))
            actionButton()
        }
    }
}
