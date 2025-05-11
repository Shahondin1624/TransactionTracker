package com.github.shahondin1624.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.shahondin1624.Constants
import com.github.shahondin1624.UiConstants.PADDING_SMALL
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_DEFAULT
import com.github.shahondin1624.UiConstants.SPACER_HEIGHT_LARGE
import com.github.shahondin1624.UiConstants.Table.APPLY_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Table.DESCRIPTION_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Table.ICON_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Table.INPUT_FIELD_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Table.NUMBERS_COLUMN_WEIGHT
import com.github.shahondin1624.UiConstants.Text.BIG_TEXT_SIZE
import com.github.shahondin1624.createRegex
import com.github.shahondin1624.getAllUniqueDescriptions
import com.github.shahondin1624.viewmodel.TransactionTrackerViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowAllDescriptions(vm: TransactionTrackerViewModel, itemsAfterList: @Composable () -> Unit = {}) {
    val transactionsState = vm.transactions.collectAsState()
    val descriptions = getAllUniqueDescriptions(transactionsState.value.transactions)

    val descriptionStates = remember { mutableStateMapOf<String, String>() }

    descriptions.forEach { description ->
        if (!descriptionStates.containsKey(description)) {
            descriptionStates[description] = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        createHeader()
                        RegexTransformer(vm)
                    }
                }
            }
            items(descriptions.toList()) { description ->
                val newDescription = descriptionStates[description] ?: ""
                val numbers =
                    transactionsState.value.transactions.filter { it.description == description }.map { it.number }
                val tx =
                    if (numbers.size == 1) transactionsState.value.transactions.find { it.number == numbers[0] } else null

                val onAction: () -> Unit = {
                    if (newDescription.isNotBlank()) {
                        vm.applyTransformation(description, newDescription)
                        descriptionStates.remove(description)
                    }
                }

                layoutRow(
                    column1 = {
                        SelectionContainer {
                            Text(numbers.joinToString(separator = ", "))
                        }
                    },
                    column2 = {
                        TextTooltip(text = tx?.let { "Booked: ${Constants.dateFormatter.format(it.date)}, Amount: ${it.amount.amount}" }) {
                            SelectionContainer {
                                Text(description)
                            }
                        }
                    },
                    column3 = {
                        Box(
                            modifier = Modifier.padding(top = SPACER_HEIGHT_LARGE),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Forward arrow")
                        }
                    },
                    column4 = {
                        TextField(
                            value = newDescription,
                            onValueChange = { descriptionStates[description] = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        SingleLineActionTextInput(
                            value = newDescription,
                            onValueChange = { descriptionStates[description] = it },
                            modifier = Modifier.fillMaxWidth(),
                            onAction = onAction,
                            placeholder = { Text("Enter new description") }
                        )
                    },
                    column5 = {
                        ApplyNewDescriptionButton(onClick = onAction)
                    }
                )
            }
            item {
                itemsAfterList()
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(listState)
        )
    }
}

@Composable
private fun ApplyNewDescriptionButton(
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(Icons.Filled.PlayArrow, "Apply new description")
    }
}

@Composable
private fun RegexTransformer(vm: TransactionTrackerViewModel) {
    var newDescription by remember { mutableStateOf("") }
    var regexString by remember { mutableStateOf("") }
    val regex = createRegex(regexString)
    val state = vm.transactions.collectAsState()
    val matchingResults = state.value.transactions.filter { it.description.matches(regex) }
    val onAction: () -> Unit = {
        if (newDescription.isNotBlank() && regexString.isNotBlank()) {
            vm.applyTransformation(newDescription = newDescription, matcher = { description ->
                description.matches(regex)
            })
            regexString = ""
            newDescription = ""
        }
    }

    layoutRow(
        column1 = {
            Text("Select by Regex")
        },
        column2 = {
            TextTooltip(text = "Using a '*' will match any number of characters") {
                SingleLineActionTextInput(
                    value = regexString,
                    onValueChange = { regexString = it },
                    placeholder = { Text("Regex like '*thing you want to match*'") },
                    modifier = Modifier.fillMaxWidth().padding(end = PADDING_SMALL),
                )
            }
        },
        column3 = {
            Text(
                text = "${matchingResults.size}",
                modifier = Modifier.padding(start = SPACER_HEIGHT_DEFAULT),
                fontSize = BIG_TEXT_SIZE,
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
            )
        },
        column4 = {
            SingleLineActionTextInput(
                value = newDescription,
                onValueChange = { newDescription = it },
                modifier = Modifier.fillMaxWidth(),
                onAction = onAction,
                placeholder = { Text("Enter new description") }
            )
        },
        column5 = {
            ApplyNewDescriptionButton(onClick = onAction)
        }
    )
}

@Composable
private fun layoutRow(
    column1: (@Composable () -> Unit)? = null,
    column2: (@Composable () -> Unit)? = null,
    column3: (@Composable () -> Unit)? = null,
    column4: (@Composable () -> Unit)? = null,
    column5: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(SPACER_HEIGHT_DEFAULT)
    ) {
        Box(modifier = Modifier.weight(NUMBERS_COLUMN_WEIGHT)) {
            column1?.invoke()
        }
        Box(modifier = Modifier.weight(DESCRIPTION_COLUMN_WEIGHT)) {
            column2?.invoke()
        }
        Box(modifier = Modifier.weight(ICON_COLUMN_WEIGHT)) {
            column3?.invoke()
        }
        Box(modifier = Modifier.weight(INPUT_FIELD_COLUMN_WEIGHT)) {
            column4?.invoke()
        }
        Box(modifier = Modifier.weight(APPLY_COLUMN_WEIGHT)) {
            column5?.invoke()
        }
    }
}

@Composable
private fun createHeader() {
    Surface(color = MaterialTheme.colors.background) {
        Column {
            layoutRow(
                column1 = { Text("Transaction Numbers") },
                column2 = { Text("Description") },
                column4 = { Text("New description") })
            Spacer(modifier = Modifier.height(SPACER_HEIGHT_LARGE))
        }
    }
}
