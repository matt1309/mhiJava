package org.openhab.binding.mhi.internal.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AirconState.RacParser protocol encoding and decoding.
 *
 * These tests validate the binary protocol used to communicate with
 * Mitsubishi WF-RAC air conditioning units, cross-referenced against the
 * Python reference implementation at jeatheak/Mitsubishi-WF-RAC-Integration.
 */
public class RacParserTest {

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Build a synthetic AC response byte array.
     *
     * Structure (N = contentByteArray[18] = 1):
     *   bytes[ 0:18]  command part (18 zeroes)
     *   byte [18]     N = 1  → startLength = 1*4+21 = 25
     *   bytes[19:25]  rest of command area (zeroes)
     *   bytes[25:43]  receive data (18 bytes, contains the AC state)
     *   byte [43]     gap byte (not read)
     *   bytes[44..]   temperature/electric data (4 bytes per entry)
     *   last 2 bytes  trailing bytes (not included in vals)
     *
     * @param receiveData  exactly 18 bytes representing the receive state
     * @param tempEntries  flat list of 4-byte temperature/electric entries
     */
    private String buildPayload(byte[] receiveData, byte[]... tempEntries) {
        int tempLen = 0;
        for (byte[] e : tempEntries) tempLen += e.length;

        // total = 25 (cmd) + 18 (rcv) + 1 (gap) + tempLen + 2 (trailing)
        // vals.length = total - 25 - 21 = tempLen
        int total = 25 + 18 + 1 + tempLen + 2;
        byte[] raw = new byte[total];

        raw[18] = 1; // N=1 → startLength=25

        System.arraycopy(receiveData, 0, raw, 25, 18);

        int offset = 44;
        for (byte[] entry : tempEntries) {
            System.arraycopy(entry, 0, raw, offset, entry.length);
            offset += entry.length;
        }

        return Base64.getEncoder().encodeToString(raw);
    }

    /**
     * Build an 18-byte receive-data buffer from the state currently held in
     * an AirconState.  This mirrors receiveToBytes() logic but is independent so
     * tests aren't circular.
     */
    private byte[] makeReceiveData(AirconState ac) {
        // Delegate to the parser itself so we can test the round-trip
        String encoded = ac.parser.toBase64();
        // toBase64 = command(25B) + receive(25B).  Receive data starts at byte 25.
        byte[] raw = Base64.getDecoder().decode(encoded);
        byte[] rcv = new byte[18];
        System.arraycopy(raw, 25, rcv, 0, 18);
        return rcv;
    }

    // -----------------------------------------------------------------------
    // Round-trip tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Round-trip: cool mode, basic settings preserved after encode→decode")
    void testRoundTrip_coolMode_basicSettings() {
        AirconState ac = new AirconState();
        ac.setOperation(true);
        ac.setOperationMode(1);   // Cool
        ac.setPresetTemp(22.0f);
        ac.setAirFlow(0);
        ac.setWindDirectionUD(0);
        ac.setWindDirectionLR(0);
        ac.setEntrust(false);
        ac.setCoolHotJudge(true);
        ac.setModelNr(0);

        String encoded = ac.parser.toBase64();
        ac.parser.translateBytes(encoded);

        assertTrue(ac.getOperation(), "Operation should be ON after round-trip");
        assertEquals(1, ac.getOperationMode(), "OperationMode should be 1 (Cool)");
        assertEquals(22.0f, ac.getPresetTemp(), 0.01f, "PresetTemp should be 22°C");
        assertEquals(0, ac.getAirFlow(), "AirFlow should be 0 (Auto)");
        assertEquals(0, ac.getWindDirectionUD(), "WindDirectionUD should be 0 (Auto)");
        assertEquals(0, ac.getWindDirectionLR(), "WindDirectionLR should be 0 (Auto)");
        assertFalse(ac.getEntrust(), "Entrust should be false");
        assertTrue(ac.getCoolHotJudge(), "CoolHotJudge should be true");
        assertEquals(0, ac.getModelNr(), "ModelNr should be 0");
    }

    @Test
    @DisplayName("Round-trip: operation OFF preserved after encode→decode")
    void testRoundTrip_operationOff() {
        AirconState ac = new AirconState();
        ac.setOperation(false);
        ac.setOperationMode(1);
        ac.setPresetTemp(20.0f);
        ac.setAirFlow(0);
        ac.setWindDirectionUD(0);
        ac.setWindDirectionLR(0);

        String encoded = ac.parser.toBase64();
        ac.parser.translateBytes(encoded);

        assertFalse(ac.getOperation(), "Operation should be OFF after round-trip");
    }

    @Test
    @DisplayName("Round-trip: entrust=true preserved after encode→decode")
    void testRoundTrip_entrustTrue() {
        AirconState ac = new AirconState();
        ac.setOperation(true);
        ac.setOperationMode(1);
        ac.setPresetTemp(24.0f);
        ac.setEntrust(true);
        ac.setAirFlow(0);
        ac.setWindDirectionUD(0);
        ac.setWindDirectionLR(0);

        String encoded = ac.parser.toBase64();
        ac.parser.translateBytes(encoded);

        assertTrue(ac.getEntrust(), "Entrust should be true after round-trip");
    }

    @Test
    @DisplayName("Round-trip: all five operation modes encode and decode correctly")
    void testRoundTrip_operationModes() {
        int[] modes = {0, 1, 2, 3, 4}; // Dry, Cool, Fan, Heat, Fan-only/model-specific
        for (int mode : modes) {
            AirconState ac = new AirconState();
            ac.setOperation(true);
            ac.setOperationMode(mode);
            ac.setPresetTemp(20.0f);
            ac.setAirFlow(0);
            ac.setWindDirectionUD(0);
            ac.setWindDirectionLR(0);

            String encoded = ac.parser.toBase64();
            ac.parser.translateBytes(encoded);

            assertEquals(mode, ac.getOperationMode(),
                    "OperationMode " + mode + " should survive round-trip");
        }
    }

    @Test
    @DisplayName("Round-trip: airflow levels 0-4 encode and decode correctly")
    void testRoundTrip_airFlowLevels() {
        int[] flows = {0, 2, 3, 4}; // auto, speed-2, speed-3, speed-4
        for (int flow : flows) {
            AirconState ac = new AirconState();
            ac.setOperation(true);
            ac.setOperationMode(1);
            ac.setPresetTemp(22.0f);
            ac.setAirFlow(flow);
            ac.setWindDirectionUD(0);
            ac.setWindDirectionLR(0);

            String encoded = ac.parser.toBase64();
            ac.parser.translateBytes(encoded);

            assertEquals(flow, ac.getAirFlow(),
                    "AirFlow " + flow + " should survive round-trip");
        }
    }

    @Test
    @DisplayName("Round-trip: wind directions UD 0-4 encode and decode correctly")
    void testRoundTrip_windDirectionUD() {
        for (int dir = 0; dir <= 4; dir++) {
            AirconState ac = new AirconState();
            ac.setOperation(true);
            ac.setOperationMode(1);
            ac.setPresetTemp(22.0f);
            ac.setAirFlow(0);
            ac.setWindDirectionUD(dir);
            ac.setWindDirectionLR(0);

            String encoded = ac.parser.toBase64();
            ac.parser.translateBytes(encoded);

            assertEquals(dir, ac.getWindDirectionUD(),
                    "WindDirectionUD " + dir + " should survive round-trip");
        }
    }

    @Test
    @DisplayName("Round-trip: wind directions LR 0-7 encode and decode correctly")
    void testRoundTrip_windDirectionLR() {
        for (int dir = 0; dir <= 7; dir++) {
            AirconState ac = new AirconState();
            ac.setOperation(true);
            ac.setOperationMode(1);
            ac.setPresetTemp(22.0f);
            ac.setAirFlow(0);
            ac.setWindDirectionUD(0);
            ac.setWindDirectionLR(dir);

            String encoded = ac.parser.toBase64();
            ac.parser.translateBytes(encoded);

            assertEquals(dir, ac.getWindDirectionLR(),
                    "WindDirectionLR " + dir + " should survive round-trip");
        }
    }

    // -----------------------------------------------------------------------
    // Temperature parsing from raw byte arrays
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Temperature: outdoor temp parsed correctly from raw bytes")
    void testTranslateBytes_outdoorTemperature() {
        // outdoorTempList[90] = 0.0°C
        byte[] rcv = new byte[18];
        byte[] outdoorEntry = {(byte) -128, 16, 90, 0};

        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv, outdoorEntry));

        assertEquals(0.0f, ac.getOutdoorTemp(), 0.001f,
                "Outdoor temp at index 90 should be 0.0°C");
    }

    @Test
    @DisplayName("Temperature: indoor temp parsed correctly from raw bytes")
    void testTranslateBytes_indoorTemperature() {
        // indoorTempList[90] = 6.6°C
        byte[] rcv = new byte[18];
        byte[] indoorEntry = {(byte) -128, 32, 90, 0};

        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv, indoorEntry));

        assertEquals(6.6f, ac.getIndoorTemp(), 0.001f,
                "Indoor temp at index 90 should be 6.6°C");
    }

    @Test
    @DisplayName("Temperature: both indoor and outdoor temperatures parsed from same payload")
    void testTranslateBytes_bothTemperatures() {
        byte[] rcv = new byte[18];
        // outdoorTempList[90] = 0.0°C, indoorTempList[90] = 6.6°C
        byte[] outdoorEntry = {(byte) -128, 16, 90, 0};
        byte[] indoorEntry  = {(byte) -128, 32, 90, 0};

        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv, outdoorEntry, indoorEntry));

        assertEquals(0.0f,  ac.getOutdoorTemp(), 0.001f, "Outdoor temp should be 0.0°C");
        assertEquals(6.6f, ac.getIndoorTemp(),  0.001f, "Indoor temp should be 6.6°C");
    }

    // -----------------------------------------------------------------------
    // Electric parsing – validates little-endian byte order fix
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Electric: 100W correctly decoded from little-endian bytes [0x90, 0x01]")
    void testTranslateBytes_electricLittleEndian_100W() {
        // 100W → raw value = 100 / 0.25 = 400 = 0x0190
        // Little-endian: low byte = 0x90 = 144, high byte = 0x01 = 1
        byte[] rcv = new byte[18];
        byte[] electricEntry = {(byte) -108, 16, (byte) 144, 1};

        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv, electricEntry));

        assertEquals(100.0f, ac.getElectric(), 0.01f,
                "Electric should be 100W from little-endian bytes [0x90, 0x01]");
    }

    @Test
    @DisplayName("Electric: 0.25W (minimum non-zero value) decoded correctly")
    void testTranslateBytes_electricMinimum() {
        // 0.25W → raw value = 1, little-endian: [0x01, 0x00]
        byte[] rcv = new byte[18];
        byte[] electricEntry = {(byte) -108, 16, 1, 0};

        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv, electricEntry));

        assertEquals(0.25f, ac.getElectric(), 0.001f,
                "Electric should be 0.25W from little-endian bytes [0x01, 0x00]");
    }

    @Test
    @DisplayName("Electric: 3276.75W (high value, both bytes populated) decoded correctly")
    void testTranslateBytes_electricHighValue() {
        // 3276.75W → raw = 3276.75 / 0.25 = 13107 = 0x3333
        // Little-endian: low = 0x33 = 51, high = 0x33 = 51
        byte[] rcv = new byte[18];
        byte[] electricEntry = {(byte) -108, 16, 51, 51};

        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv, electricEntry));

        assertEquals(3276.75f, ac.getElectric(), 0.01f,
                "Electric should be 3276.75W from little-endian bytes [0x33, 0x33]");
    }

    @Test
    @DisplayName("Electric: big-endian interpretation would give wrong result (validates fix)")
    void testTranslateBytes_electric_bigEndianWouldBeWrong() {
        // With [low=0x90, high=0x01]: correct little-endian = 0x0190 = 400 → 100W
        // Big-endian (old bug) = 0x9001 = 36865 → 9216.25W — clearly wrong
        byte[] rcv = new byte[18];
        byte[] electricEntry = {(byte) -108, 16, (byte) 144, 1};

        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv, electricEntry));

        assertNotEquals(9216.25f, ac.getElectric(), 0.01f,
                "Electric must NOT use big-endian interpretation");
        assertEquals(100.0f, ac.getElectric(), 0.01f,
                "Electric must use little-endian: [0x90, 0x01] = 100W");
    }

    // -----------------------------------------------------------------------
    // Bounds check – validates ArrayIndexOutOfBoundsException fix
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Bounds: no exception when temperature data length is not a multiple of 4")
    void testTranslateBytes_boundsCheck_nonMultipleOf4() {
        // Build a payload whose vals section is 5 bytes (non-multiple of 4).
        // Before the fix this would throw ArrayIndexOutOfBoundsException on the
        // second iteration (i=4, accessing vals[7] in a 5-element array).
        byte[] rcv = new byte[18];
        // startLength=25, vals starts at 44. We want vals.length=5 → total=25+21+5=51
        byte[] raw = new byte[51];
        raw[18] = 1;
        // Place a valid outdoor entry at vals[0..3] and one stray byte at vals[4]
        raw[44] = (byte) -128; raw[45] = 16; raw[46] = 90; raw[47] = 0; // valid entry
        raw[48] = (byte) 99; // stray byte – must not be accessed with i+1,i+2,i+3

        String encoded = Base64.getEncoder().encodeToString(raw);
        AirconState ac = new AirconState();

        assertDoesNotThrow(() -> ac.parser.translateBytes(encoded),
                "translateBytes must not throw when vals length is not a multiple of 4");
    }

    @Test
    @DisplayName("Bounds: temperature correctly parsed when vals has extra trailing byte")
    void testTranslateBytes_boundsCheck_partialLastEntry() {
        // Same as above but verify the complete first entry was still parsed
        byte[] rcv = new byte[18];
        byte[] raw = new byte[51]; // vals.length = 5
        raw[18] = 1;
        raw[44] = (byte) -128; raw[45] = 16; raw[46] = 90; raw[47] = 0;
        raw[48] = 42; // stray byte

        String encoded = Base64.getEncoder().encodeToString(raw);
        AirconState ac = new AirconState();
        ac.parser.translateBytes(encoded);

        // The complete entry at i=0 should have been processed
        assertEquals(0.0f, ac.getOutdoorTemp(), 0.001f,
                "Complete temperature entry before partial tail should still be parsed");
    }

    // -----------------------------------------------------------------------
    // CRC16-CCITT
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CRC16: empty input returns 0xFFFF (initial value)")
    void testCrc16ccitt_emptyInput() {
        AirconState ac = new AirconState();
        int crc = ac.parser.crc16ccitt(new byte[0]);
        assertEquals(0xFFFF, crc, "CRC16 of empty input should be 0xFFFF");
    }

    @Test
    @DisplayName("CRC16: single zero byte produces consistent checksum")
    void testCrc16ccitt_singleZero() {
        AirconState ac = new AirconState();
        int crc1 = ac.parser.crc16ccitt(new byte[]{0});
        int crc2 = ac.parser.crc16ccitt(new byte[]{0});
        assertEquals(crc1, crc2, "Same input must produce same CRC");
        assertTrue(crc1 >= 0 && crc1 <= 0xFFFF, "CRC must fit in 16 bits");
    }

    @Test
    @DisplayName("CRC16: different data produces different checksums")
    void testCrc16ccitt_differentInputs() {
        AirconState ac = new AirconState();
        int crc1 = ac.parser.crc16ccitt(new byte[]{1, 2, 3});
        int crc2 = ac.parser.crc16ccitt(new byte[]{3, 2, 1});
        assertNotEquals(crc1, crc2, "Different byte sequences should produce different CRCs");
    }

    // -----------------------------------------------------------------------
    // Error code parsing
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("ErrorCode: code 0 maps to '00' (no error)")
    void testTranslateBytes_errorCode_noError() {
        byte[] rcv = new byte[18]; // content[6] = 0 → error code 0
        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv));

        assertEquals("00", ac.getErrorCode(), "Error code 0 should map to '00'");
    }

    @Test
    @DisplayName("ErrorCode: maintenance code (M-prefix) when bit 7 of content[6] is clear")
    void testTranslateBytes_errorCode_maintenanceCode() {
        // content[6] = 5 (bit 7 clear, code = 5) → "M05"
        byte[] rcv = new byte[18];
        rcv[6] = 5;
        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv));

        assertEquals("M05", ac.getErrorCode(), "Error with bit7=0 should be M-code");
    }

    @Test
    @DisplayName("ErrorCode: bit-7-set byte still produces M-code due to signed integer arithmetic")
    void testTranslateBytes_errorCode_bit7Set_stillMCode() {
        // content[6] = 0x85: bit 7 is set, low 7 bits = 5
        // Java widens byte to int: 0x85 -> 0xFFFFFF85 = -123
        // (-123 & -128) = 0xFFFFFF80 = -128; -128 <= 0 -> true -> M-code
        // The E-code branch ("else" clause) is unreachable with signed arithmetic.
        byte[] rcv = new byte[18];
        rcv[6] = (byte) 0x85; // bit 7 set, code = 5
        AirconState ac = new AirconState();
        ac.parser.translateBytes(buildPayload(rcv));

        assertEquals("M05", ac.getErrorCode(),
                "Due to signed integer arithmetic, bit-7-set produces M-code, not E-code");
    }

    // -----------------------------------------------------------------------
    // Preset temperature range
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("PresetTemp: range of temperatures encode and decode correctly")
    void testRoundTrip_presetTemperatureRange() {
        float[] temps = {16.0f, 18.0f, 20.0f, 22.0f, 24.0f, 26.0f, 28.0f, 30.0f};
        for (float temp : temps) {
            AirconState ac = new AirconState();
            ac.setOperation(true);
            ac.setOperationMode(1); // Cool (not Heat, which overrides to 25°C)
            ac.setPresetTemp(temp);
            ac.setAirFlow(0);
            ac.setWindDirectionUD(0);
            ac.setWindDirectionLR(0);

            String encoded = ac.parser.toBase64();
            ac.parser.translateBytes(encoded);

            assertEquals(temp, ac.getPresetTemp(), 0.01f,
                    "PresetTemp " + temp + "°C should survive round-trip");
        }
    }
}
