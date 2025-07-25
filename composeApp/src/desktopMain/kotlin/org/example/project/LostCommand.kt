package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LostCommand(
    private val shipment: ShipmentBase,
    private val timestamp: Long
) : ShipmentCommand {
    override fun execute() {
        val previousStatus = shipment.status
        shipment.setStatus("lost")
        shipment.addUpdate("Shipment went from $previousStatus to lost on ${formatDate(timestamp)}")
    }
} 