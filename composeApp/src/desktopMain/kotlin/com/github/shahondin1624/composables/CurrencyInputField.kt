package com.github.shahondin1624.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun CurrencyInputField(
    value: Double = 0.0,
    onValueChange: (Double) -> Unit = {},
    currency: String = "€",
    focusRequester: FocusRequester,
    nextFocus: FocusRequester? = null,
) {
    OutlinedTextField(
        value = if (value == 0.0) "" else String.format("%.2f", value)
            .replace(".", ","),
        onValueChange = { input: String ->
            onlyAllowDigits(input) {
                onValueChange(it)
            }
        },
        label = { Text("Starting value in $currency") },
        placeholder = { Text("XX,XX") },
        trailingIcon = { Text(currency) },
        modifier = Modifier.fillMaxWidth()
            .focusRequester(focusRequester).let {
                if (nextFocus != null) {
                    return@let it.focusRequester(nextFocus)
                }
                return@let it
            }
    )
}

private fun onlyAllowDigits(input: String, onChange: (Double) -> Unit) {
    if (input.isEmpty() || input.matches(Regex("^\\d*,?\\d*$"))) {
        onChange(if (input.isEmpty()) 0.0 else input.replace(",", ".").toDouble())
    }
}