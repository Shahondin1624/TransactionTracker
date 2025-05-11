package com.github.shahondin1624.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter

@Composable
fun FilePicker(
    fileDialogTitle: String,
    fileDialogType: Int,
    filters: List<String> = listOf(),
    labelText: String,
    initialValue: String? = null,
    onPathSelected: (String) -> Unit = {}
) {
    var inputFilePath by remember { mutableStateOf(initialValue ?: "") }
    Button(onClick = {
        val fileDialog = FileDialog(Frame(), fileDialogTitle, fileDialogType)
        when (getOS()) {
            OS.WINDOWS -> fileDialog.file = filters.joinToString(";")
            OS.LINUX -> fileDialog.filenameFilter = createFileNameExtensionFilter(filters)
        }
        fileDialog.isVisible = true
        val selectedFile = fileDialog.file
        if (selectedFile != null) {
            inputFilePath = fileDialog.directory + File.separator + selectedFile
            onPathSelected(inputFilePath)
        }
    }) {
        Text(fileDialogTitle)
    }
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = inputFilePath,
        onValueChange = {},
        readOnly = true,
        label = { Text(labelText) },
        modifier = Modifier.fillMaxWidth()
    )
}

private fun createFileNameExtensionFilter(fileExtensions: List<String>): FilenameFilter {
    val extensions = fileExtensions.map { it.removePrefix("*") }
    return FilenameFilter { dir, name ->
        extensions.any {
            name.contains(it, ignoreCase = true) && File(dir, name).isFile
        }
    }
}

private fun getOS(): OS {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("win") -> OS.WINDOWS
        osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> OS.LINUX
        else -> throw IllegalStateException("Unsupported OS: $osName")
    }
}

private enum class OS {
    WINDOWS, LINUX
}

