package com.weather.android.ui.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.text.HeadlineText
import com.weather.android.ui.organisms.ErrorStateView
import com.weather.android.ui.organisms.ErrorType
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorTemplate(
    title: String,
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorType: ErrorType = ErrorType.UNKNOWN,
    onNavigateBack: (() -> Unit)? = null,
    topBarActions: @Composable (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    HeadlineText(
                        text = title,
                        color = AtomicDesignSystem.colors.OnPrimary
                    )
                },
// Navigation icon temporarily removed to fix build
                actions = {
                    topBarActions?.invoke()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AtomicDesignSystem.colors.Primary,
                    titleContentColor = AtomicDesignSystem.colors.OnPrimary,
                    navigationIconContentColor = AtomicDesignSystem.colors.OnPrimary,
                    actionIconContentColor = AtomicDesignSystem.colors.OnPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ErrorStateView(
                errorMessage = errorMessage,
                onRetry = onRetry,
                errorType = errorType
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorTemplatePreview() {
    AtomicTheme {
        ErrorTemplate(
            title = "Weather Error",
            errorMessage = "Failed to load weather data. Please check your internet connection.",
            onRetry = {},
            errorType = ErrorType.NETWORK
        )
    }
}