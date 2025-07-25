package org.example.project

object ShipmentFactory {
    fun create(update: ShipmentUpdate): ShipmentBase {
        val shipment = when (update.otherInfo) {
            "express" -> ExpressShipment()
            "overnight" -> OvernightShipment()
            "bulk" -> BulkShipment()
            else -> StandardShipment()
        }
        shipment.initialize(update.id, update.timestamp)
        return shipment
    }
} 