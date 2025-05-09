package com.github.shahondin1624

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.awt.FileDialog
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

@Composable
@Preview
fun App() {
    MaterialTheme {
        var inputFilePath by remember { mutableStateOf("") }
        var outputFilePath by remember { mutableStateOf("") }


        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        val firstNameFocus = remember { FocusRequester() }
        val lastNameFocus = remember { FocusRequester() }


        var startDate by remember { mutableStateOf<Date?>(null) }
        var endDate by remember { mutableStateOf<Date?>(null) }
        val startDateFocus = remember { FocusRequester() }
        val endDateFocus = remember { FocusRequester() }


        var initialAmount by remember { mutableStateOf(0.0) }
        val initialAmountFocus = remember { FocusRequester() }


        var generating by remember { mutableStateOf(false) }
        var errorState by remember { mutableStateOf<String?>(null) }
        var successState by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilePicker(
                fileDialogTitle = "Select Input CSV File",
                fileDialogType = FileDialog.LOAD,
                filters = listOf("*.csv"),
                labelText = "Input File Path",
                onPathSelected = { inputFilePath = it })

            Spacer(modifier = Modifier.height(16.dp))

            FilePicker(
                fileDialogTitle = "Select Output XLSX File",
                fileDialogType = FileDialog.SAVE,
                filters = listOf("*.xlsx"),
                labelText = "Output File Path",
                onPathSelected = { outputFilePath = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
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
                value = lastName,
                onValueChange = { lastName = it },
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
                startDate = start
                endDate = end
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = if (initialAmount == 0.0) "" else String.format("%.2f", initialAmount).replace(".", ","),
                onValueChange = { input: String ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*,?\\d*\$"))) {
                        initialAmount = if (input.isEmpty()) 0.0 else input.replace(",", ".").toDouble()
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
                    generating = true
                    val metaInformation = createDTO(firstName, lastName, startDate!!, endDate!!)
                    println("Generated meta information: $metaInformation")
                    generate(
                        inputFilePath, outputFilePath, metaInformation, initialAmount,
                        onError = { exception ->
                            val sw = StringWriter()
                            exception.printStackTrace(PrintWriter(sw))
                            println("Error occurred: $exception")
                            errorState = sw.toString()
                            generating = false
                        },
                        onSuccess = {
                            println("Generated output file successfully")
                            successState = outputFilePath
                            generating = false
                        })
                },
                enabled = (inputFilePath.isNotEmpty() && outputFilePath.isNotEmpty() &&
                        firstName.isNotEmpty() && lastName.isNotEmpty() &&
                        startDate != null && endDate != null) && !generating
            ) {
                Text("Generate Output")
            }

            if (generating) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            successState?.let { filePath ->
                Text(
                    text = "Generated $filePath",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.clickable {
                        try {
                            Desktop.getDesktop().open(File(filePath))
                        } catch (e: Exception) {
                            errorState = e.message
                        }
                    }
                )
            }

            errorState?.let { error ->
                AlertDialog(
                    onDismissRequest = { errorState = null },
                    title = { Text("Error") },
                    text = { Text(error) },
                    confirmButton = {
                        Button(onClick = { errorState = null }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}


