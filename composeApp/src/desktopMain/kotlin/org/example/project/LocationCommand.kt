package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationCommand(
    private val shipment: IShipment,
    private val timestamp: Long,
    private val newLocation: String?
) : ShipmentCommand {
    override fun execute() {
        newLocation?.let { shipment.setLocation(it) }
        shipment.addUpdate("Shipment arrived at location ${shipment.location} on ${formatDate(timestamp)}")
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
} 