package com.github.shahondin1624.viewmodel

import androidx.lifecycle.ViewModel
import com.github.shahondin1624.Constants
import com.github.shahondin1624.createDTO
import com.github.shahondin1624.createTransactionFilter
import com.github.shahondin1624.formatIban
import com.github.shahondin1624.input.parseCSV
import com.github.shahondin1624.model.Transaction
import com.github.shahondin1624.model.YearlyTransactions
import com.github.shahondin1624.output.exportToExcelFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

data class TransactionTrackerUiState(
    val currentStage: Stage = Stage.ImportCSV,
    val inputFilePath: String = "",
    val outputFilePath: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val initialAmount: Double = 0.0,
    val generating: Boolean = false,
    val errorState: String? = null,
    val successState: String? = null,
)

data class TransactionsUiState(
    val firstName: String = "",
    val lastName: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val initialAmount: Double = 0.0,
    val transactions: List<Transaction> = listOf(),
    val iban: String = "",
)

class TransactionTrackerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionTrackerUiState())
    val uiState: StateFlow<TransactionTrackerUiState> = _uiState.asStateFlow()

    private val _transactions = MutableStateFlow(TransactionsUiState())
    val transactions: StateFlow<TransactionsUiState> = _transactions.asStateFlow()

    private fun setGenerating(generating: Boolean) {
        _uiState.update {
            it.copy(generating = generating)
        }
    }

    fun setInputFilePath(path: String) {
        _uiState.update {
            it.copy(inputFilePath = path)
        }
    }

    fun setOutputFilePath(path: String) {
        _uiState.update {
            it.copy(outputFilePath = path)
        }
    }

    fun setFirstName(name: String) {
        _uiState.update {
            it.copy(firstName = name)
        }
    }

    fun setLastName(name: String) {
        _uiState.update {
            it.copy(lastName = name)
        }
    }

    fun setDateRange(start: Date?, end: Date?) {
        _uiState.update {
            it.copy(startDate = start, endDate = end)
        }
    }

    fun setInitialAmount(amount: Double) {
        _uiState.update {
            it.copy(initialAmount = amount)
        }
    }

    fun setError(errorState: String?) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun setSuccessState(successState: String?) {
        _uiState.update {
            it.copy(successState = successState)
        }
    }

    fun importCsv() {
        println("Starting import...")
        setGenerating(true)
        val filter = createTransactionFilter(startDate = uiState.value.startDate!!, endDate = uiState.value.endDate!!)
        println(
            "Created filter for date-range: ${uiState.value.startDate!!} - ${uiState.value.endDate!!}"
        )
        val transactions = parseCSV(
            uiState.value.inputFilePath,
            filter
        )
        _transactions.update {
            it.copy(transactions = transactions, iban = formatIban(Constants.outgoingIBAN))
        }
        setGenerating(false)
        println("Finished import")
        _uiState.update { it.copy(currentStage = Stage.TransformCSV) }
    }

    fun transformTransactions() {
        setGenerating(true)

        _transactions.update {
            it.copy(
                transactions = it.transactions.sortedWith(
                    compareBy<Transaction> { it.date }
                        .thenByDescending { it.number }
                ))
        }

        setGenerating(false)
        _uiState.update {
            it.copy(currentStage = Stage.ExportCSV)
        }
    }

    fun exportTransactions() {
        println("Starting export...")
        setGenerating(true)

        try {
            val metaInformation = createDTO(
                firstName = uiState.value.firstName,
                lastName = uiState.value.lastName,
                startDate = uiState.value.startDate!!,
                endDate = uiState.value.endDate!!,
            )
            println("Generated meta information: $metaInformation")

            val yearlyTransactions =
                YearlyTransactions(metaInformation, uiState.value.initialAmount, transactions.value.transactions)

            exportToExcelFile(yearlyTransactions, uiState.value.outputFilePath)

            println("Generated output file successfully")
            this.setSuccessState(uiState.value.outputFilePath)
        } catch (e: Exception) {
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            println("Error occurred: $e")
            this.setError(sw.toString())
        }

        setGenerating(false)
    }
}

enum class Stage {
    ImportCSV, TransformCSV, ExportCSV
}

fun dateRangeValid(startDate: Date, endDate: Date): Boolean = startDate.before(endDate)
