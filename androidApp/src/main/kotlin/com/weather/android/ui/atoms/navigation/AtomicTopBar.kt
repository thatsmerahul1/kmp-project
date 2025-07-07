package com.weather.android.ui.atoms.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Atomic top app bar component following Material Design 3 guidelines
 * 
 * Features:
 * - Consistent styling across all screens
 * - Flexible navigation and action handling
 * - Multiple variants (standard, center-aligned)
 * - Overflow text handling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtomicTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    centered: Boolean = false
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = AtomicDesignSystem.colors.Surface,
        titleContentColor = AtomicDesignSystem.colors.OnSurface,
        navigationIconContentColor = AtomicDesignSystem.colors.OnSurface,
        actionIconContentColor = AtomicDesignSystem.colors.OnSurface
    )
    
    if (centered) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = AtomicDesignSystem.typography.HeadlineSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = modifier,
            navigationIcon = navigationIcon ?: {},
            actions = actions,
            colors = colors
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = AtomicDesignSystem.typography.HeadlineSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = modifier,
            navigationIcon = navigationIcon ?: {},
            actions = actions,
            colors = colors
        )
    }
}

/**
 * Atomic top bar with back navigation
 */
@Composable
fun AtomicTopBarWithBack(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable (RowScope.() -> Unit) = {},
    centered: Boolean = false
) {
    AtomicTopBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = actions,
        centered = centered
    )
}

@Preview(showBackground = true)
@Composable
private fun AtomicTopBarPreview() {
    AtomicTheme {
        AtomicTopBarWithBack(
            title = "Weather Details",
            onBackClick = { /* Back action */ },
            centered = true
        )
    }
}