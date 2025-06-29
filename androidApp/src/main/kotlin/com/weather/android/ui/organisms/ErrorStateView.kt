package com.weather.android.ui.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weather.android.ui.atoms.button.PrimaryButton
import com.weather.android.ui.atoms.button.SecondaryButton
import com.weather.android.ui.atoms.text.BodyText
import com.weather.android.ui.atoms.text.HeadlineText
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

enum class ErrorType {
    NETWORK,
    SERVER,
    UNKNOWN
}

@Composable
fun ErrorStateView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorType: ErrorType = ErrorType.UNKNOWN,
    onDismiss: (() -> Unit)? = null
) {
    val errorData = getErrorDisplayData(errorType, errorMessage)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AtomicDesignSystem.spacing.ScreenPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = errorData.icon,
            contentDescription = null,
            tint = AtomicDesignSystem.colors.Error,
            modifier = Modifier.size(64.dp)
        )
        
        HeadlineText(
            text = errorData.title,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = AtomicDesignSystem.spacing.MD)
        )
        
        BodyText(
            text = errorData.description,
            textAlign = TextAlign.Center,
            color = AtomicDesignSystem.colors.OnSurfaceVariant,
            modifier = Modifier.padding(bottom = AtomicDesignSystem.spacing.LG)
        )
        
        PrimaryButton(
            text = "Try Again",
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        )
        
        onDismiss?.let { dismiss ->
            SecondaryButton(
                text = "Dismiss",
                onClick = dismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AtomicDesignSystem.spacing.SM)
            )
        }
    }
}

@Composable
fun ErrorCard(
    errorMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    errorType: ErrorType = ErrorType.UNKNOWN
) {
    val errorData = getErrorDisplayData(errorType, errorMessage)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AtomicDesignSystem.colors.Error.copy(alpha = 0.1f),
            contentColor = AtomicDesignSystem.colors.OnSurface
        ),
        shape = AtomicDesignSystem.shapes.Card
    ) {
        Column(
            modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
        ) {
            HeadlineText(
                text = errorData.title,
                color = AtomicDesignSystem.colors.Error
            )
            
            BodyText(
                text = errorData.description,
                color = AtomicDesignSystem.colors.OnSurfaceVariant,
                modifier = Modifier.padding(vertical = AtomicDesignSystem.spacing.SM)
            )
            
            SecondaryButton(
                text = "Dismiss",
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

private data class ErrorDisplayData(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private fun getErrorDisplayData(errorType: ErrorType, message: String): ErrorDisplayData {
    return when (errorType) {
        ErrorType.NETWORK -> ErrorDisplayData(
            icon = Icons.Default.Warning, // Using available icon as placeholder
            title = "No Internet Connection",
            description = "Please check your internet connection and try again."
        )
        ErrorType.SERVER -> ErrorDisplayData(
            icon = Icons.Default.Warning, // Using available icon as placeholder
            title = "Server Error",
            description = "We're having trouble connecting to our servers. Please try again later."
        )
        ErrorType.UNKNOWN -> ErrorDisplayData(
            icon = Icons.Default.Warning, // Using available icon as placeholder
            title = "Something went wrong",
            description = message.ifEmpty { "An unexpected error occurred. Please try again." }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStateViewPreview() {
    AtomicTheme {
        ErrorStateView(
            errorMessage = "Failed to load weather data",
            onRetry = {},
            errorType = ErrorType.NETWORK
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorCardPreview() {
    AtomicTheme {
        ErrorCard(
            errorMessage = "Failed to refresh weather data",
            onDismiss = {},
            errorType = ErrorType.SERVER
        )
    }
}