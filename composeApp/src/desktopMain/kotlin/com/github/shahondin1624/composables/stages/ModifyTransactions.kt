package com.github.shahondin1624.composables.stages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.shahondin1624.composables.ShowAllDescriptions
import com.github.shahondin1624.viewmodel.TransactionTrackerViewModel
import kotlinx.coroutines.launch

@Composable
fun ModifyTransactions(vm: TransactionTrackerViewModel) {
    val uiState by vm.uiState.collectAsState()
    val transactionState = vm.transactions.collectAsState()
    val scope = rememberCoroutineScope()

    ShowAllDescriptions(vm) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                enabled = true,
                onClick = {
                    scope.launch {
                        vm.transformTransactions()
                    }
                }) {
                Text("Transform")
            }
        }
    }
}