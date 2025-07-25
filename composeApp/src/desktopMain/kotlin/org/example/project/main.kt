package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

fun main() = runBlocking {
    // Start the server in a background thread
    val serverJob = launch(Dispatchers.IO) {
        TrackingServer.start()
    }

    // Start the client UI on the main thread
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