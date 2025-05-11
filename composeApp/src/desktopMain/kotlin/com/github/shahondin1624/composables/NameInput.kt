package com.github.shahondin1624.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_DEFAULT

@Composable
fun NameInput(
    firstName: String,
    lastName: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    nextFocus: FocusRequester
) {
    val firstNameFocus = remember { FocusRequester() }
    val lastNameFocus = remember { FocusRequester() }
    OutlinedTextField(
        value = firstName,
        onValueChange = { onFirstNameChange(it) },
        label = { Text("First Name") },
        modifier = Modifier.fillMaxWidth()
            .focusRequester(firstNameFocus)
            .focusProperties {
                next = lastNameFocus
                canFocus = true
            },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(SPACER_HEIGHT_DEFAULT))

    OutlinedTextField(
        value = lastName,
        onValueChange = { onLastNameChange(it) },
        label = { Text("Last Name") },
        modifier = Modifier.fillMaxWidth()
            .focusRequester(lastNameFocus)
            .focusProperties {
                next = nextFocus
                canFocus = true
            },
        singleLine = true
    )
}