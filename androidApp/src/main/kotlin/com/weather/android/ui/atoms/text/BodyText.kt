package com.weather.android.ui.atoms.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AtomicDesignSystem.colors.OnSurface,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        text = text,
        style = AtomicDesignSystem.typography.BodyLarge,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun BodyTextPreview() {
    AtomicTheme {
        BodyText(text = "Partly cloudy with a chance of rain in the afternoon")
    }
}