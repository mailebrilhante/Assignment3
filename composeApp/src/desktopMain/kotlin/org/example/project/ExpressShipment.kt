package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import java.util.concurrent.TimeUnit

@Serializable
@SerialName("express")
class ExpressShipment(
    override val id: String,
    override val creationTimestamp: Long
) : ShipmentBase() {
    override fun setExpectedDelivery(newExpectedDelivery: Long?) {
        if (newExpectedDelivery != null && status != "delayed") {
            val threeDaysInMillis = TimeUnit.DAYS.toMillis(3)
            if (newExpectedDelivery > creationTimestamp + threeDaysInMillis) {
                setAbnormalUpdateMessage("An express shipment was updated to have an expected delivery date more than 3 days after it was created.")
            }
        }
        super.setExpectedDelivery(newExpectedDelivery)
    }
} 