package org.example.project

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object Client {
    internal var client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                serializersModule = SerializersModule {
                    polymorphic(ShipmentBase::class) {
                        subclass(StandardShipment::class)
                        subclass(ExpressShipment::class)
                        subclass(OvernightShipment::class)
                        subclass(BulkShipment::class)
                    }
                }
            })
        }
    }

    private val _trackedShipments = MutableStateFlow<List<ShipmentBase>>(emptyList())
    val trackedShipments = _trackedShipments.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private var lastUpdate: String? = null

    suspend fun trackShipment(id: String) {
        if (id.isBlank()) {
            _errorMessage.value = "Shipment ID cannot be empty"
            return
        }
        if (_trackedShipments.value.any { it.id == id }) {
            _errorMessage.value = "This shipment is already being tracked"
            return
        }

        val response = client.get("http://localhost:8080/shipment/$id")
        if (response.status == HttpStatusCode.OK) {
            val shipment = response.body<ShipmentBase>()
            _trackedShipments.value = _trackedShipments.value + shipment
            _errorMessage.value = null
        } else {
            _errorMessage.value = "Shipment not found"
        }
    }

    suspend fun sendUpdate(updateString: String) {
        if (updateString == lastUpdate) {
            _errorMessage.value = "This update was already sent"
            return
        }

        val parts = updateString.split(",")
        if (parts.size < 3 || parts[1].isBlank()) {
            _errorMessage.value = "Invalid update format or empty shipment ID"
            return
        }

        val shipmentId = parts[1]
        val shipmentToUpdate = _trackedShipments.value.find { it.id == shipmentId }

        if (shipmentToUpdate != null) {
            val updateForCommand = ShipmentUpdate(
                _type = parts[0],
                _id = shipmentId,
                _timestamp = parts[2].toLong(),
                _otherInfo = parts.getOrNull(3)
            )
            val command = CommandFactory.create(updateForCommand, shipmentToUpdate)
            command?.execute()

            _trackedShipments.value = _trackedShipments.value.map {
                if (it.id == shipmentId) shipmentToUpdate.copy() else it
            }
        }

        val update = ShipmentUpdate(
            _type = parts[0],
            _id = parts[1],
            _timestamp = parts[2].toLong(),
            _otherInfo = parts.getOrNull(3)
        )
        val response = client.post("http://localhost:8080/shipment") {
            contentType(ContentType.Application.Json)
            setBody(update)
        }

        if (response.status == HttpStatusCode.OK) {
            lastUpdate = updateString
            _errorMessage.value = "Update sent!"
        } else {
            _errorMessage.value = "Failed to send update"
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun stopTrackingShipment(id: String) {
        _trackedShipments.value = _trackedShipments.value.filter { it.id != id }
    }

    suspend fun startPolling() {
        while (true) {
            if (_trackedShipments.value.isNotEmpty()) {
                val updatedShipments = _trackedShipments.value.map { oldShipment ->
                    val response = client.get("http://localhost:8080/shipment/${oldShipment.id}")
                    if (response.status == HttpStatusCode.OK) {
                        response.body<ShipmentBase>()
                    } else {
                        oldShipment
                    }
                }
                _trackedShipments.value = updatedShipments
            }
            delay(5000)
        }
    }
} 