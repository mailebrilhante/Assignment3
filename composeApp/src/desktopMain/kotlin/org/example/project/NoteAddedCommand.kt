package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAddedCommand(
    private val shipment: IShipment,
    private val timestamp: Long,
    private val note: String?
) : ShipmentCommand {
    override fun execute() {
        note?.let { shipment.addNote(it) }
        shipment.addUpdate("Note added on ${formatDate(timestamp)}: '$note'")
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
} 