package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@SerialName("standard")
class StandardShipment(
    override val id: String,
    override val creationTimestamp: Long
) : ShipmentBase() 