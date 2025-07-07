package com.weather.android.ui.atoms.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Atomic bottom navigation bar component
 * 
 * Features:
 * - Consistent navigation styling
 * - Flexible item configuration
 * - Material Design 3 styling
 * - Badge support preparation
 */
@Composable
fun AtomicBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = AtomicDesignSystem.colors.Surface,
        contentColor = AtomicDesignSystem.colors.OnSurface,
        content = content
    )
}

/**
 * Atomic bottom navigation item
 */
@Composable
fun RowScope.AtomicBottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AtomicDesignSystem.colors.OnSecondaryContainer,
            unselectedIconColor = AtomicDesignSystem.colors.OnSurfaceVariant,
            selectedTextColor = AtomicDesignSystem.colors.OnSurface,
            unselectedTextColor = AtomicDesignSystem.colors.OnSurfaceVariant,
            indicatorColor = AtomicDesignSystem.colors.SecondaryContainer
        )
    )
}

/**
 * Data class for bottom navigation items
 */
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Convenience function for creating a bottom navigation bar with items
 */
@Composable
fun AtomicBottomBarWithItems(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AtomicBottomBar(modifier = modifier) {
        items.forEach { item ->
            AtomicBottomBarItem(
                selected = selectedRoute == item.route,
                onClick = { onItemClick(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = AtomicDesignSystem.typography.LabelMedium
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AtomicBottomBarPreview() {
    AtomicTheme {
        AtomicBottomBarWithItems(
            items = listOf(
                BottomNavItem("Home", Icons.Default.Home, "home"),
                BottomNavItem("Settings", Icons.Default.Settings, "settings")
            ),
            selectedRoute = "home",
            onItemClick = { /* Navigation action */ }
        )
    }
}