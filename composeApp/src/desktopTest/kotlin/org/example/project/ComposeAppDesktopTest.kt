package org.example.project

import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.*

class ShipmentTests {

    // Helper class for observer tests
    private class TestObserver : Observer {
        var notified = false
        override fun update(shipment: IShipment) {
            notified = true
        }
    }

    // ShipmentBase Tests
    @Test
    fun observerRegistrationAndRemoval() {
        val shipment = StandardShipment("s300", System.currentTimeMillis())
        val observer = TestObserver()

        shipment.registerObserver(observer)
        shipment.addUpdate("A test update")
        assertTrue(observer.notified)

        shipment.removeObserver(observer)
        observer.notified = false
        shipment.addUpdate("Another test update")
        assertFalse(observer.notified)
    }

    @Test
    fun defensiveCopyForLists() {
        val shipment = StandardShipment("s600", System.currentTimeMillis())
        shipment.addUpdate("Initial update")
        assertFailsWith<UnsupportedOperationException> {
            (shipment.updates as MutableList<String>).add("This should fail")
        }
        assertEquals(1, shipment.updates.size)

        shipment.addNote("Initial note")
        assertFailsWith<UnsupportedOperationException> {
            (shipment.notes as MutableList<String>).add("This should also fail")
        }
        assertEquals(1, shipment.notes.size)
    }

    // Shipment Subclass Tests
    @Test
    fun expressShipmentValidDate() {
        val shipment = ExpressShipment("s500", System.currentTimeMillis())
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.DAYS.toMillis(2))
        assertNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun expressShipmentInvalidDate() {
        val shipment = ExpressShipment("s501", System.currentTimeMillis())
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.DAYS.toMillis(4))
        assertNotNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun expressShipmentDelayedValidation() {
        val shipment = ExpressShipment("s502", System.currentTimeMillis())
        shipment.setStatus("delayed")
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.DAYS.toMillis(4))
        assertNull(shipment.abnormalUpdateMessage)
    }
    
    @Test
    fun expressShipmentNullDate() {
        val shipment = ExpressShipment("s602", System.currentTimeMillis())
        shipment.setExpectedDelivery(null)
        assertNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun overnightShipmentValidDate() {
        val shipment = OvernightShipment("s503", System.currentTimeMillis())
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.HOURS.toMillis(23))
        assertNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun overnightShipmentInvalidDate() {
        val shipment = OvernightShipment("s504", System.currentTimeMillis())
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.DAYS.toMillis(2))
        assertNotNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun overnightShipmentDelayedValidation() {
        val shipment = OvernightShipment("s505", System.currentTimeMillis())
        shipment.setStatus("delayed")
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.DAYS.toMillis(2))
        assertNull(shipment.abnormalUpdateMessage)
    }
    
    @Test
    fun overnightShipmentNullDate() {
        val shipment = OvernightShipment("s603", System.currentTimeMillis())
        shipment.setExpectedDelivery(null)
        assertNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun bulkShipmentValidDate() {
        val shipment = BulkShipment("s506", System.currentTimeMillis())
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.DAYS.toMillis(4))
        assertNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun bulkShipmentInvalidDate() {
        val shipment = BulkShipment("s507", System.currentTimeMillis())
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.DAYS.toMillis(2))
        assertNotNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun bulkShipmentDelayedValidation() {
        val shipment = BulkShipment("s508", System.currentTimeMillis())
        shipment.setStatus("delayed")
        shipment.setExpectedDelivery(shipment.creationTimestamp + TimeUnit.DAYS.toMillis(2))
        assertNull(shipment.abnormalUpdateMessage)
    }

    @Test
    fun standardShipmentInitialState() {
        val shipment = StandardShipment("s1", System.currentTimeMillis())
        assertEquals("created", shipment.status)
        assertTrue(shipment.updates.isEmpty())
        assertTrue(shipment.notes.isEmpty())
        assertNull(shipment.expectedDelivery)
    }

    // Command Tests
    @Test
    fun createdCommand() {
        val shipment = StandardShipment("s1", System.currentTimeMillis())
        CreatedCommand(shipment, 1L).execute()
        assertEquals("created", shipment.status)
        assertTrue(shipment.updates.any { it.contains("Shipment created on") })
    }

    @Test
    fun shippedCommand() {
        val shipment = StandardShipment("s1", System.currentTimeMillis())
        val deliveryTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)
        ShippedCommand(shipment, 1L, deliveryTime).execute()
        assertEquals("shipped", shipment.status)
        assertEquals(deliveryTime, shipment.expectedDelivery)
    }
    
    @Test
    fun shippedCommandNullData() {
        val shipment = StandardShipment("s1", System.currentTimeMillis())
        val initialDelivery = shipment.expectedDelivery
        ShippedCommand(shipment, 2L, null).execute()
        assertEquals(initialDelivery, shipment.expectedDelivery)
    }

    @Test
    fun locationCommand() {
        val shipment = StandardShipment("s202", System.currentTimeMillis())
        val newLocation = "Los Angeles, CA"
        LocationCommand(shipment, 1L, newLocation).execute()
        assertEquals(newLocation, shipment.location)
    }
    
    @Test
    fun locationCommandNullData() {
        val shipment = StandardShipment("s202", System.currentTimeMillis())
        val initialLocation = shipment.location
        LocationCommand(shipment, 2L, null).execute()
        assertEquals(initialLocation, shipment.location)
    }

    @Test
    fun delayedCommand() {
        val shipment = StandardShipment("s203", System.currentTimeMillis())
        val newDeliveryDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5)
        DelayedCommand(shipment, 1L, newDeliveryDate).execute()
        assertEquals("delayed", shipment.status)
        assertEquals(newDeliveryDate, shipment.expectedDelivery)
    }

    @Test
    fun delayedCommandInvalidData() {
        val shipment = StandardShipment("s203", System.currentTimeMillis())
        val initialDeliveryDate = shipment.expectedDelivery
        DelayedCommand(shipment, 2L, null).execute()
        assertEquals(initialDeliveryDate, shipment.expectedDelivery)
        DelayedCommand(shipment, 3L, "invalid".toLongOrNull()).execute()
        assertEquals(initialDeliveryDate, shipment.expectedDelivery)
    }

    @Test
    fun noteAddedCommand() {
        val shipment = StandardShipment("s200", System.currentTimeMillis())
        val note = "This is a test note."
        NoteAddedCommand(shipment, 1L, note).execute()
        assertEquals(1, shipment.notes.size)
        assertEquals(note, shipment.notes.first())
    }

    @Test
    fun noteAddedCommandNullData() {
        val shipment = StandardShipment("s200", System.currentTimeMillis())
        NoteAddedCommand(shipment, 2L, null).execute()
        assertTrue(shipment.updates.last().contains("'null'"))
    }
    
    @Test
    fun deliveredCommand() {
        val shipment = StandardShipment("s1", System.currentTimeMillis())
        DeliveredCommand(shipment, 1L).execute()
        assertEquals("delivered", shipment.status)
    }

    @Test
    fun canceledCommand() {
        val shipment = StandardShipment("s2", System.currentTimeMillis())
        CanceledCommand(shipment, 1L).execute()
        assertEquals("canceled", shipment.status)
    }
    
    @Test
    fun lostCommand() {
        val shipment = StandardShipment("s3", System.currentTimeMillis())
        LostCommand(shipment, 1L).execute()
        assertEquals("lost", shipment.status)
    }

    // Factory Tests
    @Test
    fun commandFactory() {
        val shipment = StandardShipment("s1", System.currentTimeMillis())
        val timestamp = System.currentTimeMillis()
        assertTrue(CommandFactory.create(ShipmentUpdate("created", "s1", timestamp), shipment) is CreatedCommand)
        assertTrue(CommandFactory.create(ShipmentUpdate("shipped", "s1", timestamp), shipment) is ShippedCommand)
        assertTrue(CommandFactory.create(ShipmentUpdate("location", "s1", timestamp), shipment) is LocationCommand)
        assertTrue(CommandFactory.create(ShipmentUpdate("delivered", "s1", timestamp), shipment) is DeliveredCommand)
        assertTrue(CommandFactory.create(ShipmentUpdate("delayed", "s1", timestamp), shipment) is DelayedCommand)
        assertTrue(CommandFactory.create(ShipmentUpdate("lost", "s1", timestamp), shipment) is LostCommand)
        assertTrue(CommandFactory.create(ShipmentUpdate("canceled", "s1", timestamp), shipment) is CanceledCommand)
        assertTrue(CommandFactory.create(ShipmentUpdate("noteadded", "s1", timestamp), shipment) is NoteAddedCommand)
        assertNull(CommandFactory.create(ShipmentUpdate("unknown", "s1", timestamp), shipment))
    }

    @Test
    fun shipmentFactory() {
        val timestamp = System.currentTimeMillis()
        assertTrue(ShipmentFactory.create(ShipmentUpdate("created", "s1", timestamp, "express")) is ExpressShipment)
        assertTrue(ShipmentFactory.create(ShipmentUpdate("created", "s2", timestamp, "overnight")) is OvernightShipment)
        assertTrue(ShipmentFactory.create(ShipmentUpdate("created", "s3", timestamp, "bulk")) is BulkShipment)
        assertTrue(ShipmentFactory.create(ShipmentUpdate("created", "s4", timestamp, "standard")) is StandardShipment)
        assertTrue(ShipmentFactory.create(ShipmentUpdate("created", "s5", timestamp)) is StandardShipment)
        assertTrue(ShipmentFactory.create(ShipmentUpdate("created", "s6", timestamp, "economy")) is StandardShipment)
    }
} 