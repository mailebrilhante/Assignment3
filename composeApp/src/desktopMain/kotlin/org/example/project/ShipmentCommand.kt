package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface ShipmentCommand {
    fun execute()

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
} 