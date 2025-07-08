package com.weather.android.ui.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.weather.android.ui.atoms.button.PrimaryButton
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.presentation.state.ConnectionStatus

@Composable
fun ErrorStateView(
    error: String,
    connectionStatus: ConnectionStatus = ConnectionStatus.UNKNOWN,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, title, description) = when (connectionStatus) {
        ConnectionStatus.OFFLINE -> Triple(
            Icons.Default.Info,
            "No Internet Connection",
            "Please check your internet connection and try again."
        )
        ConnectionStatus.CONNECTED -> Triple(
            Icons.Default.Warning,
            "Something went wrong",
            error
        )
        ConnectionStatus.UNKNOWN -> Triple(
            Icons.Default.Warning,
            "Unable to load weather data",
            error
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PrimaryButton(
            text = "Try Again",
            onClick = onRetry
        )
    }
}

@Composable
fun EmptyStateView(
    title: String = "No weather data available",
    description: String = "Pull down to refresh or check your connection",
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PrimaryButton(
            text = "Refresh",
            onClick = onRefresh
        )
    }
}

@Composable
fun NoDataOfflineView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorStateView(
        error = "No cached data available. Please connect to the internet to load weather information.",
        connectionStatus = ConnectionStatus.OFFLINE,
        onRetry = onRetry,
        modifier = modifier
    )
}