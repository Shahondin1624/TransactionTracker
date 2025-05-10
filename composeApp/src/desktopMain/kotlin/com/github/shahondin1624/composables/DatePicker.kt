package com.github.shahondin1624.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun DatePicker(
    focusRequester: FocusRequester,
    nextFocusRequester: FocusRequester? = null,
    labelText: String = "Enter Date (DD.MM.YYYY)",
    onValidDateTyped: (String) -> Unit = {}
) {
    var dateText by remember { mutableStateOf(TextFieldValue("")) }
    var isError by remember { mutableStateOf(false) }

    var modifier = Modifier.fillMaxWidth()
        .focusRequester(focusRequester);
    if (nextFocusRequester != null) {
        modifier = modifier.focusProperties {
            next = nextFocusRequester
        }
    }

    OutlinedTextField(
        value = dateText,
        onValueChange = { newValue: TextFieldValue ->
            if (newValue.text.isEmpty() || newValue.text.all { it.isDigit() || it == '.' }) {
                dateText = newValue
                isError = newValue.text.isNotEmpty() && !isValidDate(newValue.text)
                if (!isError) {
                    onValidDateTyped(newValue.text)
                }
            }
        },
        placeholder = { Text("DD.MM.YYYY") },
        label = { Text(labelText) },
        modifier = modifier,
        singleLine = true,
        isError = isError,
    )
}


private fun isValidDate(text: String): Boolean {
    if (!text.matches(Regex("""^\d{2}\.\d{2}\.\d{4}$"""))) return false
    val (day, month, year) = text.split(".").map { it.toInt() }
    return when (month) {
        in 1..12 -> when (month) {
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) day in 1..29 else day in 1..28
            in listOf(4, 6, 9, 11) -> day in 1..30
            else -> day in 1..31
        }

        else -> false
    }
}
