package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeliveredCommand(
    private val shipment: ShipmentBase,
    private val timestamp: Long
) : ShipmentCommand {
    override fun execute() {
        val previousStatus = shipment.status
        shipment.setStatus("delivered")
        shipment.addUpdate("Shipment went from $previousStatus to delivered on ${formatDate(timestamp)}")
    }

} 