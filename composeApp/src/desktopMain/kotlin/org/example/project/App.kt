package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val client = Client
    val trackedShipments by client.trackedShipments.collectAsState()
    val errorMessage by client.errorMessage.collectAsState()
    var shipmentId by remember { mutableStateOf("") }
    var updateMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        launch {
            client.startPolling()
        }
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = shipmentId,
                    onValueChange = { shipmentId = it },
                    label = { Text("Shipment ID") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        client.trackShipment(shipmentId)
                    }
                }) {
                    Text("Track")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = updateMessage,
                    onValueChange = { updateMessage = it },
                    label = { Text("Update message") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        client.sendUpdate(updateMessage)
                    }
                }) {
                    Text("Send Update")
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colors.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(trackedShipments) { shipment ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 4.dp) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("ID: ${shipment.id}", style = MaterialTheme.typography.h6)
                                Button(onClick = {
                                    client.stopTrackingShipment(shipment.id)
                                }) {
                                    Text("Stop Tracking")
                                }
                            }
                            shipment.abnormalUpdateMessage?.let {
                                Text(it, color = MaterialTheme.colors.error)
                            }
                            Text("Status: ${shipment.status}")
                            Text("Location: ${shipment.location}")
                            val formattedDate = shipment.expectedDelivery?.let {
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(it))
                            } ?: "N/A"
                            Text("Expected Delivery: $formattedDate")
                            if (shipment.notes.isNotEmpty()) {
                                Text("Notes:")
                                shipment.notes.forEach { note ->
                                    Text("  - $note")
                                }
                            }
                            if (shipment.updates.isNotEmpty()) {
                                Text("Updates:")
                                shipment.updates.forEach { update ->
                                    Text("  - $update")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
} 