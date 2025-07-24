package org.example.project

object CommandFactory {
    fun create(update: ShipmentUpdate, shipment: Shipment): ShipmentCommand? {
        return when (update.type) {
            "created" -> CreatedCommand(shipment, update.timestamp)
            "shipped" -> ShippedCommand(shipment, update.timestamp, update.otherInfo?.toLongOrNull())
            "location" -> LocationCommand(shipment, update.timestamp, update.otherInfo)
            "delivered" -> DeliveredCommand(shipment, update.timestamp)
            "delayed" -> DelayedCommand(shipment, update.timestamp, update.otherInfo?.toLongOrNull())
            "lost" -> LostCommand(shipment, update.timestamp)
            "canceled" -> CanceledCommand(shipment, update.timestamp)
            "noteadded" -> NoteAddedCommand(shipment, update.timestamp, update.otherInfo)
            else -> null
        }
    }
} 