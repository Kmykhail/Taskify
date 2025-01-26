package com.kote.taskifyapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CustomTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions? = null,
    isSingleLine: Boolean = false,
    readOnly: Boolean = true,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        placeholder = { Text(text = placeholder) },
        onValueChange = { onValueChange(it) },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        ),
        keyboardOptions = keyboardOptions ?: KeyboardOptions(),
        keyboardActions = keyboardActions ?: KeyboardActions(),
        singleLine = isSingleLine,
        readOnly = readOnly,
        modifier = modifier
    )
}