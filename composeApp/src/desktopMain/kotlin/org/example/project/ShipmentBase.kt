package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class ShipmentBase : Subject {
    private var _id: String = ""
    val id: String
        get() = _id

    private var _creationTimestamp: Long = 0
    val creationTimestamp: Long
        get() = _creationTimestamp

    private var _status: String = "created"
    open val status: String
        get() = _status

    private var _location: String = "unknown"
    open val location: String
        get() = _location

    private var _expectedDelivery: Long? = null
    open val expectedDelivery: Long?
        get() = _expectedDelivery

    protected val _updates = mutableListOf<String>()
    val updates: List<String>
        get() = _updates.toList()

    protected val _notes = mutableListOf<String>()
    val notes: List<String>
        get() = _notes.toList()

    private var _abnormalUpdateMessage: String? = null
    open val abnormalUpdateMessage: String?
        get() = _abnormalUpdateMessage

    @Transient
    private val observers = mutableListOf<Observer>()

    fun initialize(id: String, creationTimestamp: Long) {
        this._id = id
        this._creationTimestamp = creationTimestamp
    }

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

    abstract fun copy(): ShipmentBase
} 