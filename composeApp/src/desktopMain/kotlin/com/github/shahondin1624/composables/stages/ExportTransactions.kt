package com.github.shahondin1624.composables.stages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_DEFAULT
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_LARGE
import com.github.shahondin1624.composables.CurrencyInputField
import com.github.shahondin1624.composables.FilePicker
import com.github.shahondin1624.composables.NameInput
import com.github.shahondin1624.viewmodel.TransactionTrackerViewModel
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.awt.FileDialog
import java.io.File

@Composable
fun ExportTransactions(vm: TransactionTrackerViewModel) {
    val uiState by vm.uiState.collectAsState()
    val transactionState = vm.transactions.collectAsState()
    val scope = rememberCoroutineScope()

    val initialAmountFocus = remember { FocusRequester() }

    NameInput(
        firstName = uiState.firstName,
        lastName = uiState.lastName,
        onFirstNameChange = { vm.setFirstName(it) },
        onLastNameChange = { vm.setLastName(it) },
        nextFocus = initialAmountFocus
    )

    Spacer(modifier = Modifier.height(SPACER_HEIGHT_DEFAULT))

    CurrencyInputField(
        value = uiState.initialAmount,
        onValueChange = { vm.setInitialAmount(it) },
        focusRequester = initialAmountFocus
    )

    FilePicker(
        fileDialogTitle = "Select Output XLSX File",
        fileDialogType = FileDialog.SAVE,
        filters = listOf("*.xlsx"),
        labelText = "Output File Path",
        onPathSelected = { vm.setOutputFilePath(it) }
    )

    Button(
        onClick = {
            scope.launch {
                vm.exportTransactions()
            }
        },
        enabled = uiState.outputFilePath.isNotEmpty() &&
                uiState.firstName.isNotEmpty() && uiState.lastName.isNotEmpty() &&
                uiState.startDate != null && uiState.endDate != null
                && !uiState.generating
    ) {
        Text("Generate Output")
    }

    if (uiState.generating) {
        Spacer(modifier = Modifier.height(SPACER_HEIGHT_LARGE))
        CircularProgressIndicator()
    }

    uiState.successState?.let { filePath ->
        Text(
            text = "Generated $filePath",
            color = MaterialTheme.colors.primary,
            modifier = Modifier.clickable {
                try {
                    Desktop.getDesktop().open(File(filePath))
                } catch (e: Exception) {
                    vm.setError(e.message)
                }
            }
        )
    }

    uiState.errorState?.let { error ->
        AlertDialog(
            onDismissRequest = { vm.setError(error) },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                Button(onClick = { vm.setError(null) }) {
                    Text("OK")
                }
            }
        )
    }
}