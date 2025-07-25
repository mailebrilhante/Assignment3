package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreatedCommand(
    private val shipment: IShipment,
    private val timestamp: Long
) : ShipmentCommand {
    override fun execute() {
        shipment.setStatus("created")
        shipment.addUpdate("Shipment created on ${formatDate(timestamp)}")
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
} 