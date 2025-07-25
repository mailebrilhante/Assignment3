package org.example.project

object ShipmentFactory {
    fun create(update: ShipmentUpdate): IShipment {
        val shipment = when (update.otherInfo) {
            "express" -> ExpressShipment(update.id, update.timestamp)
            "overnight" -> OvernightShipment(update.id, update.timestamp)
            "bulk" -> BulkShipment(update.id, update.timestamp)
            else -> StandardShipment(update.id, update.timestamp)
        }
        return shipment
    }
} 