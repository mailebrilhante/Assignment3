package org.example.project

import kotlinx.serialization.Serializable

@Serializable
abstract class ShipmentBase : Subject {
    abstract val id: String
    abstract val creationTimestamp: Long

    private var _status: String = "created"
    open val status: String
        get() = _status

    private var _location: String = "unknown"
    open val location: String
        get() = _location

    private var _expectedDelivery: Long? = null
    open val expectedDelivery: Long?
        get() = _expectedDelivery

    private val _updates = mutableListOf<String>()
    open val updates: List<String>
        get() = _updates.toList()

    private val _notes = mutableListOf<String>()
    open val notes: List<String>
        get() = _notes.toList()

    private var _abnormalUpdateMessage: String? = null
    open val abnormalUpdateMessage: String?
        get() = _abnormalUpdateMessage

    @kotlinx.serialization.Transient
    private val observers = mutableListOf<Observer>()

    open fun setStatus(newStatus: String) {
        _status = newStatus
    }

    open fun setLocation(newLocation: String) {
        _location = newLocation
    }

    open fun setExpectedDelivery(newExpectedDelivery: Long?) {
        _expectedDelivery = newExpectedDelivery
    }

    open fun setAbnormalUpdateMessage(newMessage: String?) {
        _abnormalUpdateMessage = newMessage
    }

    open fun addNote(note: String) {
        _notes.add(note)
        addUpdate("Note added: $note")
    }

    open fun addUpdate(update: String) {
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