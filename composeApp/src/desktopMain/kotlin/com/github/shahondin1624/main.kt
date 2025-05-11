package com.github.shahondin1624

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.shahondin1624.composables.App
import com.github.shahondin1624.viewmodel.TransactionTrackerViewModel
import java.io.File

fun main(args: Array<String>) = application {
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "TransactionTracker",
    ) {
        App(vm = createViewModel(args))
    }
}

private fun createViewModel(args: Array<String>): TransactionTrackerViewModel {
    val debugMode = args.contains("debug")
    if (!debugMode) {
        return TransactionTrackerViewModel()
    }
    return loadViewModelFromFile()
}

private fun loadViewModelFromFile(): TransactionTrackerViewModel {
    val vm = TransactionTrackerViewModel()
    val properties =
        File(System.getProperty("user.dir"), "src/commonMain/composeResources/files/debug-data").readLines()
            .associate { line ->
                val (key, value) = line.split("=")
                key to value
            }

    vm.setInputFilePath(properties["inputFilePath"] ?: "")
    vm.setOutputFilePath(properties["outputFilePath"] ?: "")
    vm.setFirstName(properties["firstName"] ?: "")
    vm.setLastName(properties["lastName"] ?: "")

    val startDate = properties["startDate"]?.let { Constants.dateFormatter.parse(it) }
    val endDate = properties["endDate"]?.let { Constants.dateFormatter.parse(it) }
    vm.setDateRange(startDate, endDate)
    properties["initialAmount"]?.let { vm.setInitialAmount(it.toDouble()) }

    return vm
}