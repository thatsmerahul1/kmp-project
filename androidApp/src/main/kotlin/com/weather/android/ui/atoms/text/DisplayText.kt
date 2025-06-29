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
fun DisplayText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AtomicDesignSystem.colors.OnSurface,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        style = AtomicDesignSystem.typography.DisplayLarge,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun DisplayTextPreview() {
    AtomicTheme {
        DisplayText(text = "22°")
    }
}