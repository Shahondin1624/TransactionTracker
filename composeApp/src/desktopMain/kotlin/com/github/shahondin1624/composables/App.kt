package com.github.shahondin1624.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.shahondin1624.composables.stages.ExportTransactions
import com.github.shahondin1624.composables.stages.ImportCSV
import com.github.shahondin1624.composables.stages.ModifyTransactions
import com.github.shahondin1624.viewmodel.Stage
import com.github.shahondin1624.viewmodel.TransactionTrackerViewModel

@Composable
@Preview
fun App(vm: TransactionTrackerViewModel = TransactionTrackerViewModel()) {
    MaterialTheme {
        val uiState by vm.uiState.collectAsState()

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState.currentStage) {
                Stage.ImportCSV -> ImportCSV(vm)
                Stage.TransformCSV -> ModifyTransactions(vm)
                Stage.ExportCSV -> ExportTransactions(vm)
            }
        }
    }
}


