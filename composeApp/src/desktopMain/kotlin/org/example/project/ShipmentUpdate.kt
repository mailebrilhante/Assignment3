
package org.example.project

import kotlinx.serialization.Serializable

@Serializable
class ShipmentUpdate(
    private val _type: String,
    private val _id: String,
    private val _timestamp: Long,
    private val _otherInfo: String? = null
) {
    val type: String
        get() = _type
    val id: String
        get() = _id
    val timestamp: Long
        get() = _timestamp
    val otherInfo: String?
        get() = _otherInfo
} 