package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import java.util.concurrent.TimeUnit

@Serializable
@SerialName("overnight")
class OvernightShipment(
    override val id: String,
    override val creationTimestamp: Long
) : ShipmentBase() {
    override fun setExpectedDelivery(newExpectedDelivery: Long?) {
        if (newExpectedDelivery != null && status != "delayed") {
            val oneDayInMillis = TimeUnit.DAYS.toMillis(1)
            if (newExpectedDelivery > creationTimestamp + oneDayInMillis) {
                setAbnormalUpdateMessage("An overnight shipment was updated to have a delivery date later than 24 hours after it was created.")
            }
        }
        super.setExpectedDelivery(newExpectedDelivery)
    }
} 