package com.weather.android.ui.atoms.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Atomic checkbox component with optional label
 * 
 * Features:
 * - Consistent checkbox styling
 * - Optional label text with proper spacing
 * - Atomic design system integration
 */
@Composable
fun AtomicCheckbox(
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
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = AtomicDesignSystem.colors.Primary,
                uncheckedColor = AtomicDesignSystem.colors.OnSurfaceVariant,
                checkmarkColor = AtomicDesignSystem.colors.OnPrimary,
                disabledCheckedColor = AtomicDesignSystem.colors.OnSurface.copy(alpha = 0.38f),
                disabledUncheckedColor = AtomicDesignSystem.colors.OnSurface.copy(alpha = 0.38f)
            )
        )
        
        if (!label.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(AtomicDesignSystem.spacing.XS))
            Text(
                text = label,
                style = AtomicDesignSystem.typography.BodyMedium,
                color = if (enabled) {
                    AtomicDesignSystem.colors.OnSurface
                } else {
                    AtomicDesignSystem.colors.OnSurface.copy(alpha = 0.38f)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AtomicCheckboxPreview() {
    AtomicTheme {
        AtomicCheckbox(
            checked = true,
            onCheckedChange = {},
            label = "Enable notifications",
            modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
        )
    }
}