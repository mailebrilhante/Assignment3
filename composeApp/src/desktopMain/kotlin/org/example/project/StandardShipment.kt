package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@SerialName("standard")
class StandardShipment : ShipmentBase() {
    override fun copy(): ShipmentBase {
        return StandardShipment().also {
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