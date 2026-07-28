package org.openhab.binding.mhi.internal.protocol;

import java.time.LocalDateTime;
import java.util.Base64;

public class AirconState {

    public final Object lock = new Object();

    // ----------------// Attribute definitions //----------------//
    public RacParser parser = new RacParser();

    private String hostname = "127.0.0.1";
    private String port = "5443";
    private String DeviceID = "f9276726d8e7";
    private String OperatorID = "openhab";
    public String name ="";
    private String AirConID = "f9276726d8e7";
    private String airconLibraryError="";
    

    boolean status = false;
    String firmware;
    int connectedAccounts = -1;

    private boolean outdoorTemperature;
    private Boolean Operation = false;
    private int OperationMode = -1;
    private int AirFlow = -1;
    private int WindDirectionUD = -1;
    private int WindDirectionLR = -1;
    private float PresetTemp;
    private boolean Entrust;
    private int ModelNr;
    private boolean Vacant;
    private boolean CoolHotJudge;

    private float indoorTemp = -100.00f;
    private float outdoorTemp = -100.00f;
    private float electric = -1.0f;
    private String errorCode;
    

    private boolean selfCleanOperation = false;
    private boolean selfCleanReset = false;

    private LocalDateTime nextRequestAfter;
    private long minrefreshRate = 1L;

    // ----------------// Synchronised methods for getting and setting variables, to
    // add some basic level of thread safety. //----------------//

    public String gethostname() {
        synchronized (lock) {
            return hostname;
        }
    }

    
    public String getName() {
        synchronized (lock) {
            return name;
        }
    }

    public String getairconLibraryError() {
        synchronized (lock) {
            return airconLibraryError;
        }
    }

    public boolean setairconLibraryError(String airconLibraryError) {
        synchronized (lock) {
            if (!this.airconLibraryError.equals(airconLibraryError)) {
                this.airconLibraryError = airconLibraryError;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean sethostname(String hostname) {
        synchronized (lock) {
            if (!this.hostname.equals(hostname)) {
                this.hostname = hostname;
                return true;
            } else {
                return false;
            }
        }
    }

    public String getport() {
        synchronized (lock) {
            return port;
        }
    }

    public boolean setport(String port) {
        synchronized (lock) {
            if (this.port == null || !this.port.equals(port)) {
                this.port = port;
                return true;
            } else {
                return false;
            }
        }
    }

    public String getDeviceID() {
        synchronized (lock) {
            return DeviceID;
        }
    }

    public boolean setDeviceID(String DeviceID) {
        synchronized (lock) {
            if (this.DeviceID == null || !this.DeviceID.equals(DeviceID)) {
                this.DeviceID = DeviceID;
                return true;
            } else {
                return false;
            }
        }
    }

    public String getOperatorID() {
        synchronized (lock) {
            return OperatorID;
        }
    }

    public boolean setOperatorID(String OperatorID) {
        synchronized (lock) {
            if (this.OperatorID == null || !this.OperatorID.equals(OperatorID)) {
                this.OperatorID = OperatorID;
                return true;
            } else {
                return false;
            }
        }
    }

    public String getAirConID() {
        synchronized (lock) {
            return AirConID;
        }
    }

    public boolean setAirConID(String AirConID) {
        synchronized (lock) {
            if (this.AirConID == null || !this.AirConID.equals(AirConID)) {
                this.AirConID = AirConID;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean getstatus() {
        synchronized (lock) {
            return status;
        }
    }

    public boolean setstatus(boolean status) {
        synchronized (lock) {
            if (this.status != status) {
                this.status = status;
                return true;
            } else {
                return false;
            }
        }
    }

    public String getfirmware() {
        synchronized (lock) {
            return firmware;
        }
    }

    public boolean setfirmware(String firmware) {
        synchronized (lock) {
            if (this.firmware == null || !this.firmware.equals(firmware)) {
                this.firmware = firmware;
                return true;
            } else {
                return false;
            }
        }
    }

    public int getconnectedAccounts() {
        synchronized (lock) {
            return connectedAccounts;
        }
    }

    public boolean setconnectedAccounts(int connectedAccounts) {
        synchronized (lock) {
            if (this.connectedAccounts == -1 || this.connectedAccounts != connectedAccounts) {
                this.connectedAccounts = connectedAccounts;
                return true;
            } else {
                return false;
            }
        }
    }

   

    public boolean setOutdoorTemperature(boolean outdoorTemperature) {
        synchronized (lock) {
            if (this.outdoorTemperature != outdoorTemperature) {
                this.outdoorTemperature = outdoorTemperature;
                return true;
            } else {
                return false;
            }
        }
    }

    public Boolean getOperation() {
        synchronized (lock) {
            return Operation;
        }
    }

    public boolean setOperation(Boolean operation) {
        synchronized (lock) {
            if (!this.Operation.equals(operation)) {
                this.Operation = operation;
                return true;
            } else {
                return false;
            }
        }
    }

    public int getOperationMode() {
        synchronized (lock) {
            return OperationMode;
        }
    }

    public boolean setOperationMode(int operationMode) {
        synchronized (lock) {
            if (this.OperationMode != operationMode) {
                this.OperationMode = operationMode;
                return true;
            } else {
                return false;
            }
        }
    }

    public int getAirFlow() {
        synchronized (lock) {
            return AirFlow;
        }
    }

    public boolean setAirFlow(int airFlow) {
        synchronized (lock) {
            if (this.AirFlow != airFlow) {
                this.AirFlow = airFlow;
                return true;
            } else {
                return false;
            }
        }
    }

    public int getWindDirectionUD() {
        synchronized (lock) {
            return WindDirectionUD;
        }
    }

    public boolean setWindDirectionUD(int windDirectionUD) {
        synchronized (lock) {
            if (this.WindDirectionUD != windDirectionUD) {
                this.WindDirectionUD = windDirectionUD;
                return true;
            } else {
                return false;
            }
        }
    }

    public int getWindDirectionLR() {
        synchronized (lock) {
            return WindDirectionLR;
        }
    }

    public boolean setWindDirectionLR(int windDirectionLR) {
        synchronized (lock) {
            if (this.WindDirectionLR != windDirectionLR) {
                this.WindDirectionLR = windDirectionLR;
                return true;
            } else {
                return false;
            }
        }
    }

    public float getPresetTemp() {
        synchronized (lock) {
            return PresetTemp;
        }
    }

    public boolean setPresetTemp(float presetTemp) {
        synchronized (lock) {
            if (this.PresetTemp != presetTemp) {
                this.PresetTemp = presetTemp;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean getEntrust() {
        synchronized (lock) {
            return Entrust;
        }
    }

    public boolean setEntrust(boolean entrust) {
        synchronized (lock) {
            if (this.Entrust != entrust) {
                this.Entrust = entrust;
                return true;
            } else {
                return false;
            }
        }
    }

    public int getModelNr() {
        synchronized (lock) {
            return ModelNr;
        }
    }

    public boolean setModelNr(int modelNr) {
        synchronized (lock) {
            if (this.ModelNr != modelNr) {
                this.ModelNr = modelNr;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean getVacant() {
        synchronized (lock) {
            return Vacant;
        }
    }

    public boolean setVacant(boolean vacant) {
        synchronized (lock) {
            if (this.Vacant != vacant) {
                this.Vacant = vacant;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean getCoolHotJudge() {
        synchronized (lock) {
            return CoolHotJudge;
        }
    }

    public boolean setCoolHotJudge(boolean coolHotJudge) {
        synchronized (lock) {
            if (this.CoolHotJudge != coolHotJudge) {
                this.CoolHotJudge = coolHotJudge;
                return true;
            } else {
                return false;
            }
        }
    }

    public float getIndoorTemp() {
        synchronized (lock) {
            return indoorTemp;
        }
    }

    public boolean setIndoorTemp(float indoorTemp) {
        synchronized (lock) {
            if (this.indoorTemp != indoorTemp) {
                this.indoorTemp = indoorTemp;
                return true;
            } else {
                return false;
            }
        }
    }

    public float getOutdoorTemp() {
        synchronized (lock) {
            return outdoorTemp;
        }
    }

    public boolean setOutdoorTemp(float outdoorTemp) {
        synchronized (lock) {
            if (this.outdoorTemp != outdoorTemp) {
                this.outdoorTemp = outdoorTemp;
                return true;
            } else {
                return false;
            }
        }
    }

    public float getElectric() {
        synchronized (lock) {
            return electric;
        }
    }

    public boolean setElectric(float electric) {
        synchronized (lock) {
            if (this.electric != electric) {
                this.electric = electric;
                return true;
            } else {
                return false;
            }
        }
    }

    public String getErrorCode() {
        synchronized (lock) {
            return errorCode;
        }
    }

    public boolean setErrorCode(String errorCode) {
        synchronized (lock) {
            if (this.errorCode == null || !this.errorCode.equals(errorCode)) {
                this.errorCode = errorCode;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isSelfCleanOperation() {
        synchronized (lock) {
            return selfCleanOperation;
        }
    }

    public boolean setSelfCleanOperation(boolean selfCleanOperation) {
        synchronized (lock) {
            if (this.selfCleanOperation != selfCleanOperation) {
                this.selfCleanOperation = selfCleanOperation;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isSelfCleanReset() {
        synchronized (lock) {
            return selfCleanReset;
        }
    }

    public boolean setSelfCleanReset(boolean selfCleanReset) {
        synchronized (lock) {
            if (this.selfCleanReset != selfCleanReset) {
                this.selfCleanReset = selfCleanReset;
                return true;
            } else {
                return false;
            }
        }
    }

    public LocalDateTime getNextRequestAfter() {
        synchronized (lock) {
            return nextRequestAfter;
        }
    }

    public boolean setNextRequestAfter(LocalDateTime nextRequestAfter) {
        synchronized (lock) {
            if (this.nextRequestAfter == null || !this.nextRequestAfter.equals(nextRequestAfter)) {
                this.nextRequestAfter = nextRequestAfter;
                return true;
            } else {
                return false;
            }
        }
    }

    public long getMinRefreshRate() {
        synchronized (lock) {
            return minrefreshRate;
        }
    }

    public boolean setMinRefreshRate(long minrefreshRate) {
        synchronized (lock) {
            if (this.minrefreshRate != minrefreshRate) {
                this.minrefreshRate = minrefreshRate;
                return true;
            } else {
                return false;
            }
        }
    }

    // ----------------// Parser Code //----------------//

    // --------// Parser Maps // --------//
    public static Float[] outdoorTempList = { -50.0f, -50.0f, -50.0f, -50.0f, -50.0f, -48.9f, -46.0f, -44.0f, -42.0f,
            -41.0f, -39.0f, -38.0f, -37.0f, -36.0f, -35.0f, -34.0f, -33.0f, -32.0f, -31.0f, -30.0f, -29.0f, -28.5f,
            -28.0f, -27.0f, -26.0f, -25.5f, -25.0f, -24.0f, -23.5f, -23.0f, -22.5f, -22.0f, -21.5f, -21.0f, -20.5f,
            -20.0f, -19.5f, -19.0f, -18.5f, -18.0f, -17.5f, -17.0f, -16.5f, -16.0f, -15.5f, -15.0f, -14.6f, -14.3f,
            -14.0f, -13.5f, -13.0f, -12.6f, -12.3f, -12.0f, -11.5f, -11.0f, -10.6f, -10.3f, -10.0f, -9.6f, -9.3f, -9.0f,
            -8.6f, -8.3f, -8.0f, -7.6f, -7.3f, -7.0f, -6.6f, -6.3f, -6.0f, -5.6f, -5.3f, -5.0f, -4.6f, -4.3f, -4.0f,
            -3.7f, -3.5f, -3.2f, -3.0f, -2.6f, -2.3f, -2.0f, -1.7f, -1.5f, -1.2f, -1.0f, -0.6f, -0.3f, 0.0f, 0.2f, 0.5f,
            0.7f, 1.0f, 1.3f, 1.6f, 2.0f, 2.2f, 2.5f, 2.7f, 3.0f, 3.2f, 3.5f, 3.7f, 4.0f, 4.2f, 4.5f, 4.7f, 5.0f, 5.2f,
            5.5f, 5.7f, 6.0f, 6.2f, 6.5f, 6.7f, 7.0f, 7.2f, 7.5f, 7.7f, 8.0f, 8.2f, 8.5f, 8.7f, 9.0f, 9.2f, 9.5f, 9.7f,
            10.0f, 10.2f, 10.5f, 10.7f, 11.0f, 11.2f, 11.5f, 11.7f, 12.0f, 12.2f, 12.5f, 12.7f, 13.0f, 13.2f, 13.5f,
            13.7f, 14.0f, 14.2f, 14.4f, 14.6f, 14.8f, 15.0f, 15.2f, 15.5f, 15.7f, 16.0f, 16.2f, 16.5f, 16.7f, 17.0f,
            17.2f, 17.5f, 17.7f, 18.0f, 18.2f, 18.5f, 18.7f, 19.0f, 19.2f, 19.4f, 19.6f, 19.8f, 20.0f, 20.2f, 20.5f,
            20.7f, 21.0f, 21.2f, 21.5f, 21.7f, 22.0f, 22.2f, 22.5f, 22.7f, 23.0f, 23.2f, 23.5f, 23.7f, 24.0f, 24.2f,
            24.5f, 24.7f, 25.0f, 25.2f, 25.5f, 25.7f, 26.0f, 26.2f, 26.5f, 26.7f, 27.0f, 27.2f, 27.5f, 27.7f, 28.0f,
            28.2f, 28.5f, 28.7f, 29.0f, 29.2f, 29.5f, 29.7f, 30.0f, 30.2f, 30.5f, 30.7f, 31.0f, 31.3f, 31.6f, 32.0f,
            32.2f, 32.5f, 32.7f, 33.0f, 33.2f, 33.5f, 33.7f, 34.0f, 34.3f, 34.6f, 35.0f, 35.2f, 35.5f, 35.7f, 36.0f,
            36.3f, 36.6f, 37.0f, 37.2f, 37.5f, 37.7f, 38.0f, 38.3f, 38.6f, 39.0f, 39.3f, 39.6f, 40.0f, 40.3f, 40.6f,
            41.0f, 41.3f, 41.6f, 42.0f, 42.3f, 42.6f, 43.0f };
    public static Float[] indoorTempList = { -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f,
            -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -29.0f, -28.0f, -27.0f, -26.0f, -25.0f, -24.0f,
            -23.0f, -22.5f, -22.0f, -21.0f, -20.0f, -19.5f, -19.0f, -18.0f, -17.5f, -17.0f, -16.5f, -16.0f, -15.0f,
            -14.5f, -14.0f, -13.5f, -13.0f, -12.5f, -12.0f, -11.5f, -11.0f, -10.5f, -10.0f, -9.5f, -9.0f, -8.6f, -8.3f,
            -8.0f, -7.5f, -7.0f, -6.5f, -6.0f, -5.6f, -5.3f, -5.0f, -4.5f, -4.0f, -3.6f, -3.3f, -3.0f, -2.6f, -2.3f,
            -2.0f, -1.6f, -1.3f, -1.0f, -0.5f, 0.0f, 0.3f, 0.6f, 1.0f, 1.3f, 1.6f, 2.0f, 2.3f, 2.6f, 3.0f, 3.2f, 3.5f,
            3.7f, 4.0f, 4.3f, 4.6f, 5.0f, 5.3f, 5.6f, 6.0f, 6.3f, 6.6f, 7.0f, 7.2f, 7.5f, 7.7f, 8.0f, 8.3f, 8.6f, 9.0f,
            9.2f, 9.5f, 9.7f, 10.0f, 10.3f, 10.6f, 11.0f, 11.2f, 11.5f, 11.7f, 12.0f, 12.3f, 12.6f, 13.0f, 13.2f, 13.5f,
            13.7f, 14.0f, 14.2f, 14.5f, 14.7f, 15.0f, 15.3f, 15.6f, 16.0f, 16.2f, 16.5f, 16.7f, 17.0f, 17.2f, 17.5f,
            17.7f, 18.0f, 18.2f, 18.5f, 18.7f, 19.0f, 19.2f, 19.5f, 19.7f, 20.0f, 20.2f, 20.5f, 20.7f, 21.0f, 21.2f,
            21.5f, 21.7f, 22.0f, 22.2f, 22.5f, 22.7f, 23.0f, 23.2f, 23.5f, 23.7f, 24.0f, 24.2f, 24.5f, 24.7f, 25.0f,
            25.2f, 25.5f, 25.7f, 26.0f, 26.2f, 26.5f, 26.7f, 27.0f, 27.2f, 27.5f, 27.7f, 28.0f, 28.2f, 28.5f, 28.7f,
            29.0f, 29.2f, 29.5f, 29.7f, 30.0f, 30.2f, 30.5f, 30.7f, 31.0f, 31.3f, 31.6f, 32.0f, 32.2f, 32.5f, 32.7f,
            33.0f, 33.2f, 33.5f, 33.7f, 34.0f, 34.2f, 34.5f, 34.7f, 35.0f, 35.3f, 35.6f, 36.0f, 36.2f, 36.5f, 36.7f,
            37.0f, 37.2f, 37.5f, 37.7f, 38.0f, 38.3f, 38.6f, 39.0f, 39.2f, 39.5f, 39.7f, 40.0f, 40.3f, 40.6f, 41.0f,
            41.2f, 41.5f, 41.7f, 42.0f, 42.3f, 42.6f, 43.0f, 43.2f, 43.5f, 43.7f, 44.0f, 44.3f, 44.6f, 45.0f, 45.3f,
            45.6f, 46.0f, 46.2f, 46.5f, 46.7f, 47.0f, 47.3f, 47.6f, 48.0f, 48.3f, 48.6f, 49.0f, 49.3f, 49.6f, 50.0f,
            50.3f, 50.6f, 51.0f, 51.3f, 51.6f, 52.0f };

    // --------// Parser Class // --------//

    public class RacParser {

        public String toBase64() {
            byte[] command = addCrc16(addVariable(commandToByte(AirconState.this)));
            byte[] receive = addCrc16(addVariable(recieveToBytes()));

            byte[] combined = new byte[command.length + receive.length];
            System.arraycopy(command, 0, combined, 0, command.length);
            System.arraycopy(receive, 0, combined, command.length, receive.length);

            return Base64.getEncoder().encodeToString(combined);
        }

        public byte[] addVariable(byte[] byteBuffer) {
            byte[] variable = { 1, (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
            byte[] result = new byte[byteBuffer.length + variable.length];

            System.arraycopy(byteBuffer, 0, result, 0, byteBuffer.length);
            System.arraycopy(variable, 0, result, byteBuffer.length, variable.length);

            return result;
        }

        public byte[] commandToByte(AirconState airconStat) {
            byte[] statByte = new byte[18];
            statByte[5] = (byte) 255;

            // On/Off
            if (airconStat.getOperation()) {
                statByte[2] |= 3;
            } else {
                statByte[2] |= 2;
            }

            // Operating Mode
            switch (airconStat.getOperationMode()) {
                case 0:
                    statByte[2] |= 32;
                    break;
                case 1:
                    statByte[2] |= 40;
                    break;
                case 2:
                    statByte[2] |= 48;
                    break;
                case 3:
                    statByte[2] |= 44;
                    break;
                case 4:
                    statByte[2] |= 36;
                    break;
            }

            // Airflow
            switch (airconStat.getAirFlow()) {
                case 0:
                    statByte[3] |= 15;
                    break;
                case 1:
                    statByte[3] |= 8;
                    break;
                case 2:
                    statByte[3] |= 9;
                    break;
                case 3:
                    statByte[3] |= 10;
                    break;
                case 4:
                    statByte[3] |= 14;
                    break;
            }

            // Vertical wind direction
            switch (airconStat.getWindDirectionUD()) {
                case 0:
                    statByte[2] |= 192;
                    statByte[3] |= 128;
                    break;
                case 1:
                    statByte[2] |= 128;
                    statByte[3] |= 128;
                    break;
                case 2:
                    statByte[2] |= 128;
                    statByte[3] |= 144;
                    break;
                case 3:
                    statByte[2] |= 128;
                    statByte[3] |= 160;
                    break;
                case 4:
                    statByte[2] |= 128;
                    statByte[3] |= 176;
                    break;
            }

            // Horizontal wind direction
            switch (airconStat.getWindDirectionLR()) {
                case 0:
                    statByte[12] |= 3;
                    statByte[11] |= 16;
                    break;
                case 1:
                    statByte[12] |= 2;
                    statByte[11] |= 16;
                    break;
                case 2:
                    statByte[12] |= 2;
                    statByte[11] |= 17;
                    break;
                case 3:
                    statByte[12] |= 2;
                    statByte[11] |= 18;
                    break;
                case 4:
                    statByte[12] |= 2;
                    statByte[11] |= 19;
                    break;
                case 5:
                    statByte[12] |= 2;
                    statByte[11] |= 20;
                    break;
                case 6:
                    statByte[12] |= 2;
                    statByte[11] |= 21;
                    break;
                case 7:
                    statByte[12] |= 2;
                    statByte[11] |= 22;
                    break;
            }

            // Preset temp
            float presetTemp = airconStat.getOperationMode() == 3 ? 25.0f : airconStat.getPresetTemp();
            statByte[4] |= (int) (presetTemp / 0.5) + 128;

            // Entrust
            if (!airconStat.getEntrust()) {
                statByte[12] |= 8;
            } else {
                statByte[12] |= 12;
            }

            if (!airconStat.getCoolHotJudge()) {
                statByte[8] |= 8;
            }

            if (airconStat.getModelNr() == 1) {
                statByte[10] |= airconStat.getVacant() ? 1 : 0;
            }

            if (airconStat.getModelNr() != 1 && airconStat.getModelNr() != 2) {
                return statByte;
            }

            statByte[10] |= airconStat.isSelfCleanReset() ? 4 : 0;
            statByte[10] |= airconStat.isSelfCleanOperation() ? 144 : 128;

            return statByte;
        }

        public byte[] recieveToBytes() {
            byte[] statByte = new byte[18];
            statByte[5] = (byte) 255;

            // On/Off
            if (AirconState.this.getOperation()) {
                statByte[2] |= 1;
            }

            // Operating Mode
            switch (AirconState.this.getOperationMode()) {
                case 1:
                    statByte[2] |= 8;
                    break;
                case 2:
                    statByte[2] |= 16;
                    break;
                case 3:
                    statByte[2] |= 12;
                    break;
                case 4:
                    statByte[2] |= 4;
                    break;
            }

            // Airflow
            switch (AirconState.this.getAirFlow()) {
                case 0:
                    statByte[3] |= 7;
                    break;
                case 2:
                    statByte[3] |= 1;
                    break;
                case 3:
                    statByte[3] |= 2;
                    break;
                case 4:
                    statByte[3] |= 6;
                    break;
            }

            // Vertical wind direction
            switch (AirconState.this.getWindDirectionUD()) {
                case 0:
                    statByte[2] |= 64;
                    break;
                case 2:
                    statByte[3] |= 16;
                    break;
                case 3:
                    statByte[3] |= 32;
                    break;
                case 4:
                    statByte[3] |= 48;
                    break;
            }

            // Horizontal wind direction
            switch (AirconState.this.getWindDirectionLR()) {
                case 0:
                    statByte[12] |= 1;
                    break;
                case 1:
                    statByte[11] |= 0;
                    break;
                case 2:
                    statByte[11] |= 1;
                    break;
                case 3:
                    statByte[11] |= 2;
                    break;
                case 4:
                    statByte[11] |= 3;
                    break;
                case 5:
                    statByte[11] |= 4;
                    break;
                case 6:
                    statByte[11] |= 5;
                    break;
                case 7:
                    statByte[11] |= 6;
                    break;
            }

            // Preset temp
            float presetTemp = AirconState.this.getOperationMode() == 3 ? 25.0f : AirconState.this.getPresetTemp();
            statByte[4] |= (int) (presetTemp / 0.5);

            // Entrust
            if (AirconState.this.getEntrust()) {
                statByte[12] |= 4;
            }

            if (!AirconState.this.getCoolHotJudge()) {
                statByte[8] |= 8;
            }

            if (AirconState.this.getModelNr() == 1) {
                statByte[0] |= 1;
            } else if (AirconState.this.getModelNr() == 2) {
                statByte[0] |= 2;
            }

            if (AirconState.this.getModelNr() == 1) {
                statByte[10] |= AirconState.this.getVacant() ? 1 : 0;
            }

            if (AirconState.this.getModelNr() != 1 && AirconState.this.getModelNr() != 2) {
                return statByte;
            }

            statByte[15] |= AirconState.this.isSelfCleanOperation() ? 1 : 0;

            return statByte;
        }

        public AirconState translateBytes(String inputString) {
            // System.out.println(inputString);

            byte[] contentByteArray = Base64.getDecoder()
                    .decode(inputString.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            int startLength = contentByteArray[18] * 4 + 21;
            byte[] content = new byte[18];
            System.arraycopy(contentByteArray, startLength, content, 0, 18);

            AirconState.this.setOperation((3 & content[2]) == 1);
            AirconState.this.setPresetTemp(content[4] / 2.0f);
            AirconState.this.setOperationMode(findMatch(60 & content[2], 8, 16, 12, 4) + 1);
            AirconState.this.setAirFlow(findMatch(15 & content[3], 7, 0, 1, 2, 6));
            AirconState.this
                    .setWindDirectionUD((content[2] & 192) == 64 ? 0 : findMatch(240 & content[3], 0, 16, 32, 48) + 1);
            AirconState.this.setWindDirectionLR(
                    (content[12] & 3) == 1 ? 0 : findMatch(31 & content[11], 0, 1, 2, 3, 4, 5, 6) + 1);
            AirconState.this.setEntrust((12 & content[12]) == 4);
            AirconState.this.setCoolHotJudge((content[8] & 8) <= 0);
            AirconState.this.setModelNr(findMatch(content[0] & 127, 0, 1, 2));
            AirconState.this.setVacant((content[10] & 1) != 0);
            int code = content[6] & 127;
            AirconState.this.setErrorCode(
                    code == 0 ? "00" : (content[6] & -128) <= 0 ? "M" + String.format("%02d", code) : "E" + code);

            byte[] vals = new byte[contentByteArray.length - startLength - 21];
            System.arraycopy(contentByteArray, startLength + 19, vals, 0, vals.length);

            for (int i = 0; i + 3 < vals.length; i += 4) {
                if (vals[i] == -128 && vals[i + 1] == 16) {
                    AirconState.this.setOutdoorTemp(AirconState.outdoorTempList[vals[i + 2] & 0xFF]);
                }
                if (vals[i] == -128 && vals[i + 1] == 32) {
                    AirconState.this.setIndoorTemp(AirconState.indoorTempList[vals[i + 2] & 0xFF]);
                }
                if (vals[i] == -108 && vals[i + 1] == 16) {
                    // Little-endian 16-bit value, scaled by 0.25 to get watts
                    int value = ((vals[i + 3] & 0xFF) << 8) | (vals[i + 2] & 0xFF);
                    AirconState.this.setElectric(value * 0.25f);
                }
            }

            return AirconState.this;
        }

        public int crc16ccitt(byte[] data) {
            int crc = 0xFFFF;
            for (byte b : data) {
                for (int i = 0; i < 8; i++) {
                    boolean bit = ((b >> (7 - i) & 1) == 1);
                    boolean c15 = ((crc >> 15 & 1) == 1);
                    crc <<= 1;
                    if (c15 ^ bit)
                        crc ^= 0x1021;
                }
            }
            return crc & 0xFFFF;
        }

        public byte[] addCrc16(byte[] byteBuffer) {
            int crc = crc16ccitt(byteBuffer);
            byte[] result = new byte[byteBuffer.length + 2];
            System.arraycopy(byteBuffer, 0, result, 0, byteBuffer.length);
            result[result.length - 2] = (byte) (crc & 0xFF);
            result[result.length - 1] = (byte) ((crc >> 8) & 0xFF);
            return result;
        }

        private int findMatch(int value, int... matches) {
            for (int i = 0; i < matches.length; i++) {
                if (value == matches[i]) {
                    return i;
                }
            }
            return -1;
        }
    }

}
