package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationCommand(
    private val shipment: ShipmentBase,
    private val timestamp: Long,
    private val newLocation: String?
) : ShipmentCommand {
    override fun execute() {
        newLocation?.let { shipment.setLocation(it) }
        shipment.addUpdate("Shipment arrived at location ${shipment.location} on ${formatDate(timestamp)}")
    }

} 