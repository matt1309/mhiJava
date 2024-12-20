package com.thunderstorm.mhi;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        // Create object
        AirCon aircon = new AirCon();

        // Set hostname and port.
        aircon.sethostname("192.168.0.12");
        aircon.setport("5443");
        aircon.setDeviceID("openhabAC-con-device-1314abcdef1"); 
        aircon.setOperatorID("openhabAC-130995140699130995140699130995");

        try {
            // Get initial stats from aircon unit and parse into the aircon object.
            aircon.getAirconStats(false);
            // Berlow is the old way of, closer to how python managed it
            // aircon.parser.translateBytes(aircon.getAirconStats(false));
        } catch (Exception e) {

            System.out.println(e.toString());
        }

        // editing the operation data to on.
        aircon.setOperation(true); // turn on unit
        aircon.setOperationMode(1); // not Sure what operation modes are but they can be 0 to 4. Need to add
                                    // checking for this ie if not inside that range throw log error.
        aircon.setPresetTemp(21); // set Temp to 21.

        // Tranlsating command into base64 to send to unit. Might try consolidate this
        // into the sendAircoComand function. So parser is truly hidden from users of
        // object.
        String command = aircon.parser.toBase64();

        try {
            // sending command to the aircon unit itself
            String response = aircon.sendAircoCommand(command);

            // Similar to getAirconStats might be worth consolidating the translateBytes
            // into the sendAircoCommand
            aircon.parser.translateBytes(response);

        } catch (Exception e) {

            System.out.println(e.toString());
        }

    }
}