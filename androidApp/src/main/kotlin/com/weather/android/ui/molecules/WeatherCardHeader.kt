package com.weather.android.ui.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.icon.WeatherIcon
import com.weather.android.ui.atoms.text.BodyText
import com.weather.android.ui.atoms.text.HeadlineText
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate

@Composable
fun WeatherCardHeader(
    title: String,
    subtitle: String? = null,
    condition: WeatherCondition,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title and subtitle
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.XS)
        ) {
            HeadlineText(
                text = title,
                color = AtomicDesignSystem.colors.OnSurface
            )
            
            subtitle?.let {
                BodyText(
                    text = it,
                    color = AtomicDesignSystem.colors.OnSurfaceVariant
                )
            }
        }
        
        // Weather icon
        WeatherIcon(
            condition = condition,
            size = AtomicDesignSystem.spacing.IconSizeLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherCardHeaderPreview() {
    AtomicTheme {
        WeatherCardHeader(
            title = "Today",
            subtitle = "January 15, 2024",
            condition = WeatherCondition.CLEAR
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherCardHeaderNoSubtitlePreview() {
    AtomicTheme {
        WeatherCardHeader(
            title = "Tomorrow",
            condition = WeatherCondition.RAIN
        )
    }
}