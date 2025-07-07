package com.weather.android.ui.atoms.navigation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Atomic navigation drawer components for side navigation
 * 
 * Features:
 * - Modal and dismissible drawer variants
 * - Consistent styling and spacing
 * - Flexible content composition
 * - Material Design 3 navigation patterns
 */

/**
 * Modal navigation drawer
 */
@Composable
fun AtomicModalNavigationDrawer(
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = AtomicDesignSystem.colors.Surface,
                drawerContentColor = AtomicDesignSystem.colors.OnSurface,
                modifier = Modifier.fillMaxHeight()
            ) {
                drawerContent()
            }
        },
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        content = content
    )
}

/**
 * Dismissible navigation drawer
 */
@Composable
fun AtomicDismissibleNavigationDrawer(
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    DismissibleNavigationDrawer(
        drawerContent = {
            DismissibleDrawerSheet(
                drawerContainerColor = AtomicDesignSystem.colors.Surface,
                drawerContentColor = AtomicDesignSystem.colors.OnSurface,
                modifier = Modifier.fillMaxHeight()
            ) {
                drawerContent()
            }
        },
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        content = content
    )
}

/**
 * Atomic navigation drawer item
 */
@Composable
fun AtomicNavigationDrawerItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    badge: @Composable (() -> Unit)? = null
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                style = AtomicDesignSystem.typography.LabelLarge
            )
        },
        selected = selected,
        onClick = onClick,
        modifier = modifier.padding(horizontal = AtomicDesignSystem.spacing.XS),
        icon = icon,
        badge = badge,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = AtomicDesignSystem.colors.SecondaryContainer,
            unselectedContainerColor = AtomicDesignSystem.colors.Surface,
            selectedIconColor = AtomicDesignSystem.colors.OnSecondaryContainer,
            unselectedIconColor = AtomicDesignSystem.colors.OnSurfaceVariant,
            selectedTextColor = AtomicDesignSystem.colors.OnSecondaryContainer,
            unselectedTextColor = AtomicDesignSystem.colors.OnSurfaceVariant
        ),
        shape = AtomicDesignSystem.shapes.NavigationDrawerItem
    )
}

/**
 * Data class for navigation drawer items
 */
data class DrawerNavItem(
    val label: String,
    val icon: ImageVector? = null,
    val route: String,
    val badgeCount: Int? = null
)

/**
 * Convenience function for creating drawer content with items
 */
@Composable
fun ColumnScope.AtomicDrawerContent(
    items: List<DrawerNavItem>,
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    header: @Composable (() -> Unit)? = null
) {
    header?.let {
        it()
        Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.MD))
    }
    
    items.forEach { item ->
        AtomicNavigationDrawerItem(
            label = item.label,
            selected = selectedRoute == item.route,
            onClick = { onItemClick(item.route) },
            icon = item.icon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = item.label
                    )
                }
            },
            badge = item.badgeCount?.let { count ->
                {
                    Text(
                        text = count.toString(),
                        style = AtomicDesignSystem.typography.LabelSmall
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AtomicNavigationDrawerPreview() {
    AtomicTheme {
        AtomicModalNavigationDrawer(
            drawerContent = {
                AtomicDrawerContent(
                    items = listOf(
                        DrawerNavItem("Home", Icons.Default.Home, "home"),
                        DrawerNavItem("Settings", Icons.Default.Settings, "settings", badgeCount = 2)
                    ),
                    selectedRoute = "home",
                    onItemClick = { /* Navigation action */ },
                    header = {
                        Text(
                            text = "Weather App",
                            style = AtomicDesignSystem.typography.HeadlineSmall,
                            modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
                        )
                    }
                )
            }
        ) {
            Text("Main content goes here")
        }
    }
}