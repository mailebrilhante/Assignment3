import org.example.project.Shipment

class ShippedUpdate : UpdateStrategy {
    override fun applyUpdate(shipment: Shipment, timestamp: Long, otherInfo: String?) {
        val previousStatus = shipment.status
        shipment.setStatus("shipped")
        otherInfo?.toLongOrNull()?.let {
            shipment.setExpectedDelivery(it)
        }
        val updateMessage = "Shipment went from $previousStatus to ${shipment.status} on ${formatDate(timestamp)}"
        shipment.addUpdate(updateMessage)
    }
}