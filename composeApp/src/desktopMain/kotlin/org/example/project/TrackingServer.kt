
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object TrackingServer {
    private val _shipments = mutableMapOf<String, IShipment>()
    val shipments: Map<String, IShipment>
        get() = _shipments.toMap()

    fun start() {
        embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                json(Json {
                    serializersModule = SerializersModule {
                        polymorphic(IShipment::class) {
                            subclass(StandardShipment::class)
                            subclass(ExpressShipment::class)
                            subclass(OvernightShipment::class)
                            subclass(BulkShipment::class)
                        }
                    }
                })
            }
            routing {
                post("/shipment") {
                    val update = call.receive<ShipmentUpdate>()

                    // Handle creation of a new shipment
                    if (update.type == "created") {
                        val newShipment = ShipmentFactory.create(update)
                        _shipments[newShipment.id] = newShipment
                        val command = CommandFactory.create(update, newShipment)
                        command?.execute()
                        call.respond(mapOf("status" to "ok"))
                        return@post
                    }

                    // Handle updates to an existing shipment
                    val existingShipment = _shipments[update.id]
                    if (existingShipment != null) {
                        val command = CommandFactory.create(update, existingShipment)
                        command?.execute()
                        call.respond(mapOf("status" to "ok"))
                    } else {
                        // Optionally handle updates for shipments that don't exist
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Shipment not found"))
                    }
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