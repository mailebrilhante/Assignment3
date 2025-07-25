package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CanceledCommand(
    private val shipment: ShipmentBase,
    private val timestamp: Long
) : ShipmentCommand {
    override fun execute() {
        val previousStatus = shipment.status
        shipment.setStatus("canceled")
        shipment.addUpdate("Shipment went from $previousStatus to canceled on ${formatDate(timestamp)}")
    }

} 