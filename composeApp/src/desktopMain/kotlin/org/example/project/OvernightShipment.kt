package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.util.concurrent.TimeUnit

@Serializable
@SerialName("overnight")
class OvernightShipment : ShipmentBase() {
    override fun setExpectedDelivery(newExpectedDelivery: Long?) {
        if (newExpectedDelivery != null && status != "delayed") {
            val oneDayInMillis = TimeUnit.DAYS.toMillis(1)
            if (newExpectedDelivery > creationTimestamp + oneDayInMillis) {
                setAbnormalUpdateMessage("An overnight shipment was updated to have a delivery date later than 24 hours after it was created.")
            }
        }
        super.setExpectedDelivery(newExpectedDelivery)
    }

    override fun copy(): ShipmentBase {
        return OvernightShipment().also {
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