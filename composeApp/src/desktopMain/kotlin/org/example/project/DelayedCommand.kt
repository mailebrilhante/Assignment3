package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DelayedCommand(
    private val shipment: ShipmentBase,
    private val timestamp: Long,
    private val newExpectedDelivery: Long?
) : ShipmentCommand {
    override fun execute() {
        val previousStatus = shipment.status
        shipment.setStatus("delayed")
        newExpectedDelivery?.let { shipment.setExpectedDelivery(it) }
        shipment.addUpdate("Shipment went from $previousStatus to delayed on ${formatDate(timestamp)}")
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
} 