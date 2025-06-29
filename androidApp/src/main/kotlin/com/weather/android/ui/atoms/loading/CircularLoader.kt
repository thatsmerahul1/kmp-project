package com.weather.android.ui.atoms.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

@Composable
fun CircularLoader(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color = AtomicDesignSystem.colors.Primary,
    strokeWidth: Dp = 4.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth
    )
}

@Composable
fun CircularLoaderCentered(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color = AtomicDesignSystem.colors.Primary,
    strokeWidth: Dp = 4.dp
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularLoader(
            size = size,
            color = color,
            strokeWidth = strokeWidth
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CircularLoaderPreview() {
    AtomicTheme {
        CircularLoader()
    }
}

@Preview(showBackground = true)
@Composable
private fun CircularLoaderCenteredPreview() {
    AtomicTheme {
        CircularLoaderCentered(
            modifier = Modifier.size(200.dp)
        )
    }
}