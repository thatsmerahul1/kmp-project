package com.weather.android.ui.atoms.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Atomic search field component optimized for search interactions
 * 
 * Features:
 * - Search icon leading indicator
 * - Clear button when text is present
 * - Search-optimized keyboard options
 * - IME action handling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtomicSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    enabled: Boolean = true,
    onSearch: (() -> Unit)? = null,
    onClear: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch?.invoke()
                keyboardController?.hide()
            }
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AtomicDesignSystem.colors.OnSurfaceVariant
            )
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(
                    onClick = {
                        onClear?.invoke() ?: onValueChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = AtomicDesignSystem.colors.OnSurfaceVariant
                    )
                }
            }
        } else null,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = AtomicDesignSystem.colors.Surface,
            focusedBorderColor = AtomicDesignSystem.colors.Primary,
            unfocusedBorderColor = AtomicDesignSystem.colors.Outline,
            cursorColor = AtomicDesignSystem.colors.Primary
        ),
        shape = AtomicDesignSystem.shapes.SearchField
    )
}

@Preview(showBackground = true)
@Composable
private fun AtomicSearchFieldPreview() {
    AtomicTheme {
        AtomicSearchField(
            value = "Weather search",
            onValueChange = {},
            placeholder = "Search locations..."
        )
    }
}