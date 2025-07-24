
package org.example.project

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class TrackingServer {
    private val _shipments = mutableMapOf<String, Shipment>()
    val shipments: Map<String, Shipment>
        get() = _shipments.toMap()

    fun start() {
        embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                json()
            }
            routing {
                post("/shipment") {
                    val update = call.receive<ShipmentUpdate>()
                    val shipment = _shipments.getOrPut(update.id) { Shipment(update.id) }
                    shipment.applyUpdate(update.type, update.timestamp, update.otherInfo)
                    call.respond(mapOf("status" to "ok"))
                }
                get("/") {
                    call.respondText("Tracking Server is running!")
                }
                get("/shipment/{id}") {
                    val id = call.parameters["id"]
                    val shipment = _shipments[id]
                    if (shipment != null) {
                        call.respond(shipment)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }.start(wait = true)
    }
} 