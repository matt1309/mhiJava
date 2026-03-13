package com.thunderstorm.mhi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for multi-unit support in the MQTT/AirCon bridge.
 *
 * The application stores all AC units in a ConcurrentHashMap keyed by
 * AirConID.  These tests verify that:
 *  - State for each unit is fully isolated.
 *  - Multiple units can be added and retrieved correctly.
 *  - Concurrent access from multiple threads doesn't corrupt state.
 *  - Each unit produces its own distinct protocol encoding.
 *  - Updating one unit does not affect any other unit.
 */
public class AirConMultiUnitTest {

    // -----------------------------------------------------------------------
    // Helper: create a configured AirCon unit
    // -----------------------------------------------------------------------

    private AirCon createUnit(String id, String hostname, float presetTemp,
                               int operationMode, boolean operation) {
        AirCon ac = new AirCon();
        ac.setAirConID(id);
        ac.setDeviceID(id);
        ac.sethostname(hostname);
        ac.setport("51443");
        ac.setOperatorID("testOperator");
        ac.setOperation(operation);
        ac.setOperationMode(operationMode);
        ac.setPresetTemp(presetTemp);
        ac.setAirFlow(0);
        ac.setWindDirectionUD(0);
        ac.setWindDirectionLR(0);
        return ac;
    }

    // -----------------------------------------------------------------------
    // State isolation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Multi-unit: two units have independent state")
    void testTwoUnits_stateIsIndependent() {
        ConcurrentHashMap<String, AirCon> units = new ConcurrentHashMap<>();

        AirCon unit1 = createUnit("aabbcc001122", "192.168.1.10", 22.0f, 1, true);
        AirCon unit2 = createUnit("ddeeff334455", "192.168.1.11", 26.0f, 3, false);

        units.put(unit1.getAirConID(), unit1);
        units.put(unit2.getAirConID(), unit2);

        assertEquals(22.0f, units.get("aabbcc001122").getPresetTemp(), 0.01f,
                "Unit1 preset temp should be 22°C");
        assertEquals(26.0f, units.get("ddeeff334455").getPresetTemp(), 0.01f,
                "Unit2 preset temp should be 26°C");

        assertTrue(units.get("aabbcc001122").getOperation(),
                "Unit1 should be ON");
        assertFalse(units.get("ddeeff334455").getOperation(),
                "Unit2 should be OFF");

        assertEquals(1, units.get("aabbcc001122").getOperationMode(),
                "Unit1 mode should be Cool (1)");
        assertEquals(3, units.get("ddeeff334455").getOperationMode(),
                "Unit2 mode should be Heat (3)");
    }

    @Test
    @DisplayName("Multi-unit: updating one unit does not change any other unit")
    void testTwoUnits_updateOneDoesNotAffectOther() {
        ConcurrentHashMap<String, AirCon> units = new ConcurrentHashMap<>();

        AirCon unit1 = createUnit("unit001", "10.0.0.1", 22.0f, 1, true);
        AirCon unit2 = createUnit("unit002", "10.0.0.2", 25.0f, 2, true);

        units.put(unit1.getAirConID(), unit1);
        units.put(unit2.getAirConID(), unit2);

        // Modify unit1
        units.get("unit001").setPresetTemp(18.0f);
        units.get("unit001").setOperation(false);
        units.get("unit001").setOperationMode(2);

        // Verify unit1 changed
        assertEquals(18.0f, units.get("unit001").getPresetTemp(), 0.01f,
                "Unit1 preset temp should now be 18°C");
        assertFalse(units.get("unit001").getOperation(), "Unit1 should now be OFF");
        assertEquals(2, units.get("unit001").getOperationMode(), "Unit1 mode should now be Fan (2)");

        // Verify unit2 is unchanged
        assertEquals(25.0f, units.get("unit002").getPresetTemp(), 0.01f,
                "Unit2 preset temp should still be 25°C — unaffected by unit1 change");
        assertTrue(units.get("unit002").getOperation(),
                "Unit2 should still be ON — unaffected by unit1 change");
        assertEquals(2, units.get("unit002").getOperationMode(),
                "Unit2 mode should still be Fan (2) — unaffected by unit1 change");
    }

    @Test
    @DisplayName("Multi-unit: five units all have independent, correct state")
    void testFiveUnits_allIndependent() {
        ConcurrentHashMap<String, AirCon> units = new ConcurrentHashMap<>();

        float[] temps = {16.0f, 18.0f, 20.0f, 24.0f, 28.0f};
        int[] modes = {0, 1, 2, 3, 4};
        String[] ids = {"unit-A", "unit-B", "unit-C", "unit-D", "unit-E"};

        for (int i = 0; i < ids.length; i++) {
            AirCon ac = createUnit(ids[i], "10.0.0." + (i + 1), temps[i], modes[i], i % 2 == 0);
            units.put(ids[i], ac);
        }

        assertEquals(5, units.size(), "All 5 units should be stored");

        for (int i = 0; i < ids.length; i++) {
            AirCon ac = units.get(ids[i]);
            assertNotNull(ac, "Unit " + ids[i] + " should be present in map");
            assertEquals(temps[i], ac.getPresetTemp(), 0.01f,
                    "Unit " + ids[i] + " preset temp should be " + temps[i]);
            assertEquals(modes[i], ac.getOperationMode(),
                    "Unit " + ids[i] + " mode should be " + modes[i]);
        }
    }

    // -----------------------------------------------------------------------
    // ConcurrentHashMap keying
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Multi-unit: units are keyed by AirConID, retrieved correctly")
    void testConcurrentHashMap_keyedByAirConID() {
        ConcurrentHashMap<String, AirCon> units = new ConcurrentHashMap<>();

        AirCon ac1 = createUnit("id-alpha", "192.168.0.1", 22.0f, 1, true);
        AirCon ac2 = createUnit("id-beta",  "192.168.0.2", 24.0f, 1, true);

        units.put(ac1.getAirConID(), ac1);
        units.put(ac2.getAirConID(), ac2);

        // Retrieve by ID, not by object reference
        assertSame(ac1, units.get("id-alpha"), "Should retrieve same instance for id-alpha");
        assertSame(ac2, units.get("id-beta"),  "Should retrieve same instance for id-beta");
        assertNull(units.get("id-nonexistent"), "Non-existent ID should return null");
    }

    @Test
    @DisplayName("Multi-unit: replacing a unit ID updates the map without affecting other entries")
    void testConcurrentHashMap_replaceUnit() {
        ConcurrentHashMap<String, AirCon> units = new ConcurrentHashMap<>();

        AirCon original = createUnit("same-id", "192.168.0.1", 20.0f, 1, true);
        AirCon replacement = createUnit("same-id", "192.168.0.99", 26.0f, 3, false);
        AirCon other = createUnit("other-id", "192.168.0.2", 18.0f, 2, true);

        units.put(original.getAirConID(), original);
        units.put(other.getAirConID(), other);
        units.put(replacement.getAirConID(), replacement); // replaces "same-id"

        assertEquals(2, units.size(), "Map should still have 2 entries after replacement");
        assertSame(replacement, units.get("same-id"),
                "Replaced unit should now be the replacement");
        assertEquals(26.0f, units.get("same-id").getPresetTemp(), 0.01f,
                "Replaced unit should have new preset temp");

        // other unit is unaffected
        assertEquals(18.0f, units.get("other-id").getPresetTemp(), 0.01f,
                "Other unit should be unaffected by replacement");
    }

    // -----------------------------------------------------------------------
    // Independent protocol encodings
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Multi-unit: units with different settings produce different base64 encodings")
    void testMultiUnit_differentSettingsProduceDifferentEncodings() {
        AirCon unit1 = createUnit("enc-1", "10.0.0.1", 22.0f, 1, true);
        AirCon unit2 = createUnit("enc-2", "10.0.0.2", 26.0f, 3, false);

        String enc1 = unit1.parser.toBase64();
        String enc2 = unit2.parser.toBase64();

        assertNotNull(enc1, "Unit1 encoding should not be null");
        assertNotNull(enc2, "Unit2 encoding should not be null");
        assertNotEquals(enc1, enc2,
                "Units with different settings must produce different encodings");
    }

    @Test
    @DisplayName("Multi-unit: units with identical settings produce identical encodings")
    void testMultiUnit_identicalSettingsProduceIdenticalEncodings() {
        AirCon unit1 = createUnit("id-x", "10.0.0.1", 22.0f, 1, true);
        AirCon unit2 = createUnit("id-y", "10.0.0.2", 22.0f, 1, true);
        // Same operational settings; hostname and ID don't affect encoding

        String enc1 = unit1.parser.toBase64();
        String enc2 = unit2.parser.toBase64();

        assertEquals(enc1, enc2,
                "Units with identical operational settings should produce identical encodings");
    }

    // -----------------------------------------------------------------------
    // Getter/setter thread safety
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Multi-unit: concurrent reads from multiple units are safe")
    void testConcurrentReads_noDataCorruption() throws InterruptedException {
        ConcurrentHashMap<String, AirCon> units = new ConcurrentHashMap<>();
        int numUnits = 5;
        int numThreads = 10;
        int iterationsPerThread = 1000;

        for (int i = 0; i < numUnits; i++) {
            AirCon ac = createUnit("unit-" + i, "10.0.0." + i,
                    20.0f + i, 1, true);
            units.put("unit-" + i, ac);
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        List<Throwable> errors = new ArrayList<>();

        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < iterationsPerThread; i++) {
                        String unitKey = "unit-" + (i % numUnits);
                        AirCon ac = units.get(unitKey);
                        assertNotNull(ac, "Unit should not be null");
                        // Read operations should be consistent
                        float temp = ac.getPresetTemp();
                        assertTrue(temp >= 20.0f && temp < 25.0f,
                                "Temp should remain in expected range, got: " + temp);
                    }
                } catch (Throwable e) {
                    synchronized (errors) {
                        errors.add(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "Threads did not complete in time");
        executor.shutdown();

        assertTrue(errors.isEmpty(),
                "Concurrent reads produced errors: " + errors);
    }

    @Test
    @DisplayName("Multi-unit: concurrent writes to different units do not corrupt each other")
    void testConcurrentWrites_noDataCorruption() throws InterruptedException {
        ConcurrentHashMap<String, AirCon> units = new ConcurrentHashMap<>();

        AirCon unit1 = createUnit("cw-unit1", "10.0.0.1", 20.0f, 1, true);
        AirCon unit2 = createUnit("cw-unit2", "10.0.0.2", 24.0f, 2, true);
        units.put("cw-unit1", unit1);
        units.put("cw-unit2", unit2);

        int numIterations = 500;
        CountDownLatch latch = new CountDownLatch(2);
        List<Throwable> errors = new ArrayList<>();

        // Thread 1: repeatedly writes to unit1
        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < numIterations; i++) {
                    units.get("cw-unit1").setPresetTemp(20.0f + (i % 10));
                    units.get("cw-unit1").setOperationMode(i % 5);
                }
            } catch (Throwable e) {
                synchronized (errors) { errors.add(e); }
            } finally {
                latch.countDown();
            }
        });

        // Thread 2: repeatedly writes to unit2
        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < numIterations; i++) {
                    units.get("cw-unit2").setPresetTemp(24.0f + (i % 6));
                    units.get("cw-unit2").setOperation(i % 2 == 0);
                }
            } catch (Throwable e) {
                synchronized (errors) { errors.add(e); }
            } finally {
                latch.countDown();
            }
        });

        t1.start();
        t2.start();
        assertTrue(latch.await(30, TimeUnit.SECONDS), "Threads did not complete in time");

        assertTrue(errors.isEmpty(),
                "Concurrent writes produced errors: " + errors);

        // The units themselves must still be retrievable
        assertNotNull(units.get("cw-unit1"), "Unit1 should still be accessible");
        assertNotNull(units.get("cw-unit2"), "Unit2 should still be accessible");
    }

    // -----------------------------------------------------------------------
    // AirCon property setters return-value correctness
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Setter: returns true on first set (change from default)")
    void testSetter_returnsTrueOnChange() {
        AirCon ac = new AirCon();
        boolean changed = ac.setPresetTemp(22.0f);
        // Default PresetTemp is 0.0f, so 22.0f is a change
        assertTrue(changed, "setPresetTemp should return true when value changes");
    }

    @Test
    @DisplayName("Setter: returns false when setting same value twice")
    void testSetter_returnsFalseWhenSameValue() {
        AirCon ac = new AirCon();
        ac.setPresetTemp(22.0f);
        boolean changed = ac.setPresetTemp(22.0f); // same value again
        assertFalse(changed, "setPresetTemp should return false when value is unchanged");
    }

    @Test
    @DisplayName("Setter: hostname change detection works correctly")
    void testSetter_hostnameChangeDetection() {
        AirCon ac = new AirCon();
        boolean first = ac.sethostname("192.168.1.100");
        assertTrue(first, "First hostname set should return true");

        boolean second = ac.sethostname("192.168.1.100");
        assertFalse(second, "Same hostname set again should return false");

        boolean third = ac.sethostname("192.168.1.200");
        assertTrue(third, "Different hostname should return true");
    }

    // -----------------------------------------------------------------------
    // AirConID isolation between units
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Multi-unit: AirConIDs are independent per unit instance")
    void testAirConID_independentPerUnit() {
        AirCon unit1 = new AirCon();
        AirCon unit2 = new AirCon();

        unit1.setAirConID("id-111");
        unit2.setAirConID("id-222");

        assertEquals("id-111", unit1.getAirConID(), "Unit1 should have its own ID");
        assertEquals("id-222", unit2.getAirConID(), "Unit2 should have its own ID");
        assertNotEquals(unit1.getAirConID(), unit2.getAirConID(),
                "Units must have different IDs");
    }

    @Test
    @DisplayName("Multi-unit: getNextRequestAfter is initially null (no NPE)")
    void testNextRequestAfter_initiallyNullNoNPE() {
        AirCon ac = new AirCon();
        // Before the fix, setNextRequestAfter called .equals() on null field, causing NPE
        assertDoesNotThrow(() -> {
            ac.setNextRequestAfter(java.time.LocalDateTime.now());
        }, "setNextRequestAfter must not throw NPE when nextRequestAfter is null initially");
    }

    @Test
    @DisplayName("Multi-unit: setNextRequestAfter works correctly across multiple units")
    void testNextRequestAfter_multiUnit() {
        AirCon unit1 = new AirCon();
        AirCon unit2 = new AirCon();

        java.time.LocalDateTime time1 = java.time.LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        java.time.LocalDateTime time2 = java.time.LocalDateTime.of(2024, 1, 1, 11, 0, 0);

        unit1.setNextRequestAfter(time1);
        unit2.setNextRequestAfter(time2);

        assertEquals(time1, unit1.getNextRequestAfter(),
                "Unit1 nextRequestAfter should be time1");
        assertEquals(time2, unit2.getNextRequestAfter(),
                "Unit2 nextRequestAfter should be time2");
    }

    // -----------------------------------------------------------------------
    // jsonParser.containsKey fix
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Multi-unit: containsKey correctly identifies existing units")
    void testConcurrentHashMap_containsKey_vs_contains() {
        ConcurrentHashMap<String, AirCon> units = new ConcurrentHashMap<>();
        AirCon ac = createUnit("known-id", "10.0.0.1", 22.0f, 1, true);
        units.put("known-id", ac);

        // containsKey checks keys (correct) — this is what jsonParser now uses
        assertTrue(units.containsKey("known-id"),
                "containsKey should find unit by its AirConID key");
        assertFalse(units.containsKey("unknown-id"),
                "containsKey should return false for unknown IDs");

        // Verify contains() would NOT work for key lookup (it checks values, not keys)
        // This demonstrates why the bug existed
        assertFalse(units.contains("known-id"),
                "contains() checks values (not keys) — would miss the unit, demonstrating the bug");
    }
}
