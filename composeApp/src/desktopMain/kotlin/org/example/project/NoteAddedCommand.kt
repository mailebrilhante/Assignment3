package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAddedCommand(
    private val shipment: ShipmentBase,
    private val timestamp: Long,
    private val note: String?
) : ShipmentCommand {
    override fun execute() {
        note?.let {
            shipment.addNote(it)
            shipment.addUpdate("Note added on ${formatDate(timestamp)}: '$it'")
        }
    }

} 