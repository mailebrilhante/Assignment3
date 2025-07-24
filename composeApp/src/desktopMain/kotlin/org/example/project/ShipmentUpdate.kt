
package org.example.project

import kotlinx.serialization.Serializable

@Serializable
data class ShipmentUpdate(
    val type: String,
    val id: String,
    val timestamp: Long,
    val otherInfo: String? = null
) 