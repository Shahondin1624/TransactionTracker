package com.github.shahondin1624.composables.stages

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import com.github.shahondin1624.composables.DateRangePicker
import com.github.shahondin1624.composables.FilePicker
import com.github.shahondin1624.viewmodel.TransactionTrackerViewModel
import com.github.shahondin1624.viewmodel.dateRangeValid
import kotlinx.coroutines.launch
import java.awt.FileDialog

@Composable
fun ImportCSV(vm: TransactionTrackerViewModel) {
    val uiState by vm.uiState.collectAsState()
    val startDateFocus = remember { FocusRequester() }
    val endDateFocus = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    FilePicker(
        fileDialogTitle = "Select Input CSV File",
        fileDialogType = FileDialog.LOAD,
        filters = listOf("*.csv"),
        labelText = "Input File Path",
        initialValue = uiState.inputFilePath,
        onPathSelected = { vm.setInputFilePath(it) }
    )

    DateRangePicker(
        startDateFocusRequester = startDateFocus,
        endDateFocusRequester = endDateFocus,
        initialStartDate = uiState.startDate,
        initialEndDate = uiState.endDate
    ) { (start, end) ->
        vm.setDateRange(start, end)
    }

    Button(
        onClick = {
            scope.launch {
                vm.importCsv()
            }
        },
        enabled = uiState.inputFilePath.isNotEmpty()
                && uiState.startDate != null
                && uiState.endDate != null
                && dateRangeValid(uiState.startDate!!, uiState.endDate!!)
    ) {
        Text("Import")
    }
}