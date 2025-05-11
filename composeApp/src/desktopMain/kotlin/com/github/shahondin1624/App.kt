package com.github.shahondin1624

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.github.shahondin1624.composables.DateRangePicker
import com.github.shahondin1624.composables.FilePicker
import com.github.shahondin1624.viewmodel.TransactionTrackerViewModel
import java.awt.Desktop
import java.awt.FileDialog
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

@Composable
@Preview
fun App(vm: TransactionTrackerViewModel = TransactionTrackerViewModel()) {
    MaterialTheme {
        val uiState by vm.uiState.collectAsState()

        val firstNameFocus = remember { FocusRequester() }
        val lastNameFocus = remember { FocusRequester() }

        val startDateFocus = remember { FocusRequester() }
        val endDateFocus = remember { FocusRequester() }

        val initialAmountFocus = remember { FocusRequester() }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilePicker(
                fileDialogTitle = "Select Input CSV File",
                fileDialogType = FileDialog.LOAD,
                filters = listOf("*.csv"),
                labelText = "Input File Path",
                onPathSelected = { vm.setInputFilePath(it) })

            Spacer(modifier = Modifier.height(16.dp))

            FilePicker(
                fileDialogTitle = "Select Output XLSX File",
                fileDialogType = FileDialog.SAVE,
                filters = listOf("*.xlsx"),
                labelText = "Output File Path",
                onPathSelected = { vm.setOutputFilePath(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = { vm.setFirstName(it) },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(firstNameFocus)
                    .focusProperties {
                        next = lastNameFocus
                        canFocus = true
                    },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = { vm.setLastName(it) },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(lastNameFocus)
                    .focusProperties {
                        next = startDateFocus
                        canFocus = true
                    },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateRangePicker(startDateFocus, endDateFocus, initialAmountFocus) { (start, end) ->
                vm.setStartDate(start)
                vm.setEndDate(end)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = if (uiState.initialAmount == 0.0) "" else String.format("%.2f", uiState.initialAmount)
                    .replace(".", ","),
                onValueChange = { input: String ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*,?\\d*\$"))) {
                        vm.setInitialAmount(if (input.isEmpty()) 0.0 else input.replace(",", ".").toDouble())
                    }
                },
                label = { Text("Starting value in €") },
                placeholder = { Text("XX,XX") },
                trailingIcon = { Text("€") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(initialAmountFocus)
            )


            Button(
                onClick = {
                    println("Starting generation...")
                    vm.setGenerating(true)
                    val metaInformation =
                        createDTO(uiState.firstName, uiState.lastName, uiState.startDate!!, uiState.endDate!!)
                    println("Generated meta information: $metaInformation")
                    generate(
                        uiState.inputFilePath, uiState.outputFilePath, metaInformation, uiState.initialAmount,
                        onError = { exception ->
                            val sw = StringWriter()
                            exception.printStackTrace(PrintWriter(sw))
                            println("Error occurred: $exception")
                            vm.setError(sw.toString())
                            vm.setGenerating(false)
                        },
                        onSuccess = {
                            println("Generated output file successfully")
                            vm.setSuccessState(uiState.outputFilePath)
                            vm.setGenerating(false)
                        })
                },
                enabled = (uiState.inputFilePath.isNotEmpty() && uiState.outputFilePath.isNotEmpty() &&
                        uiState.firstName.isNotEmpty() && uiState.lastName.isNotEmpty() &&
                        uiState.startDate != null && uiState.endDate != null) && !uiState.generating
            ) {
                Text("Generate Output")
            }

            if (uiState.generating) {
                Spacer(modifier = Modifier.height(16.dp))
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
    }
}


