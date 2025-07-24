package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShippedCommand(
    private val shipment: Shipment,
    private val timestamp: Long,
    private val expectedDelivery: Long?
) : ShipmentCommand {
    override fun execute() {
        val previousStatus = shipment.status
        shipment.setStatus("shipped")
        expectedDelivery?.let { shipment.setExpectedDelivery(it) }
        shipment.addUpdate("Shipment went from $previousStatus to shipped on ${formatDate(timestamp)}")
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
} 