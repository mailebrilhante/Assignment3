package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShippedCommand(
    private val shipment: ShipmentBase,
    private val timestamp: Long,
    private val expectedDeliveryTimestamp: Long?
) : ShipmentCommand {
    override fun execute() {
        val initialStatus = shipment.status
        if (initialStatus == "shipped" || initialStatus == "delivered" || initialStatus == "lost" || initialStatus == "canceled") {
            return
        }

        if (expectedDeliveryTimestamp != null) {
            shipment.setExpectedDelivery(expectedDeliveryTimestamp)
            shipment.setStatus("shipped")
            val updateDetails = "Shipment went from $initialStatus to shipped on ${
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
            }"
            shipment.addUpdate(updateDetails)
        }
    }
} 