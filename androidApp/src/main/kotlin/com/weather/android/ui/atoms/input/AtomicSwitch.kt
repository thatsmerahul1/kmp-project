package com.weather.android.ui.atoms.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Atomic switch component with optional label
 * 
 * Features:
 * - Consistent switch styling
 * - Optional label text with proper spacing
 * - Atomic design system integration
 */
@Composable
fun AtomicSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                style = AtomicDesignSystem.typography.BodyMedium,
                color = if (enabled) {
                    AtomicDesignSystem.colors.OnSurface
                } else {
                    AtomicDesignSystem.colors.OnSurface.copy(alpha = 0.38f)
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(AtomicDesignSystem.spacing.SM))
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AtomicDesignSystem.colors.OnPrimary,
                checkedTrackColor = AtomicDesignSystem.colors.Primary,
                uncheckedThumbColor = AtomicDesignSystem.colors.Outline,
                uncheckedTrackColor = AtomicDesignSystem.colors.SurfaceVariant,
                disabledCheckedThumbColor = AtomicDesignSystem.colors.Surface,
                disabledCheckedTrackColor = AtomicDesignSystem.colors.OnSurface.copy(alpha = 0.12f),
                disabledUncheckedThumbColor = AtomicDesignSystem.colors.OnSurface.copy(alpha = 0.38f),
                disabledUncheckedTrackColor = AtomicDesignSystem.colors.SurfaceVariant.copy(alpha = 0.12f)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AtomicSwitchPreview() {
    AtomicTheme {
        AtomicSwitch(
            checked = true,
            onCheckedChange = {},
            label = "Dark mode",
            modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
        )
    }
}