package com.weather.android.ui.atoms.button

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = AtomicDesignSystem.colors.Primary,
            contentColor = AtomicDesignSystem.colors.OnPrimary,
            disabledContainerColor = AtomicDesignSystem.colors.OnSurfaceDisabled,
            disabledContentColor = AtomicDesignSystem.colors.OnSurfaceVariant
        ),
        shape = AtomicDesignSystem.shapes.Button,
        modifier = modifier.height(AtomicDesignSystem.spacing.ButtonHeight)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = AtomicDesignSystem.colors.OnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = AtomicDesignSystem.typography.LabelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    AtomicTheme {
        PrimaryButton(
            text = "Refresh Weather",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonLoadingPreview() {
    AtomicTheme {
        PrimaryButton(
            text = "Loading...",
            onClick = {},
            isLoading = true
        )
    }
}