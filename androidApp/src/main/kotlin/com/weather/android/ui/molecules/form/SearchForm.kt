package com.weather.android.ui.molecules.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.button.PrimaryButton
import com.weather.android.ui.atoms.input.AtomicSearchField
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Search form molecule composed of atomic search field and action button
 * 
 * Features:
 * - Location search functionality
 * - Input validation
 * - Loading state support
 * - Error handling
 */
@Composable
fun SearchForm(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    placeholder: String = "Search for a location...",
    searchButtonText: String = "Search Weather"
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        AtomicSearchField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = placeholder,
            enabled = !isLoading,
            onSearch = {
                if (searchQuery.isNotBlank()) {
                    onSearch()
                }
            }
        )
        
        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.XS))
            Text(
                text = errorMessage,
                color = AtomicDesignSystem.colors.Error,
                style = AtomicDesignSystem.typography.BodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.SM))
        
        PrimaryButton(
            text = searchButtonText,
            onClick = onSearch,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && searchQuery.isNotBlank(),
            isLoading = isLoading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchFormPreview() {
    var searchQuery by remember { mutableStateOf("") }
    
    AtomicTheme {
        SearchForm(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSearch = { /* Search action */ }
        )
    }
}