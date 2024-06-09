package com.thunderstorm.mhi;
import java.util.Base64;

public class RacParser {
    
    public String toBase64(AirCon airconStat) {
        byte[] command = addCrc16(addVariable(commandToByte(airconStat)));
        byte[] receive = addCrc16(addVariable(recieveToBytes(airconStat)));

        byte[] combined = new byte[command.length + receive.length];
        System.arraycopy(command, 0, combined, 0, command.length);
        System.arraycopy(receive, 0, combined, command.length, receive.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public byte[] addVariable(byte[] byteBuffer) {
        byte[] variable = {1, (byte) 255, (byte) 255, (byte) 255, (byte) 255};
        byte[] result = new byte[byteBuffer.length + variable.length];

        System.arraycopy(byteBuffer, 0, result, 0, byteBuffer.length);
        System.arraycopy(variable, 0, result, byteBuffer.length, variable.length);

        return result;
    }

    public byte[] commandToByte(AirCon airconStat) {
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
            case 0: statByte[2] |= 32; break;
            case 1: statByte[2] |= 40; break;
            case 2: statByte[2] |= 48; break;
            case 3: statByte[2] |= 44; break;
            case 4: statByte[2] |= 36; break;
        }

        // Airflow
        switch (airconStat.getAirFlow()) {
            case 0: statByte[3] |= 15; break;
            case 1: statByte[3] |= 8; break;
            case 2: statByte[3] |= 9; break;
            case 3: statByte[3] |= 10; break;
            case 4: statByte[3] |= 14; break;
        }

        // Vertical wind direction
        switch (airconStat.getWindDirectionUD()) {
            case 0: statByte[2] |= 192; statByte[3] |= 128; break;
            case 1: statByte[2] |= 128; statByte[3] |= 128; break;
            case 2: statByte[2] |= 128; statByte[3] |= 144; break;
            case 3: statByte[2] |= 128; statByte[3] |= 160; break;
            case 4: statByte[2] |= 128; statByte[3] |= 176; break;
        }

        // Horizontal wind direction
        switch (airconStat.getWindDirectionLR()) {
            case 0: statByte[12] |= 3; statByte[11] |= 16; break;
            case 1: statByte[12] |= 2; statByte[11] |= 16; break;
            case 2: statByte[12] |= 2; statByte[11] |= 17; break;
            case 3: statByte[12] |= 2; statByte[11] |= 18; break;
            case 4: statByte[12] |= 2; statByte[11] |= 19; break;
            case 5: statByte[12] |= 2; statByte[11] |= 20; break;
            case 6: statByte[12] |= 2; statByte[11] |= 21; break;
            case 7: statByte[12] |= 2; statByte[11] |= 22; break;
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

    public byte[] recieveToBytes(AirCon airconStat) {
        byte[] statByte = new byte[18];
        statByte[5] = (byte) 255;

        // On/Off
        if (airconStat.getOperation()) {
            statByte[2] |= 1;
        }

        // Operating Mode
        switch (airconStat.getOperationMode()) {
            case 1: statByte[2] |= 8; break;
            case 2: statByte[2] |= 16; break;
            case 3: statByte[2] |= 12; break;
            case 4: statByte[2] |= 4; break;
        }

        // Airflow
        switch (airconStat.getAirFlow()) {
            case 0: statByte[3] |= 7; break;
            case 2: statByte[3] |= 1; break;
            case 3: statByte[3] |= 2; break;
            case 4: statByte[3] |= 6; break;
        }

        // Vertical wind direction
        switch (airconStat.getWindDirectionUD()) {
            case 0: statByte[2] |= 64; break;
            case 2: statByte[3] |= 16; break;
            case 3: statByte[3] |= 32; break;
            case 4: statByte[3] |= 48; break;
        }

        // Horizontal wind direction
        switch (airconStat.getWindDirectionLR()) {
            case 0: statByte[12] |= 1; break;
            case 1: statByte[11] |= 0; break;
            case 2: statByte[11] |= 1; break;
            case 3: statByte[11] |= 2; break;
            case 4: statByte[11] |= 3; break;
            case 5: statByte[11] |= 4; break;
            case 6: statByte[11] |= 5; break;
            case 7: statByte[11] |= 6; break;
        }

        // Preset temp
        float presetTemp = airconStat.getOperationMode() == 3 ? 25.0f : airconStat.getPresetTemp();
        statByte[4] |= (int) (presetTemp / 0.5);

        // Entrust
        if (airconStat.getEntrust()) {
            statByte[12] |= 4;
        }

        if (!airconStat.getCoolHotJudge()) {
            statByte[8] |= 8;
        }

        if (airconStat.getModelNr() == 1) {
            statByte[0] |= 1;
        } else if (airconStat.getModelNr() == 2) {
            statByte[0] |= 2;
        }

        if (airconStat.getModelNr() == 1) {
            statByte[10] |= airconStat.getVacant() ? 1 : 0;
        }

        if (airconStat.getModelNr() != 1 && airconStat.getModelNr() != 2) {
            return statByte;
        }

        statByte[15] |= airconStat.isSelfCleanOperation() ? 1 : 0;

        return statByte;
    }

    public AirCon translateBytes(AirCon acDevice, String inputString) {
       // AirCon acDevice = new AirCon();

        byte[] contentByteArray = Base64.getDecoder().decode(inputString.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        int startLength = contentByteArray[18] * 4 + 21;
        byte[] content = new byte[18];
        System.arraycopy(contentByteArray, startLength, content, 0, 18);

        acDevice.setOperation((3 & content[2]) == 1);
        acDevice.setPresetTemp(content[4] / 2.0f);
        acDevice.setOperationMode(findMatch(60 & content[2], 8, 16, 12, 4) + 1);
        acDevice.setAirFlow(findMatch(15 & content[3], 7, 0, 1, 2, 6));
        acDevice.setWindDirectionUD((content[2] & 192) == 64 ? 0 : findMatch(240 & content[3], 0, 16, 32, 48) + 1);
        acDevice.setWindDirectionLR((content[12] & 3) == 1 ? 0 : findMatch(31 & content[11], 0, 1, 2, 3, 4, 5, 6) + 1);
        acDevice.setEntrust((12 & content[12]) == 4);
        acDevice.setCoolHotJudge((content[8] & 8) <= 0);
        acDevice.setModelNr(findMatch(content[0] & 127, 0, 1, 2));
        acDevice.setVacant((content[10] & 1) != 0);
        int code = content[6] & 127;
        acDevice.setErrorCode(code == 0 ? "00" : (content[6] & -128) <= 0 ? "M" + String.format("%02d", code) : "E" + code);

        byte[] vals = new byte[contentByteArray.length - startLength - 21];
        System.arraycopy(contentByteArray, startLength + 19, vals, 0, vals.length);

        for (int i = 0; i < vals.length; i += 4) {
            if (vals[i] == -128 && vals[i + 1] == 16) {
                acDevice.setOutdoorTemp(AirCon.outdoorTempList[vals[i + 2] & 0xFF]);
            }
            if (vals[i] == -128 && vals[i + 1] == 32) {
                acDevice.setIndoorTemp(AirCon.indoorTempList[vals[i + 2] & 0xFF]);
            }
            if (vals[i] == -108 && vals[i + 1] == 16) {
                float value = ((vals[i + 2] & 0xFF) << 8) | (vals[i + 3] & 0xFF);
                acDevice.setElectric(value * 0.25f);
            }
        }

        return acDevice;
    }

    public int crc16ccitt(byte[] data) {
        int crc = 0xFFFF;
        for (byte b : data) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= 0x1021;
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







