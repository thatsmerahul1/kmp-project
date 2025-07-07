package com.weather.android.ui.molecules.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.card.AtomicCard
import com.weather.android.ui.atoms.input.AtomicCheckbox
import com.weather.android.ui.atoms.input.AtomicSwitch
import com.weather.android.ui.atoms.input.AtomicTextField
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Settings form molecule combining various input atoms
 * 
 * Features:
 * - User preferences configuration
 * - Multiple input types (text, switch, checkbox)
 * - Grouped settings sections
 * - Validation support
 */
data class SettingsFormData(
    val defaultLocation: String = "",
    val enableNotifications: Boolean = true,
    val enableDarkMode: Boolean = false,
    val enableAutoRefresh: Boolean = true,
    val enableLocationServices: Boolean = false
)

@Composable
fun SettingsForm(
    settingsData: SettingsFormData,
    onSettingsChange: (SettingsFormData) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Location Settings Section
        AtomicCard {
            Column(
                modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
            ) {
                Text(
                    text = "Location Settings",
                    style = AtomicDesignSystem.typography.HeadlineSmall,
                    color = AtomicDesignSystem.colors.OnSurface
                )
                
                Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.SM))
                
                AtomicTextField(
                    value = settingsData.defaultLocation,
                    onValueChange = { newLocation ->
                        onSettingsChange(settingsData.copy(defaultLocation = newLocation))
                    },
                    label = "Default Location",
                    placeholder = "Enter city name..."
                )
                
                Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.SM))
                
                AtomicSwitch(
                    checked = settingsData.enableLocationServices,
                    onCheckedChange = { enabled ->
                        onSettingsChange(settingsData.copy(enableLocationServices = enabled))
                    },
                    label = "Enable Location Services"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.MD))
        
        // App Preferences Section
        AtomicCard {
            Column(
                modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
            ) {
                Text(
                    text = "App Preferences",
                    style = AtomicDesignSystem.typography.HeadlineSmall,
                    color = AtomicDesignSystem.colors.OnSurface
                )
                
                Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.SM))
                
                AtomicSwitch(
                    checked = settingsData.enableDarkMode,
                    onCheckedChange = { enabled ->
                        onSettingsChange(settingsData.copy(enableDarkMode = enabled))
                    },
                    label = "Dark Mode"
                )
                
                Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.XS))
                
                AtomicSwitch(
                    checked = settingsData.enableAutoRefresh,
                    onCheckedChange = { enabled ->
                        onSettingsChange(settingsData.copy(enableAutoRefresh = enabled))
                    },
                    label = "Auto Refresh Weather Data"
                )
                
                Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.XS))
                
                AtomicCheckbox(
                    checked = settingsData.enableNotifications,
                    onCheckedChange = { enabled ->
                        onSettingsChange(settingsData.copy(enableNotifications = enabled))
                    },
                    label = "Enable Push Notifications"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsFormPreview() {
    var settingsData by remember { mutableStateOf(SettingsFormData()) }
    
    AtomicTheme {
        SettingsForm(
            settingsData = settingsData,
            onSettingsChange = { settingsData = it },
            modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
        )
    }
}