package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

fun main() = runBlocking {
    val serverJob = launch(Dispatchers.IO) {
        TrackingServer.start()
    }

    application {
        Window(
            onCloseRequest = {
            serverJob.cancel()
            exitApplication()
        },
            title = "Shipment Tracker",
            alwaysOnTop = true) {
            App()
        }
    }
}