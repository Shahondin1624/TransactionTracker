package com.github.shahondin1624.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

data class TransactionTrackerUiState(
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

class TransactionTrackerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionTrackerUiState())
    val uiState: StateFlow<TransactionTrackerUiState> = _uiState.asStateFlow()

    fun setInputFilePath(path: String) {
        _uiState.value = _uiState.value.copy(inputFilePath = path)
    }

    fun setOutputFilePath(path: String) {
        _uiState.value = _uiState.value.copy(outputFilePath = path)
    }

    fun setFirstName(firstName: String) {
        _uiState.value = _uiState.value.copy(firstName = firstName)
    }

    fun setLastName(lastName: String) {
        _uiState.value = _uiState.value.copy(lastName = lastName)
    }

    fun setStartDate(date: Date) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }

    fun setEndDate(date: Date) {
        _uiState.value = _uiState.value.copy(endDate = date)
    }

    fun setInitialAmount(amount: Double) {
        _uiState.value = _uiState.value.copy(initialAmount = amount)
    }

    fun setGenerating(generating: Boolean) {
        _uiState.value = _uiState.value.copy(generating = generating)
    }

    fun setError(errorState: String?) {
        _uiState.value = _uiState.value.copy(errorState = errorState)
    }

    fun setSuccessState(successState: String?) {
        _uiState.value = _uiState.value.copy(successState = successState)
    }
}
