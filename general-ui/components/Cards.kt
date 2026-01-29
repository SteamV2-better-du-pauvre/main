package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ui.theme.AppColors

/**
 * Basic card container with consistent styling
 * 
 * @param modifier Optional modifier
 * @param onClick Optional click handler to make card clickable
 * @param content Card content
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        },
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface,
            contentColor = AppColors.TextPrimary
        ),
        border = BorderStroke(1.dp, AppColors.Border),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp,
            hoveredElevation = 8.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Game card for displaying game information in catalog/library
 * 
 * @param title Game title
 * @param price Game price (null if owned or free)
 * @param tags List of game tags
 * @param imageContent Optional composable for game image
 * @param modifier Optional modifier
 * @param onClick Click handler
 * @param badge Optional badge text (e.g., "New", "Owned")
 */
@Composable
fun GameCard(
    title: String,
    price: String? = null,
    tags: List<String> = emptyList(),
    imageContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    badge: String? = null
) {
    Card(
        modifier = modifier
            .width(240.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface,
            contentColor = AppColors.TextPrimary
        ),
        border = BorderStroke(1.dp, AppColors.Border),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            hoveredElevation = 8.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(AppColors.BackgroundSecondary),
                contentAlignment = Alignment.Center
            ) {
                if (imageContent != null) {
                    imageContent()
                } else {
                    // Placeholder gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        AppColors.AccentPrimary,
                                        AppColors.BackgroundSecondary
                                    )
                                )
                            )
                    )
                }
                
                // Badge overlay
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Badge(
                            text = badge,
                            color = AppColors.AccentHighlight
                        )
                    }
                }
            }
            
            // Content section
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tags
                if (tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tags.take(2).forEach { tag ->
                            Tag(text = tag)
                        }
                        if (tags.size > 2) {
                            Text(
                                text = "+${tags.size - 2}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price or action
                if (price != null) {
                    Text(
                        text = price,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.AccentHighlight
                    )
                }
            }
        }
    }
}

/**
 * Compact card for lists
 * 
 * @param title Card title
 * @param subtitle Optional subtitle
 * @param modifier Optional modifier
 * @param onClick Optional click handler
 * @param leadingContent Optional leading content (icon, image)
 * @param trailingContent Optional trailing content (actions)
 */
@Composable
fun CompactCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        },
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface,
            contentColor = AppColors.TextPrimary
        ),
        border = BorderStroke(1.dp, AppColors.Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Leading content
            if (leadingContent != null) {
                leadingContent()
            }
            
            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Trailing content
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}
