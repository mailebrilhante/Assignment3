package org.example.project

import kotlinx.serialization.Serializable

@Serializable
class Shipment(private val _id: String) : Subject {

    val id: String
        get() = _id

    private var _status: String = "created"
    val status: String
        get() = _status

    private var _location: String = "unknown"
    val location: String
        get() = _location

    private var _expectedDelivery: Long? = null
    val expectedDelivery: Long?
        get() = _expectedDelivery

    private val _updates = mutableListOf<String>()
    val updates: List<String>
        get() = _updates.toList()

    private val _notes = mutableListOf<String>()
    val notes: List<String>
        get() = _notes.toList()
    
    private val observers = mutableListOf<Observer>()

    fun setStatus(newStatus: String) {
        _status = newStatus
    }

    fun setLocation(newLocation: String) {
        _location = newLocation
    }

    fun setExpectedDelivery(newExpectedDelivery: Long?) {
        _expectedDelivery = newExpectedDelivery
    }

    override fun registerObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun hasObserver(observer: Observer): Boolean {
        return observers.contains(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.update(this) }
    }

    fun addNote(note: String) {
        _notes.add(note)
        addUpdate("Note added: $note")
    }

    fun addUpdate(update: String) {
        _updates.add(update)
        notifyObservers()
    }

    fun applyUpdate(type: String, timestamp: Long, otherInfo: String?) {
        val previousStatus = this.status
        val previousLocation = this.location

        when (type) {
            "created" -> setStatus("created")
            "shipped" -> {
                setStatus("shipped")
                otherInfo?.toLongOrNull()?.let { setExpectedDelivery(it) }
            }
            "location" -> otherInfo?.let { setLocation(it) }
            "delivered" -> setStatus("delivered")
            "delayed" -> {
                setStatus("delayed")
                otherInfo?.toLongOrNull()?.let { setExpectedDelivery(it) }
            }
            "lost" -> setStatus("lost")
            "canceled" -> setStatus("canceled")
            "noteadded" -> otherInfo?.let { addNote(it) }
        }

        if (previousStatus != this.status) {
            addUpdate("Shipment went from $previousStatus to ${this.status} on ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(timestamp))}")
        }
        if (previousLocation != this.location) {
            addUpdate("Shipment location changed from $previousLocation to ${this.location}")
        }

        notifyObservers()
    }

    fun copy(): Shipment {
        val newShipment = Shipment(id)
        newShipment._status = this._status
        newShipment._location = this._location
        newShipment._expectedDelivery = this._expectedDelivery
        newShipment._updates.addAll(this._updates)
        newShipment._notes.addAll(this._notes)
        return newShipment
    }
}