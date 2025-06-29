package com.weather.android.ui.atoms.button

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

@Composable
fun AtomicIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = AtomicDesignSystem.colors.OnSurface
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = tint,
            disabledContentColor = AtomicDesignSystem.colors.OnSurfaceDisabled
        ),
        modifier = modifier.size(AtomicDesignSystem.spacing.MinTouchTarget)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(AtomicDesignSystem.spacing.IconSize)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AtomicIconButtonPreview() {
    AtomicTheme {
        AtomicIconButton(
            icon = Icons.Default.Refresh,
            contentDescription = "Refresh",
            onClick = {}
        )
    }
}