package org.example.project

import kotlinx.serialization.Serializable

@Serializable
abstract class ShipmentBase : IShipment {
    abstract override val id: String
    abstract override val creationTimestamp: Long

    private var _status: String = "created"
    override val status: String
        get() = _status

    private var _location: String = "unknown"
    override val location: String
        get() = _location

    private var _expectedDelivery: Long? = null
    override val expectedDelivery: Long?
        get() = _expectedDelivery

    private val _updates = mutableListOf<String>()
    override val updates: List<String>
        get() = _updates.toList()

    private val _notes = mutableListOf<String>()
    override val notes: List<String>
        get() = _notes.toList()

    private var _abnormalUpdateMessage: String? = null
    override val abnormalUpdateMessage: String?
        get() = _abnormalUpdateMessage

    @kotlinx.serialization.Transient
    private val observers = mutableListOf<Observer>()

    override fun setStatus(newStatus: String) {
        _status = newStatus
    }

    override fun setLocation(newLocation: String) {
        _location = newLocation
    }

    override fun setExpectedDelivery(newExpectedDelivery: Long?) {
        _expectedDelivery = newExpectedDelivery
    }

    override fun setAbnormalUpdateMessage(newMessage: String?) {
        _abnormalUpdateMessage = newMessage
    }

    override fun addNote(note: String) {
        _notes.add(note)
        addUpdate("Note added: $note")
    }

    override fun addUpdate(update: String) {
        _updates.add(update)
        notifyObservers()
    }

    override fun registerObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.update(this) }
    }
} 