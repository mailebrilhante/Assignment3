package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.util.concurrent.TimeUnit

@Serializable
@SerialName("bulk")
class BulkShipment : ShipmentBase() {
    override fun setExpectedDelivery(newExpectedDelivery: Long?) {
        if (newExpectedDelivery != null && status != "delayed") {
            val threeDaysInMillis = TimeUnit.DAYS.toMillis(3)
            if (newExpectedDelivery < creationTimestamp + threeDaysInMillis) {
                setAbnormalUpdateMessage("A bulk shipment was updated to have an expected delivery date sooner than 3 days after it was created.")
            }
        }
        super.setExpectedDelivery(newExpectedDelivery)
    }

    override fun copy(): ShipmentBase {
        return BulkShipment().also {
            it.initialize(this.id, this.creationTimestamp)
            it.setStatus(this.status)
            it.setLocation(this.location)
            it.setExpectedDelivery(this.expectedDelivery)
            it._updates.addAll(this._updates)
            it._notes.addAll(this._notes)
            it.setAbnormalUpdateMessage(this.abnormalUpdateMessage)
        }
    }
} 