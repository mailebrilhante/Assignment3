package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

@Composable
fun App() {
    val client = remember { Client() }
    var shipmentId by remember { mutableStateOf("") }
    var updateString by remember { mutableStateOf("") }
    val trackedShipments by client.trackedShipments.collectAsState()
    val errorMessage by client.errorMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        client.startPolling()
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            launch {
                delay(3000)
                client.clearErrorMessage()
            }
        }
    }

    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                TextField(
                    value = shipmentId,
                    onValueChange = { shipmentId = it },
                    label = { Text("Shipment ID") }
                )
                Button(onClick = {
                    coroutineScope.launch {
                        client.trackShipment(shipmentId)
                        shipmentId = ""
                    }
                }) {
                    Text("Track")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                TextField(
                    value = updateString,
                    onValueChange = { updateString = it },
                    label = { Text("Update message") }
                )
                Button(onClick = {
                    coroutineScope.launch {
                        client.sendUpdate(updateString)
                        updateString = ""
                    }
                }) {
                    Text("Send Update")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            errorMessage?.let {
                Text(it, color = MaterialTheme.colors.error)
            }
            LazyColumn {
                items(trackedShipments) { shipment ->
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ID: ${shipment.id}")
                            Button(onClick = {
                                client.stopTrackingShipment(shipment.id)
                            }) {
                                Text("Stop Tracking")
                            }
                        }
                        Text("Status: ${shipment.status}")
                        Text("Location: ${shipment.location}")
                        val formattedDate = shipment.expectedDelivery?.let {
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(it))
                        } ?: "N/A"
                        Text("Expected Delivery: $formattedDate")
                        Text("Notes:")
                        shipment.notes.forEach { note ->
                            Text("- $note")
                        }
                        Text("Updates:")
                        shipment.updates.forEach { update ->
                            Text("- $update")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
} 