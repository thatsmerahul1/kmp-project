package com.weather.android.ui.atoms.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.ExperimentalMaterial3Api
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

/**
 * Atomic text field component following atomic design principles
 * 
 * This is the most basic input atom that can be composed into more complex input molecules
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtomicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = AtomicDesignSystem.colors.Surface,
            focusedBorderColor = AtomicDesignSystem.colors.Primary,
            unfocusedBorderColor = AtomicDesignSystem.colors.Outline,
            errorBorderColor = AtomicDesignSystem.colors.Error,
            focusedLabelColor = AtomicDesignSystem.colors.Primary,
            unfocusedLabelColor = AtomicDesignSystem.colors.OnSurfaceVariant,
            errorLabelColor = AtomicDesignSystem.colors.Error,
            cursorColor = AtomicDesignSystem.colors.Primary
        ),
        shape = AtomicDesignSystem.shapes.InputField
    )
    
    // Error message support
    if (isError && !errorMessage.isNullOrBlank()) {
        Text(
            text = errorMessage,
            color = AtomicDesignSystem.colors.Error,
            style = AtomicDesignSystem.typography.BodySmall,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AtomicTextFieldPreview() {
    AtomicTheme {
        AtomicTextField(
            value = "Sample text",
            onValueChange = {},
            label = "Label",
            placeholder = "Enter text here"
        )
    }
}