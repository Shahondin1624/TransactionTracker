package com.github.shahondin1624.composables

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SingleLineActionTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    onActionCondition: () -> Boolean = { true },
    onAction: () -> Unit = {},
    placeholder: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (onActionCondition()) {
                onAction()
            }
        })
    )
}