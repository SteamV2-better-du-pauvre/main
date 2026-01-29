package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ui.theme.AppColors

/**
 * Star rating component for displaying and selecting ratings
 * 
 * @param rating Current rating (0.0 to 5.0)
 * @param modifier Optional modifier
 * @param maxRating Maximum rating value (default 5)
 * @param editable Whether the rating can be changed by clicking
 * @param onRatingChanged Callback when rating changes (for editable mode)
 * @param size Size of stars in dp
 */
@Composable
fun StarRating(
    rating: Float,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    editable: Boolean = false,
    onRatingChanged: ((Int) -> Unit)? = null,
    size: Int = 24
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val starModifier = if (editable && onRatingChanged != null) {
                Modifier
                    .size(size.dp)
                    .clickable { onRatingChanged(i) }
            } else {
                Modifier.size(size.dp)
            }
            
            when {
                i <= rating.toInt() -> {
                    // Full star
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star $i",
                        tint = AppColors.Warning,
                        modifier = starModifier
                    )
                }
                i == rating.toInt() + 1 && rating % 1 >= 0.5f -> {
                    // Half star
                    Icon(
                        imageVector = Icons.Default.StarHalf,
                        contentDescription = "Half star",
                        tint = AppColors.Warning,
                        modifier = starModifier
                    )
                }
                else -> {
                    // Empty star
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Empty star $i",
                        tint = if (editable) AppColors.Border else AppColors.TextSecondary,
                        modifier = starModifier
                    )
                }
            }
        }
    }
}

/**
 * Star rating with text display
 * 
 * @param rating Current rating value
 * @param totalRatings Optional total number of ratings
 * @param modifier Optional modifier
 */
@Composable
fun StarRatingWithText(
    rating: Float,
    totalRatings: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StarRating(rating = rating)
        
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextPrimary
        )
        
        if (totalRatings != null) {
            Text(
                text = "($totalRatings)",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
    }
}

/**
 * Badge component for status indicators and counters
 * 
 * @param text Badge text
 * @param modifier Optional modifier
 * @param color Badge background color
 */
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AppColors.AccentHighlight
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = MaterialTheme.shapes.extraSmall,
        tonalElevation = 2.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Status badge with predefined styles
 * 
 * @param status Status type
 * @param modifier Optional modifier
 */
@Composable
fun StatusBadge(
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        StatusType.Draft -> "Brouillon" to AppColors.TextSecondary
        StatusType.Pending -> "En attente" to AppColors.Warning
        StatusType.Published -> "Publié" to AppColors.Success
        StatusType.Rejected -> "Refusé" to AppColors.Error
        StatusType.New -> "Nouveau" to AppColors.Info
    }
    
    Badge(
        text = text,
        color = color,
        modifier = modifier
    )
}

enum class StatusType {
    Draft, Pending, Published, Rejected, New
}

/**
 * Tag chip component for displaying game tags, categories, etc.
 * 
 * @param text Tag text
 * @param modifier Optional modifier
 * @param onClick Optional click handler
 * @param selected Whether the tag is selected (for filtering)
 */
@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false
) {
    Surface(
        modifier = if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        },
        color = if (selected) AppColors.AccentPrimary else AppColors.SurfaceVariant,
        shape = MaterialTheme.shapes.extraSmall,
        border = if (selected) {
            BorderStroke(1.dp, AppColors.AccentHighlight)
        } else null
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) AppColors.TextPrimary else AppColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Chip group for multiple tags
 * 
 * @param tags List of tags
 * @param modifier Optional modifier
 * @param selectedTags Set of selected tag indices (for filtering)
 * @param onTagClick Optional callback when tag is clicked
 */
@Composable
fun TagGroup(
    tags: List<String>,
    modifier: Modifier = Modifier,
    selectedTags: Set<Int> = emptySet(),
    onTagClick: ((Int) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        tags.forEachIndexed { index, tag ->
            Tag(
                text = tag,
                selected = index in selectedTags,
                onClick = if (onTagClick != null) {
                    { onTagClick(index) }
                } else null
            )
        }
    }
}

/**
 * Numeric badge for notifications/counters
 * Typically used in navigation items
 * 
 * @param count Number to display
 * @param modifier Optional modifier
 * @param maxCount Maximum count to display (shows "maxCount+" if exceeded)
 */
@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    maxCount: Int = 99
) {
    if (count > 0) {
        Surface(
            modifier = modifier.size(20.dp),
            color = AppColors.AccentHighlight,
            shape = androidx.compose.foundation.shape.CircleShape
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = if (count > maxCount) "$maxCount+" else count.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp
                    ),
                    color = AppColors.TextPrimary
                )
            }
        }
    }
}

/**
 * Severity badge for bug reports
 * 
 * @param severity Severity level
 * @param modifier Optional modifier
 */
@Composable
fun SeverityBadge(
    severity: BugSeverity,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (severity) {
        BugSeverity.Minor -> "Mineur" to AppColors.Info
        BugSeverity.Major -> "Majeur" to AppColors.Warning
        BugSeverity.Critical -> "Critique" to AppColors.Error
    }
    
    Badge(
        text = text,
        color = color,
        modifier = modifier
    )
}

enum class BugSeverity {
    Minor, Major, Critical
}
