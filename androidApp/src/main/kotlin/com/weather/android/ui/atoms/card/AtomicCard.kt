package com.weather.android.ui.atoms.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Basic atomic card component - the foundation for all card-based UI elements
 * 
 * This component provides:
 * - Consistent card styling across the app
 * - Flexible elevation and color options
 * - Optional click handling
 * - Border support
 * - Customizable shapes
 */
@Composable
fun AtomicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = AtomicDesignSystem.shapes.Card,
    containerColor: Color = AtomicDesignSystem.colors.Surface,
    contentColor: Color = AtomicDesignSystem.colors.OnSurface,
    elevation: Dp = AtomicDesignSystem.spacing.XS,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.38f),
            disabledContentColor = contentColor.copy(alpha = 0.38f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            pressedElevation = if (onClick != null) elevation * 1.5f else elevation,
            focusedElevation = if (onClick != null) elevation * 1.2f else elevation,
            hoveredElevation = if (onClick != null) elevation * 1.1f else elevation
        ),
        border = border
    ) {
        content()
    }
}

/**
 * Outlined variant of AtomicCard
 */
@Composable
fun AtomicOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = AtomicDesignSystem.shapes.Card,
    containerColor: Color = AtomicDesignSystem.colors.Surface,
    contentColor: Color = AtomicDesignSystem.colors.OnSurface,
    borderColor: Color = AtomicDesignSystem.colors.Outline,
    content: @Composable ColumnScope.() -> Unit
) {
    AtomicCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = AtomicDesignSystem.spacing.None,
        border = BorderStroke(
            width = AtomicDesignSystem.spacing.BorderWidth,
            color = borderColor
        ),
        content = content
    )
}

/**
 * Elevated variant of AtomicCard
 */
@Composable
fun AtomicElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = AtomicDesignSystem.shapes.Card,
    containerColor: Color = AtomicDesignSystem.colors.SurfaceContainer,
    contentColor: Color = AtomicDesignSystem.colors.OnSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    AtomicCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = AtomicDesignSystem.spacing.SM,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun AtomicCardPreview() {
    AtomicTheme {
        Column(
            modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
        ) {
            AtomicCard {
                Box(modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)) {
                    Text("Basic Card")
                }
            }
            
            AtomicOutlinedCard(
                modifier = Modifier.padding(top = AtomicDesignSystem.spacing.SM)
            ) {
                Box(modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)) {
                    Text("Outlined Card")
                }
            }
            
            AtomicElevatedCard(
                modifier = Modifier.padding(top = AtomicDesignSystem.spacing.SM),
                onClick = { /* Click action */ }
            ) {
                Box(modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)) {
                    Text("Elevated Clickable Card")
                }
            }
        }
    }
}