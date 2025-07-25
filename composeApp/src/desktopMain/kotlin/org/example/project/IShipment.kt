package org.example.project

interface IShipment : Subject {
    val id: String
    val status: String
    val location: String
    val expectedDelivery: Long?
    val updates: List<String>
    val notes: List<String>
    val abnormalUpdateMessage: String?
    val creationTimestamp: Long

    fun setStatus(newStatus: String)
    fun setLocation(newLocation: String)
    fun setExpectedDelivery(newExpectedDelivery: Long?)
    fun setAbnormalUpdateMessage(newMessage: String?)
    fun addNote(note: String)
    fun addUpdate(update: String)
} 