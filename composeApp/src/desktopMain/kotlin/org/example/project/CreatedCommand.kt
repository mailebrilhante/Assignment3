package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreatedCommand(
    private val shipment: ShipmentBase,
    private val timestamp: Long
) : ShipmentCommand {
    override fun execute() {
        shipment.setStatus("created")
        shipment.addUpdate("Shipment created on ${formatDate(timestamp)}")
    }

} 