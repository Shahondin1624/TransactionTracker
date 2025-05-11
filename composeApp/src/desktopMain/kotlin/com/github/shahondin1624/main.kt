package com.github.shahondin1624

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.shahondin1624.composables.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TransactionTracker",
    ) {
        App()
    }
}