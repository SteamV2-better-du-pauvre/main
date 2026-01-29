package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ui.theme.AppColors

/**
 * Navigation rail item data class
 * 
 * @param icon Icon for the nav item
 * @param label Label text
 * @param badge Optional badge count
 */
data class NavItem(
    val icon: ImageVector,
    val label: String,
    val badge: Int? = null
)

/**
 * Vertical navigation rail for desktop applications
 * Collapsible from 250px to 72px
 * 
 * @param items List of navigation items
 * @param selectedIndex Currently selected item index
 * @param onItemSelected Callback when item is selected
 * @param modifier Optional modifier
 * @param expanded Whether the rail is expanded (shows labels)
 * @param onToggleExpanded Callback to toggle expansion
 */
@Composable
fun AppNavigationRail(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    onToggleExpanded: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(if (expanded) 250.dp else 72.dp),
        color = AppColors.BackgroundSecondary,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with toggle button
            if (onToggleExpanded != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable(onClick = onToggleExpanded)
                        .padding(16.dp),
                    contentAlignment = if (expanded) Alignment.CenterStart else Alignment.Center
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.MenuOpen else Icons.Default.Menu,
                        contentDescription = if (expanded) "Collapse menu" else "Expand menu",
                        tint = AppColors.TextPrimary
                    )
                    if (expanded) {
                        Text(
                            text = "Menu",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.TextPrimary,
                            modifier = Modifier.padding(start = 40.dp)
                        )
                    }
                }
                HorizontalDivider(color = AppColors.Border)
            }
            
            // Navigation items
            items.forEachIndexed { index, item ->
                NavigationRailItem(
                    selected = selectedIndex == index,
                    onClick = { onItemSelected(index) },
                    icon = item.icon,
                    label = item.label,
                    badge = item.badge,
                    expanded = expanded
                )
            }
        }
    }
}

/**
 * Individual navigation rail item
 */
@Composable
private fun NavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    badge: Int?,
    expanded: Boolean
) {
    val backgroundColor = if (selected) AppColors.AccentPrimary else androidx.compose.ui.graphics.Color.Transparent
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = if (expanded) Alignment.CenterStart else Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon with optional badge
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) AppColors.TextPrimary else AppColors.TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
                if (badge != null && badge > 0) {
                    Badge(
                        text = badge.toString(),
                        color = AppColors.AccentHighlight,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
            
            // Label (only when expanded)
            if (expanded) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selected) AppColors.TextPrimary else AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Top app bar with title and actions
 * 
 * @param title Bar title
 * @param modifier Optional modifier
 * @param navigationIcon Optional navigation icon (back button, menu, etc.)
 * @param actions Optional row of action buttons
 */
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppColors.BackgroundSecondary,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (navigationIcon != null) {
                    navigationIcon()
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppColors.TextPrimary
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions()
            }
        }
    }
}

/**
 * Simple horizontal divider
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = AppColors.Border
) {
    Divider(
        modifier = modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = color
    )
}
