package com.weather.android.ui.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.button.PrimaryButton
import com.weather.android.ui.atoms.button.SecondaryButton
import com.weather.android.ui.atoms.card.AtomicCard
import com.weather.android.ui.atoms.card.AtomicElevatedCard
import com.weather.android.ui.atoms.card.AtomicOutlinedCard
import com.weather.android.ui.atoms.input.AtomicCheckbox
import com.weather.android.ui.atoms.input.AtomicSearchField
import com.weather.android.ui.atoms.input.AtomicSwitch
import com.weather.android.ui.atoms.input.AtomicTextField
import com.weather.android.ui.atoms.navigation.AtomicBottomBar
import com.weather.android.ui.atoms.navigation.AtomicBottomBarItem
import com.weather.android.ui.atoms.navigation.AtomicTopBarWithBack
import com.weather.android.ui.atoms.navigation.BottomNavItem
import com.weather.android.ui.molecules.form.SearchForm
import com.weather.android.ui.molecules.form.SettingsForm
import com.weather.android.ui.molecules.form.SettingsFormData
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Showcase template demonstrating all atomic components and molecules
 * 
 * This template serves as:
 * - Documentation of available components
 * - Visual testing of component integration
 * - Design system validation
 * - Component behavior demonstration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtomicComponentShowcase(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var textFieldValue by remember { mutableStateOf("") }
    var checkboxChecked by remember { mutableStateOf(false) }
    var switchChecked by remember { mutableStateOf(true) }
    var settingsData by remember { mutableStateOf(SettingsFormData()) }
    var selectedBottomNavItem by remember { mutableStateOf("home") }
    
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Favorites", Icons.Default.Favorite, "favorites"),
        BottomNavItem("Settings", Icons.Default.Settings, "settings")
    )
    
    Scaffold(
        topBar = {
            AtomicTopBarWithBack(
                title = "Atomic Components",
                onBackClick = { /* Back navigation */ },
                centered = true
            )
        },
        bottomBar = {
            AtomicBottomBar {
                bottomNavItems.forEach { item ->
                    AtomicBottomBarItem(
                        selected = selectedBottomNavItem == item.route,
                        onClick = { selectedBottomNavItem = item.route },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = AtomicDesignSystem.typography.LabelMedium
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(AtomicDesignSystem.spacing.MD)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.MD)
        ) {
            // Input Atoms Section
            Text(
                text = "Input Atoms",
                style = AtomicDesignSystem.typography.HeadlineSmall
            )
            
            AtomicTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                label = "Text Field",
                placeholder = "Enter some text..."
            )
            
            AtomicSearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search locations..."
            )
            
            AtomicCheckbox(
                checked = checkboxChecked,
                onCheckedChange = { checkboxChecked = it },
                label = "Enable notifications"
            )
            
            AtomicSwitch(
                checked = switchChecked,
                onCheckedChange = { switchChecked = it },
                label = "Dark mode"
            )
            
            Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.SM))
            
            // Button Atoms Section
            Text(
                text = "Button Atoms",
                style = AtomicDesignSystem.typography.HeadlineSmall
            )
            
            PrimaryButton(
                text = "Primary Action",
                onClick = { /* Primary action */ }
            )
            
            SecondaryButton(
                text = "Secondary Action",
                onClick = { /* Secondary action */ }
            )
            
            Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.SM))
            
            // Card Atoms Section
            Text(
                text = "Card Atoms",
                style = AtomicDesignSystem.typography.HeadlineSmall
            )
            
            AtomicCard(
                onClick = { /* Card click */ }
            ) {
                Text(
                    text = "Basic Card",
                    modifier = Modifier.padding(AtomicDesignSystem.spacing.MD),
                    style = AtomicDesignSystem.typography.BodyLarge
                )
            }
            
            AtomicOutlinedCard {
                Text(
                    text = "Outlined Card",
                    modifier = Modifier.padding(AtomicDesignSystem.spacing.MD),
                    style = AtomicDesignSystem.typography.BodyLarge
                )
            }
            
            AtomicElevatedCard(
                onClick = { /* Elevated card click */ }
            ) {
                Text(
                    text = "Elevated Card (Clickable)",
                    modifier = Modifier.padding(AtomicDesignSystem.spacing.MD),
                    style = AtomicDesignSystem.typography.BodyLarge
                )
            }
            
            Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.SM))
            
            // Molecule Forms Section
            Text(
                text = "Form Molecules",
                style = AtomicDesignSystem.typography.HeadlineSmall
            )
            
            SearchForm(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearch = { /* Search action */ }
            )
            
            Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.SM))
            
            SettingsForm(
                settingsData = settingsData,
                onSettingsChange = { settingsData = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AtomicComponentShowcasePreview() {
    AtomicTheme {
        AtomicComponentShowcase()
    }
}